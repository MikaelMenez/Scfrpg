package com.escudomestre.digital.domain.repository

import com.escudomestre.digital.domain.model.Magia

/**
 * Interface de repositório para as operações CRUD da tabela de Magias via Exposed ORM (RF01).
 */
interface MagiaRepository {

    /** Persiste uma nova [magia] vinculada à ficha de [personagemId]. */
    fun criar(personagemId: String, magia: Magia): Magia

    /** Busca uma magia pelo [id]. */
    fun buscarPorId(id: String): Magia?

    /** Lista as magias do grimório da ficha de [personagemId]. */
    fun listarPorPersonagem(personagemId: String): List<Magia>

    /** Atualiza o registro correspondente da [magia] no banco local. */
    fun atualizar(magia: Magia): Magia

    /** Remove a magia com o [id]. */
    fun excluir(id: String)
}
