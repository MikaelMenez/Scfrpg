package com.escudomestre.digital.application

import com.escudomestre.digital.domain.model.EstadoCombate
import com.escudomestre.digital.domain.model.Item
import com.escudomestre.digital.domain.model.Magia
import com.escudomestre.digital.domain.model.Personagem
import com.escudomestre.digital.domain.model.TipoRolagem
import com.escudomestre.digital.domain.repository.HistoricoRepository
import com.escudomestre.digital.domain.repository.ItemRepository
import com.escudomestre.digital.domain.repository.MagiaRepository
import com.escudomestre.digital.domain.repository.PersonagemRepository
import com.escudomestre.digital.domain.service.MotorDeRegras
import com.escudomestre.digital.domain.service.Vantagem
import com.escudomestre.digital.infrastructure.persistence.DatabaseFactory
import com.escudomestre.digital.infrastructure.persistence.PersonagemDAO
import com.escudomestre.digital.infrastructure.persistence.repositories.ExposedHistoricoRepository
import com.escudomestre.digital.infrastructure.persistence.repositories.ExposedItemRepository
import com.escudomestre.digital.infrastructure.persistence.repositories.ExposedMagiaRepository
import com.escudomestre.digital.infrastructure.persistence.repositories.ExposedPersonagemRepository
import java.io.File
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SimuladorDeCombateTest {

    private lateinit var personagemRepository: PersonagemRepository
    private lateinit var itemRepository: ItemRepository
    private lateinit var magiaRepository: MagiaRepository
    private lateinit var historicoRepository: HistoricoRepository
    private var arquivoBanco: File? = null

    @BeforeTest
    fun setup() {
        arquivoBanco = File.createTempFile("escudo_simulador_", ".db")
        DatabaseFactory.init("jdbc:sqlite:${arquivoBanco!!.absolutePath}")
        personagemRepository = ExposedPersonagemRepository(PersonagemDAO())
        itemRepository = ExposedItemRepository()
        magiaRepository = ExposedMagiaRepository()
        historicoRepository = ExposedHistoricoRepository()
    }

    @AfterTest
    fun limpar() {
        arquivoBanco?.delete()
        arquivoBanco = null
    }

    private fun simulador(
        relogio: () -> Long = System::currentTimeMillis,
        motorDeRegras: MotorDeRegras = MotorDeRegras(),
    ): SimuladorDeCombate = SimuladorDeCombate(
        motorDeRegras = motorDeRegras,
        personagemRepository = personagemRepository,
        historicoRepository = historicoRepository,
        relogio = relogio,
    )

    private fun personagem(pvAtual: Int = 30, nome: String = "Aragorn"): Personagem =
        Personagem(
            nome = nome,
            raca = "Humano",
            classe = "Guerreiro",
            nivel = 3,
            pontosDeVidaAtual = pvAtual,
            pontosDeVidaMaximo = 30,
            classeArmadura = 16,
            modificadorAtaque = 5,
        )

    @Test
    fun `aplicar dano atualiza PV persiste e notifica a interface (AC 2 3)`() {
        val personagem = personagem().also { personagemRepository.criar(it) }
        val simulador = simulador()
        val notificados = mutableListOf<Personagem>()
        simulador.registrarObservador(ObservadorDeMudanca { notificados += it })

        simulador.processarDano(personagem, 12)

        assertEquals(18, personagem.pontosDeVidaAtual)
        assertEquals(18, personagemRepository.buscarPorId(personagem.id)?.pontosDeVidaAtual)
        assertEquals(1, notificados.size)
        assertEquals(18, notificados[0].pontosDeVidaAtual)
    }

    @Test
    fun `atualizacao de PV e notificada dentro do limite de 200ms (RNF02)`() {
        val personagem = personagem().also { personagemRepository.criar(it) }
        val simulador = simulador()
        val inicio = System.nanoTime()
        var notificado = false
        simulador.registrarObservador(ObservadorDeMudanca { notificado = true })

        simulador.processarDano(personagem, 5)

        val tempoMs = (System.nanoTime() - inicio) / 1_000_000.0
        assertTrue(notificado)
        assertTrue(tempoMs <= 200.0, "atualização de PV levou ${tempoMs}ms, acima do limite de 200ms")
    }

    @Test
    fun `processar dano notifica todos os observadores registrados`() {
        val personagem = personagem().also { personagemRepository.criar(it) }
        val simulador = simulador()
        var primeiro = 0
        var segundo = 0
        simulador.registrarObservador(ObservadorDeMudanca { primeiro++ })
        simulador.registrarObservador(ObservadorDeMudanca { segundo++ })

        simulador.processarDano(personagem, 3)

        assertEquals(1, primeiro)
        assertEquals(1, segundo)
    }

    @Test
    fun `observador removido deixa de receber notificacoes`() {
        val personagem = personagem().also { personagemRepository.criar(it) }
        val simulador = simulador()
        var contagem = 0
        val observador = ObservadorDeMudanca { contagem++ }
        simulador.registrarObservador(observador)

        simulador.processarDano(personagem, 3)
        simulador.removerObservador(observador)
        simulador.processarDano(personagem, 3)

        assertEquals(1, contagem)
    }

    @Test
    fun `dano que zera a vida marca o personagem como derrotado e notifica`() {
        val personagem = personagem(pvAtual = 10).also { personagemRepository.criar(it) }
        val simulador = simulador()
        var notificado: Personagem? = null
        simulador.registrarObservador(ObservadorDeMudanca { notificado = it })

        simulador.processarDano(personagem, 10)

        assertEquals(EstadoCombate.DERROTADO, personagem.estado)
        assertEquals(EstadoCombate.DERROTADO, notificado?.estado)
    }

    @Test
    fun `rolagem de ataque registra automaticamente no historico (AC 3 1)`() {
        val personagem = personagem().also { personagemRepository.criar(it) }
        val simulador = simulador()

        val resultado = simulador.rolarAtaque(personagem)

        val registros = historicoRepository.listarDaSessao()
        assertEquals(1, registros.size)
        assertEquals(personagem.id, registros[0].personagemId)
        assertEquals(TipoRolagem.ATACAR, registros[0].tipoRolagem)
        assertEquals(resultado.resultado, registros[0].resultado)
        assertTrue(registros[0].timestamp > 0)
    }

    @Test
    fun `rolagem de ataque soma o modificador de ataque definido no personagem (AC 2 1)`() {
        val personagem = personagem().also { personagemRepository.criar(it) }
        val simulador = simulador()

        val resultado = simulador.rolarAtaque(personagem, Vantagem.NENHUMA)

        assertTrue(resultado.resultado in 6..25, "resultado $resultado fora do intervalo 6..25")
    }

    @Test
    fun `rolagem de dano registra tipo DANO no historico`() {
        val personagem = personagem().also { personagemRepository.criar(it) }
        val simulador = simulador()

        val resultado = simulador.rolarDano(personagem, faces = 8, modificador = 2)

        val registros = historicoRepository.listarDaSessao()
        assertEquals(1, registros.size)
        assertEquals(TipoRolagem.DANO, registros[0].tipoRolagem)
        assertEquals(resultado.resultado, registros[0].resultado)
    }

    @Test
    fun `rolagem de magia registra tipo MAGIA no historico`() {
        val personagem = personagem().also { personagemRepository.criar(it) }
        val simulador = simulador()

        simulador.rolarMagia(personagem, faces = 8, modificador = 0)

        assertEquals(TipoRolagem.MAGIA, historicoRepository.listarDaSessao()[0].tipoRolagem)
    }

    @Test
    fun `historico lista rolagens da sessao em ordem cronologica decrescente (AC 3 2)`() {
        val personagem = personagem().also { personagemRepository.criar(it) }
        val tempos = ArrayDeque(listOf(1000L, 3000L, 2000L))
        val simulador = simulador(relogio = { tempos.removeFirst() })

        simulador.rolarAtaque(personagem)
        simulador.rolarDano(personagem, faces = 6, modificador = 2)
        simulador.rolarMagia(personagem, faces = 8, modificador = 0)

        assertEquals(
            listOf(3000L, 2000L, 1000L),
            historicoRepository.listarDaSessao().map { it.timestamp },
        )
    }

    @Test
    fun `rolagem de ataque com vantagem executa duas rolagens e escolhe a maior (AC 2 2)`() {
        val personagem = personagem().also { personagemRepository.criar(it) }
        val simulador = simulador()

        val resultado = simulador.rolarAtaque(personagem, Vantagem.VANTAGEM)

        assertEquals(2, resultado.dados.size)
        assertEquals(maxOf(resultado.dados[0], resultado.dados[1]), resultado.resultado)
    }

    @Test
    fun `itens e magias sao persistidos e recuperados junto a ficha`() {
        val personagem = personagem().also { personagemRepository.criar(it) }
        val item = Item(nome = "Espada Longa", peso = 2.5, quantidade = 1)
        val magia = Magia(nome = "Bola de Fogo", nivel = 3)
        itemRepository.criar(personagem.id, item)
        magiaRepository.criar(personagem.id, magia)

        assertEquals(listOf(item), itemRepository.listarPorPersonagem(personagem.id))
        assertEquals(listOf(magia), magiaRepository.listarPorPersonagem(personagem.id))
    }
}
