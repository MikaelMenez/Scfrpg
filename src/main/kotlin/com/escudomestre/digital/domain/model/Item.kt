package com.escudomestre.digital.domain.model

import java.util.UUID

/**
 * Entidade do domínio representando um item do inventário (Seção 7.2).
 * Relaciona-se em cardinalidade um-para-muitos com Personagem (Inventario).
 */
data class Item(
    val id: String = UUID.randomUUID().toString(),
    val nome: String,
    var peso: Double,
    var quantidade: Int,
)
