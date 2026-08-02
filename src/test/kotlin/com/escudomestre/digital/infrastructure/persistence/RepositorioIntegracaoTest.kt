package com.escudomestre.digital.infrastructure.persistence

import com.escudomestre.digital.FichasDeTeste
import com.escudomestre.digital.domain.model.Item
import com.escudomestre.digital.domain.model.Magia
import com.escudomestre.digital.domain.model.Personagem
import com.escudomestre.digital.domain.model.RolagemEvento
import com.escudomestre.digital.domain.model.TipoRolagem
import com.escudomestre.digital.domain.repository.HistoricoRepository
import com.escudomestre.digital.domain.repository.ItemRepository
import com.escudomestre.digital.domain.repository.MagiaRepository
import com.escudomestre.digital.domain.repository.PersonagemRepository
import com.escudomestre.digital.infrastructure.persistence.repositories.ExposedHistoricoRepository
import com.escudomestre.digital.infrastructure.persistence.repositories.ExposedItemRepository
import com.escudomestre.digital.infrastructure.persistence.repositories.ExposedMagiaRepository
import com.escudomestre.digital.infrastructure.persistence.repositories.ExposedPersonagemRepository
import java.io.File
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class RepositorioIntegracaoTest {

    private lateinit var personagemRepository: PersonagemRepository
    private lateinit var itemRepository: ItemRepository
    private lateinit var magiaRepository: MagiaRepository
    private lateinit var historicoRepository: HistoricoRepository
    private var arquivoBanco: File? = null

    @BeforeTest
    fun setup() {
        arquivoBanco = File.createTempFile("escudo_test_", ".db")
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

    @Test
    fun `criar e buscar ficha por id persiste via Exposed ORM`() {
        val personagem = personagem()
        personagemRepository.criar(personagem)

        val encontrado = personagemRepository.buscarPorId(personagem.id)
        assertNotNull(encontrado)
        assertSamePersonagem(personagem, encontrado)
    }

    @Test
    fun `editar ficha existente persiste a alteracao no banco local`() {
        val personagem = personagem().also { personagemRepository.criar(it) }
        personagem.pontosDeVidaMaximo = 42
        personagem.aplicarDano(12)

        personagemRepository.atualizar(personagem)

        val encontrado = personagemRepository.buscarPorId(personagem.id)
        assertEquals(42, encontrado?.pontosDeVidaMaximo)
        assertEquals(personagem.pontosDeVidaAtual, encontrado?.pontosDeVidaAtual)
    }

    @Test
    fun `atualizar pontos de vida reflete no banco local`() {
        val personagem = personagem().also { personagemRepository.criar(it) }

        personagemRepository.atualizarPontosDeVida(personagem.id, 18)

        val encontrado = personagemRepository.buscarPorId(personagem.id)
        assertEquals(18, encontrado?.pontosDeVidaAtual)
    }

    @Test
    fun `excluir ficha remove o registro do banco`() {
        val personagem = personagem().also { personagemRepository.criar(it) }

        personagemRepository.excluir(personagem.id)

        assertNull(personagemRepository.buscarPorId(personagem.id))
    }

    @Test
    fun `listar retorna todas as fichas persistidas`() {
        personagemRepository.criar(personagem(nome = "Legolas"))
        personagemRepository.criar(personagem(nome = "Gimli"))

        val fichas = personagemRepository.listar()

        assertEquals(2, fichas.size)
        assertTrue(fichas.any { it.nome == "Legolas" })
        assertTrue(fichas.any { it.nome == "Gimli" })
    }

    @Test
    fun `criar item vinculado a personagem e listar por inventario`() {
        val personagem = personagem().also { personagemRepository.criar(it) }
        val item = Item(nome = "Espada Longa", peso = 2.5, quantidade = 1)

        itemRepository.criar(personagem.id, item)

        assertEquals(listOf(item), itemRepository.listarPorPersonagem(personagem.id))
    }

    @Test
    fun `criar item para personagem inexistente viola integridade referencial`() {
        assertFailsWith<Exception> {
            itemRepository.criar(
                personagemId = "personagem-inexistente",
                item = Item(nome = "Espada", peso = 1.0, quantidade = 1),
            )
        }
    }

    @Test
    fun `editar item persiste a alteracao`() {
        val personagem = personagem().also { personagemRepository.criar(it) }
        val item = Item(nome = "Espada Longa", peso = 2.5, quantidade = 1)
        itemRepository.criar(personagem.id, item)

        item.quantidade = 3
        itemRepository.atualizar(item)

        val atualizado = itemRepository.buscarPorId(item.id)
        assertEquals(3, atualizado?.quantidade)
    }

    @Test
    fun `excluir item remove do inventario`() {
        val personagem = personagem().also { personagemRepository.criar(it) }
        val item = Item(nome = "Poção", peso = 0.1, quantidade = 2)
        itemRepository.criar(personagem.id, item)

        itemRepository.excluir(item.id)

        assertTrue(itemRepository.listarPorPersonagem(personagem.id).isEmpty())
    }

    @Test
    fun `criar magia vinculada a personagem e listar por grimorio`() {
        val personagem = personagem().also { personagemRepository.criar(it) }
        val magia = Magia(nome = "Bola de Fogo", nivel = 3)

        magiaRepository.criar(personagem.id, magia)

        assertEquals(listOf(magia), magiaRepository.listarPorPersonagem(personagem.id))
    }

    @Test
    fun `editar magia persiste estado preparada e slot gasto`() {
        val personagem = personagem().also { personagemRepository.criar(it) }
        val magia = Magia(nome = "Curar Ferimentos", nivel = 1)
        magiaRepository.criar(personagem.id, magia)

        magia.preparada = true
        magia.slotGasto = true
        magiaRepository.atualizar(magia)

        val atualizada = magiaRepository.buscarPorId(magia.id)
        assertEquals(true, atualizada?.preparada)
        assertEquals(true, atualizada?.slotGasto)
    }

    @Test
    fun `historico registra rolagem automaticamente com personagem tipo resultado e timestamp`() {
        val personagem = personagem().also { personagemRepository.criar(it) }
        val evento = RolagemEvento(personagem.id, TipoRolagem.ATACAR, 17, 1000)

        historicoRepository.registrar(evento)

        val registros = historicoRepository.listarDaSessao()
        assertEquals(1, registros.size)
        assertEquals(personagem.id, registros[0].personagemId)
        assertEquals(TipoRolagem.ATACAR, registros[0].tipoRolagem)
        assertEquals(17, registros[0].resultado)
        assertEquals(1000, registros[0].timestamp)
    }

    @Test
    fun `historico lista rolagens da sessao em ordem cronologica decrescente`() {
        val personagem = personagem().also { personagemRepository.criar(it) }
        historicoRepository.registrar(RolagemEvento(personagem.id, TipoRolagem.ATACAR, 10, 1000))
        historicoRepository.registrar(RolagemEvento(personagem.id, TipoRolagem.DANO, 8, 3000))
        historicoRepository.registrar(RolagemEvento(personagem.id, TipoRolagem.MAGIA, 22, 2000))

        val registros = historicoRepository.listarDaSessao()

        assertEquals(listOf(3000L, 2000L, 1000L), registros.map { it.timestamp })
    }

    private fun personagem(nome: String = "Aragorn"): Personagem =
        FichasDeTeste.guerreiro(nome = nome)

    private fun assertSamePersonagem(esperado: Personagem, atual: Personagem) {
        assertEquals(esperado.id, atual.id)
        assertEquals(esperado.nome, atual.nome)
        assertEquals(esperado.raca, atual.raca)
        assertEquals(esperado.classe, atual.classe)
        assertEquals(esperado.nivel, atual.nivel)
        assertEquals(esperado.atributos, atual.atributos)
        assertEquals(esperado.pontosDeVidaAtual, atual.pontosDeVidaAtual)
        assertEquals(esperado.pontosDeVidaMaximo, atual.pontosDeVidaMaximo)
        assertEquals(esperado.armaEquipada, atual.armaEquipada)
        assertEquals(esperado.armaduraEquipada, atual.armaduraEquipada)
        assertEquals(esperado.escudoEquipado, atual.escudoEquipado)
    }
}
