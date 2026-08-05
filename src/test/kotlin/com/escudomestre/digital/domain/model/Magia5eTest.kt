package com.escudomestre.digital.domain.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class Magia5eTest {

    @Test
    fun `magia de circulo zero e rotulada como truque`() {
        val magia = Magia(nome = "Pancada Sônica", nivel = 0, escola = EscolaDeMagia.EVOCACAO)
        assertEquals("Truque", magia.rotuloNivel)
    }

    @Test
    fun `magia exemplo apresenta rotulo de circulo`() {
        val magia = Magia(nome = "Bola de Fogo", nivel = 3)
        assertEquals("3º círculo", magia.rotuloNivel)
    }

    @Test
    fun `catalogo contem magia conhecida e lista para o mago`() {
        assertTrue(CatalogoDeMagias.todas().any { it.nome == "Bola de Fogo" })
        val magiasDoMago = CatalogoDeMagias.magiasDaClasse(ClasseDePersonagem.MAGO)
        assertTrue(magiasDoMago.any { it.nome == "Bola de Fogo" })
    }

    @Test
    fun `classe sem magia nao recebe itens do catalogo do mago`() {
        val doGuerreiro = CatalogoDeMagias.magiasDaClasse(ClasseDePersonagem.GUERREIRO)
        assertFalse(doGuerreiro.any { it.nome == "Bola de Fogo" })
    }

    @Test
    fun `magias do catologo preservam metadados 55`() {
        val bola = CatalogoDeMagias.todas().first { it.nome == "Bola de Fogo" }
        assertEquals(EscolaDeMagia.EVOCACAO, bola.escola)
        assertEquals("150 pés", bola.alcance)
        assertEquals(3, bola.nivel)
    }
}