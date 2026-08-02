package com.escudomestre.digital.domain.service

import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals

class MotorDeRegrasTest {

    private fun motorComSequencia(vararg valores: Int): MotorDeRegras {
        val dadoControlado = DadoVirtual(SequenciaRandom(ArrayDeque(valores.toList())))
        return MotorDeRegras(dadoControlado)
    }

    @Test
    fun `rolagem simples de ataque aplica o modificador`() {
        val motor = motorComSequencia(10)
        val resultado = motor.rolarAtaque(modificador = 3)
        assertEquals(14, resultado.resultado)
        assertEquals(listOf(14), resultado.dados)
    }

    @Test
    fun `vantagem retorna o maior valor entre duas rolagens do PRNG`() {
        val motor = motorComSequencia(5, 3)
        val resultado = motor.rolarAtaque(modificador = 0, vantagem = Vantagem.VANTAGEM)
        assertEquals(6, resultado.resultado)
        assertEquals(listOf(6, 4), resultado.dados)
    }

    @Test
    fun `desvantagem retorna o menor valor entre duas rolagens do PRNG`() {
        val motor = motorComSequencia(5, 3)
        val resultado = motor.rolarAtaque(modificador = 0, vantagem = Vantagem.DESVANTAGEM)
        assertEquals(4, resultado.resultado)
        assertEquals(listOf(6, 4), resultado.dados)
    }

    @Test
    fun `rolagem de dano soma o modificador ao resultado`() {
        val motor = motorComSequencia(4)
        val resultado = motor.rolarDano(faces = 8, modificador = 2)
        assertEquals(7, resultado.resultado)
    }

    @Test
    fun `rolagem de dano com vantagem escolhe o maior`() {
        val motor = motorComSequencia(4, 7)
        val resultado = motor.rolarDano(faces = 8, modificador = 0, vantagem = Vantagem.VANTAGEM)
        assertEquals(8, resultado.resultado)
    }
}

private class SequenciaRandom(private val valores: ArrayDeque<Int>) : Random() {
    override fun nextBits(bitCount: Int): Int = throw UnsupportedOperationException("não usado")

    override fun nextInt(until: Int): Int = valores.removeFirst() % until
}
