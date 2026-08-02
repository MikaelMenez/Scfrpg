package com.escudomestre.digital.domain.model

import java.util.UUID

/**
 * Entidade do domínio representando uma magia do grimório (Seção 7.2).
 * Relaciona-se em cardinalidade um-para-muitos com Personagem (Grimorio).
 */
data class Magia(
    val id: String = UUID.randomUUID().toString(),
    val nome: String,
    var nivel: Int,
    var preparada: Boolean = false,
    var slotGasto: Boolean = false,
)
