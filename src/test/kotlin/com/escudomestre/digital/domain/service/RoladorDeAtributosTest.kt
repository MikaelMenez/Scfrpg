package com.escudomestre.digital.domain.service

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class RoladorDeAtributosTest {

    private val rolador = RoladorDeAtributos()

    @Test
    fun `array fixo retorna os seis valores padrao do SRD`() {
        assertEquals(listOf(15, 14, 13, 12, 10, 8), rolador.arrayFixo())
    }

    @Test
    fun `rolagem 4d6 descartando menor gera seis valores entre 3 e 18`() {
        val valores = rolador.rolar4d6DescartandoMenor()

        assertEquals(6, valores.size)
        assertTrue(valores.all { it in 3..18 })
    }

    @Test
    fun `rolagem de um conjunto soma os tres maiores de quatro d6`() {
        val dadosControlados = DadoVirtual(SequenciaDeDados(listOf(5, 5, 5, 0)))
        val roladorControlado = RoladorDeAtributos(dadosControlados)

        assertEquals(18, roladorControlado.rolarUmConjunto())
    }

    @Test
    fun `custo da compra de pontos segue a tabela do SRD`() {
        assertEquals(0, rolador.custoDe(8))
        assertEquals(4, rolador.custoDe(12))
        assertEquals(9, rolador.custoDe(15))
        assertFailsWith<IllegalArgumentException> { rolador.custoDe(16) }
    }

    @Test
    fun `array fixo custa exatamente os 27 pontos da compra`() {
        assertEquals(0, rolador.pontosRestantes(rolador.arrayFixo()))
        assertTrue(rolador.comprarPontos(rolador.arrayFixo()))
    }

    @Test
    fun `compra de pontos invalida e rejeitada quando estoura o orcamento`() {
        assertFalse(rolador.comprarPontos(listOf(15, 15, 15, 15, 15, 15)))
    }

    @Test
    fun `compra de pontos rejeita valores fora da faixa 8 a 15`() {
        assertFalse(rolador.comprarPontos(listOf(18, 8, 8, 8, 8, 8)))
        assertFalse(rolador.comprarPontos(listOf(7, 8, 8, 8, 8, 8)))
    }

    @Test
    fun `compra de pontos rejeita quantidade errada de valores`() {
        assertFalse(rolador.comprarPontos(listOf(15, 14, 13)))
    }
}

private class SequenciaDeDados(private val valores: List<Int>) : kotlin.random.Random() {
    private var indice = 0

    override fun nextBits(bitCount: Int): Int = throw UnsupportedOperationException("não usado")

    override fun nextInt(until: Int): Int = valores[indice++ % valores.size] % until
}
