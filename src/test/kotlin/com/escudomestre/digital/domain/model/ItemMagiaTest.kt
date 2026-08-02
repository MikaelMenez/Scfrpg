package com.escudomestre.digital.domain.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class ItemMagiaTest {

    @Test
    fun `cria item com nome peso e quantidade`() {
        val item = Item(nome = "Espada Longa", peso = 2.5, quantidade = 1)
        assertEquals("Espada Longa", item.nome)
        assertEquals(2.5, item.peso)
        assertEquals(1, item.quantidade)
    }

    @Test
    fun `cria magia despreparada e com slot livre`() {
        val magia = Magia(nome = "Bola de Fogo", nivel = 3)
        assertEquals("Bola de Fogo", magia.nome)
        assertEquals(3, magia.nivel)
        assertFalse(magia.preparada)
        assertFalse(magia.slotGasto)
    }
}
