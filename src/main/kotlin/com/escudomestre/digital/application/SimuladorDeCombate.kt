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
 * Camada de aplicação: coordena o fluxo de combate entre a interface e o motor de regras,
 * delegando a persistência ao repositório e notificando a interface via padrão Observer
 * (Seção 6.2, 6.5 e 7.3).
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

    /** Executa a rolagem de ataque somando o modificador de ataque do personagem (AC 2.1). */
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
