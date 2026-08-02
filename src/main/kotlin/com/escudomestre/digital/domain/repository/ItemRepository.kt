package com.escudomestre.digital.domain.repository

import com.escudomestre.digital.domain.model.Item

/**
 * Interface de repositório para as operações CRUD da tabela de Itens via Exposed ORM (RF01).
 */
interface ItemRepository {

    /** Persiste um novo [item] vinculado à ficha de [personagemId]. */
    fun criar(personagemId: String, item: Item): Item

    /** Busca um item pelo [id]. */
    fun buscarPorId(id: String): Item?

    /** Lista os itens do inventário da ficha de [personagemId]. */
    fun listarPorPersonagem(personagemId: String): List<Item>

    /** Atualiza o registro correspondente do [item] no banco local. */
    fun atualizar(item: Item): Item

    /** Remove o item com o [id]. */
    fun excluir(id: String)
}
