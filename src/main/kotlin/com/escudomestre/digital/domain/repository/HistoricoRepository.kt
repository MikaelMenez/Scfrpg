package com.escudomestre.digital.domain.repository

import com.escudomestre.digital.domain.model.RolagemEvento

/**
 * Interface de repositório da entidade Historico (RU03/RF03).
 * Persiste os eventos de rolagem da sessão e permite a consulta cronológica.
 */
interface HistoricoRepository {

    /** Registra um [evento] de rolagem na sessão corrente, sem exigir ação manual (AC 3.1). */
    fun registrar(evento: RolagemEvento): RolagemEvento

    /** Lista os eventos da sessão em ordem cronológica decrescente (AC 3.2). */
    fun listarDaSessao(limite: Int = 100): List<RolagemEvento>
}
