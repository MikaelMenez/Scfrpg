package com.escudomestre.digital.domain.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SlotsDeMagiaTest {

    @Test
    fun `conjurador pleno no nivel 1 tem dois slots de primeiro circulo`() {
        assertEquals(mapOf(1 to 2), SlotsDeMagia.slots(ClasseDePersonagem.MAGO, 1))
    }

    @Test
    fun `mago nivel 5 desbloqueia terceiro circulo`() {
        val slots = SlotsDeMagia.slots(ClasseDePersonagem.MAGO, 5)
        assertEquals(2, slots[3])
    }

    @Test
    fun `mago nivel 20 tem slots ate o nono circulo`() {
        val slots = SlotsDeMagia.slots(ClasseDePersonagem.MAGO, 20)
        assertEquals(mapOf(1 to 4, 2 to 3, 3 to 3, 4 to 3, 5 to 3, 6 to 2, 7 to 2, 8 to 1, 9 to 1), slots)
    }

    @Test
    fun `meio conjurador nao tem slot no nivel 1 e abre no 3`() {
        assertEquals(emptyMap(), SlotsDeMagia.slots(ClasseDePersonagem.PALADINO, 1))
        val slots = SlotsDeMagia.slots(ClasseDePersonagem.PALADINO, 3)
        assertEquals(3, slots[1])
    }

    @Test
    fun `bruxo usa magia de pacto com slots unicos`() {
        assertEquals(mapOf(1 to 1), SlotsDeMagia.slots(ClasseDePersonagem.BRUXO, 1))
        assertEquals(mapOf(2 to 2), SlotsDeMagia.slots(ClasseDePersonagem.BRUXO, 3))
    }

    @Test
    fun `classe nao conjuradora nao recebe slots e nem circulos`() {
        assertEquals(emptyMap(), SlotsDeMagia.slots(ClasseDePersonagem.GUERREIRO, 9))
        assertTrue(SlotsDeMagia.circulos(ClasseDePersonagem.GUERREIRO, 9).isEmpty())
    }

    @Test
    fun `atributo de conjuracao de cada classe`() {
        assertEquals(Atributo.INTELIGENCIA, SlotsDeMagia.atributoDeConjuracao(ClasseDePersonagem.MAGO))
        assertEquals(Atributo.SABEDORIA, SlotsDeMagia.atributoDeConjuracao(ClasseDePersonagem.CLERIGO))
        assertEquals(Atributo.CARISMA, SlotsDeMagia.atributoDeConjuracao(ClasseDePersonagem.BRUXO))
        assertEquals(null, SlotsDeMagia.atributoDeConjuracao(ClasseDePersonagem.BARBARO))
    }
}