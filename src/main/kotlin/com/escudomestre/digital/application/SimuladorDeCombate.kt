package com.escudomestre.digital.application

import com.escudomestre.digital.domain.model.Personagem
import com.escudomestre.digital.domain.model.RolagemEvento
import com.escudomestre.digital.domain.model.TipoRolagem
import com.escudomestre.digital.domain.repository.HistoricoRepository
import com.escudomestre.digital.domain.repository.PersonagemRepository
import com.escudomestre.digital.domain.service.MotorDeRegras
import com.escudomestre.digital.domain.service.ResultadoRolagem
import com.escudomestre.digital.domain.service.Vantagem

/**
 * Resultado de um ataque processado com as regras do SRD 5e:
 * d20 + bônus de ataque comparado à Classe de Armadura do alvo.
 */
data class ResultadoDeAtaque(
    val acertou: Boolean,
    val critico: Boolean,
    val rolagem: ResultadoRolagem,
    val dano: Int = 0,
)

/**
 * Camada de aplicação: coordena o fluxo de combate entre a interface e o motor de regras,
 * delegando a persistência ao repositório e notificando a interface via padrão Observer
 * (Seção 6.2, 6.5 e 7.3). As rolagens seguem as regras do SRD 5e.
 */
class SimuladorDeCombate(
    private val motorDeRegras: MotorDeRegras = MotorDeRegras(),
    private val personagemRepository: PersonagemRepository,
    private val historicoRepository: HistoricoRepository,
    private val relogio: () -> Long = System::currentTimeMillis,
) {

    private val observadores = mutableListOf<ObservadorDeMudanca>()

    /** Registra um observador para receber notificações de mudança de estado. */
    fun registrarObservador(observador: ObservadorDeMudanca) {
        observadores += observador
    }

    /** Remove um observador previamente registrado. */
    fun removerObservador(observador: ObservadorDeMudanca) {
        observadores -= observador
    }

    /**
     * Fluxo "Aplicar Dano" (Seção 7.3): aplica [valor] de dano à [personagem],
     * delega a persistência ao DAO (atualizarPontosDeVida) e notifica a interface
     * via Observer. A notificação ocorre dentro do limite de 200 ms (RNF02).
     */
    fun processarDano(personagem: Personagem, valor: Int): Personagem {
        personagem.aplicarDano(valor)
        personagemRepository.atualizarPontosDeVida(personagem.id, personagem.pontosDeVidaAtual)
        notificarMudancaDeEstado(personagem)
        return personagem
    }

    /**
     * Ataque 5e: rola `d20 + bônus de ataque` do [atacante] contra a Classe de Armadura
     * do [alvo]. Natural 20 sempre acerta (crítico); natural 1 sempre erra. Em caso de
     * acerto, rola o dano da arma equipada + modificador de atributo e aplica ao alvo.
     */
    fun atacar(
        atacante: Personagem,
        alvo: Personagem,
        vantagem: Vantagem = Vantagem.NENHUMA,
    ): ResultadoDeAtaque {
        val rolagem = motorDeRegras.rolarAtaque(atacante.modificadorAtaque, vantagem)
        registrarRolagem(atacante, TipoRolagem.ATACAR, rolagem.resultado)

        val critico = rolagem.dadoBruto == 20
        val falhaCritica = rolagem.dadoBruto == 1
        val acertou = critico || (!falhaCritica && rolagem.resultado >= alvo.classeArmadura)

        val dano = if (acertou) aplicarDanoDeArma(atacante, alvo, critico) else 0
        return ResultadoDeAtaque(
            acertou = acertou,
            critico = critico,
            rolagem = rolagem,
            dano = dano,
        )
    }

    /**
     * Simula um ataque 5e contra um alvo com Classe de Armadura [caDoAlvo] sem aplicar
     * dano real: útil para o painel exibir acerto/crítico e o dano potencial da arma.
     */
    fun testarAtaque(
        atacante: Personagem,
        caDoAlvo: Int,
        vantagem: Vantagem = Vantagem.NENHUMA,
    ): ResultadoDeAtaque {
        val rolagem = motorDeRegras.rolarAtaque(atacante.modificadorAtaque, vantagem)
        registrarRolagem(atacante, TipoRolagem.ATACAR, rolagem.resultado)

        val critico = rolagem.dadoBruto == 20
        val falhaCritica = rolagem.dadoBruto == 1
        val acertou = critico || (!falhaCritica && rolagem.resultado >= caDoAlvo)

        val dano = if (acertou) calcularDano(atacante, critico) else 0
        return ResultadoDeAtaque(
            acertou = acertou,
            critico = critico,
            rolagem = rolagem,
            dano = dano,
        )
    }

    private fun calcularDano(atacante: Personagem, critico: Boolean): Int {
        val arma = atacante.armaEquipada
        val modificador = atacante.modificadorDe(atacante.atributoDeCombate)
        val rolagens = rolarDadosDeArma(arma)
        var dano = rolagens.sum()
        if (critico) {
            dano += rolarDadosDeArma(arma).sum()
        }
        return dano + modificador
    }

    private fun rolarDadosDeArma(arma: com.escudomestre.digital.domain.model.Arma?): List<Int> {
        if (arma == null) return listOf(1)
        return List(arma.quantidadeDadosDano) { motorDeRegras.rolarDano(arma.facesDano).resultado }
    }

    private fun aplicarDanoDeArma(atacante: Personagem, alvo: Personagem, critico: Boolean): Int {
        val dano = calcularDano(atacante, critico)
        processarDano(alvo, dano)
        registrarRolagem(atacante, TipoRolagem.DANO, dano)
        return dano
    }

    /** Executa a rolagem de ataque somando o bônus de ataque do personagem (AC 2.1). */
    fun rolarAtaque(
        personagem: Personagem,
        vantagem: Vantagem = Vantagem.NENHUMA,
    ): ResultadoRolagem {
        val resultado = motorDeRegras.rolarAtaque(personagem.modificadorAtaque, vantagem)
        registrarRolagem(personagem, TipoRolagem.ATACAR, resultado.resultado)
        return resultado
    }

    /** Executa a rolagem de dano de [faces] com [modificador]. */
    fun rolarDano(
        personagem: Personagem,
        faces: Int,
        modificador: Int = 0,
        vantagem: Vantagem = Vantagem.NENHUMA,
    ): ResultadoRolagem {
        val resultado = motorDeRegras.rolarDano(faces, modificador, vantagem)
        registrarRolagem(personagem, TipoRolagem.DANO, resultado.resultado)
        return resultado
    }

    /** Executa a rolagem de magia de [faces] com [modificador]. */
    fun rolarMagia(
        personagem: Personagem,
        faces: Int,
        modificador: Int = 0,
        vantagem: Vantagem = Vantagem.NENHUMA,
    ): ResultadoRolagem {
        val resultado = motorDeRegras.rolarDano(faces, modificador, vantagem)
        registrarRolagem(personagem, TipoRolagem.MAGIA, resultado.resultado)
        return resultado
    }

    /**
     * Registra automaticamente o evento de rolagem no histórico da sessão (AC 3.1),
     * tanto no domínio quanto na persistência, sem exigir ação manual do Mestre.
     */
    private fun registrarRolagem(personagem: Personagem, tipoRolagem: TipoRolagem, resultado: Int) {
        val evento = RolagemEvento(
            personagemId = personagem.id,
            tipoRolagem = tipoRolagem,
            resultado = resultado,
            timestamp = relogio(),
        )
        personagem.historico.registrar(evento)
        historicoRepository.registrar(evento)
    }

    private fun notificarMudancaDeEstado(personagem: Personagem) {
        observadores.forEach { it.notificarMudancaDeEstado(personagem) }
    }
}
