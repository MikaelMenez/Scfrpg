package com.escudomestre.digital.domain.repository

import com.escudomestre.digital.domain.model.Personagem

/**
 * Interface de repositório para as operações CRUD da tabela de Fichas via Exposed ORM (RF01).
 */
interface PersonagemRepository {

    /** Persiste uma nova ficha de personagem e a retorna com o id gerado. */
    fun criar(personagem: Personagem): Personagem

    /** Busca uma ficha de personagem pelo [id]. */
    fun buscarPorId(id: String): Personagem?

    /** Atualiza o registro correspondente da ficha [personagem] no banco local. */
    fun atualizar(personagem: Personagem): Personagem

    /** Atualiza somente os pontos de vida atuais da ficha de [id]. */
    fun atualizarPontosDeVida(id: String, valor: Int)

    /** Remove a ficha de personagem com o [id]. */
    fun excluir(id: String)

    /** Lista todas as fichas persistidas. */
    fun listar(): List<Personagem>
}
