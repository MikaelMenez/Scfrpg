package com.escudomestre.digital.domain.model

import java.util.UUID

/** Tipos de rolagem registrados no histórico da sessão (RF03). */
enum class TipoRolagem {
    ATACAR,
    DANO,
    MAGIA,
}

/**
 * Objeto de valor descrevendo um evento de rolagem executado na sessão (Seção 7.2).
 *
 * @param personagemId identificador do personagem associado à rolagem.
 * @param tipoRolagem tipo de rolagem executada (ataque, dano ou magia).
 * @param resultado resultado final da rolagem.
 * @param timestamp instante em que a rolagem foi registrada.
 */
data class RolagemEvento(
    val personagemId: String,
    val tipoRolagem: TipoRolagem,
    val resultado: Int,
    val timestamp: Long,
)

/**
 * Entidade do domínio que registra os eventos de rolagem da sessão (RU03/RF03).
 * Cada evento associa personagemId, tipoRolagem, resultado e timestamp (AC 3.1).
 */
class Historico(val id: String = UUID.randomUUID().toString()) {

    private val registros = mutableListOf<RolagemEvento>()

    /** Registra um [evento] de rolagem na sessão corrente. */
    fun registrar(evento: RolagemEvento): RolagemEvento {
        registros.add(evento)
        return evento
    }

    /** Lista os registros da sessão em ordem cronológica decrescente (AC 3.2). */
    fun registrosDaSessao(): List<RolagemEvento> =
        registros.sortedByDescending { it.timestamp }
}
