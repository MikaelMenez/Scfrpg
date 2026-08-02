package com.escudomestre.digital.domain.service

import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class DadoVirtualTest {

    private val dadoVirtual = DadoVirtual(Random(42))

    @Test
    fun `rolar retorna valor dentro do intervalo de um a vinte`() {
        repeat(1000) {
            val resultado = dadoVirtual.rolar(faces = 20)
            assertTrue(resultado in 1..20, "resultado $resultado fora do intervalo 1..20")
        }
    }

    @Test
    fun `rolar soma o modificador ao resultado do dado`() {
        val resultado = dadoVirtual.rolar(faces = 6, modificador = 4)
        assertTrue(resultado in 5..10, "resultado $resultado fora do intervalo 5..10")
    }

    @Test
    fun `rolar com modificador negativo pode reduzir abaixo de 1`() {
        val resultado = dadoVirtual.rolar(faces = 6, modificador = -5)
        assertTrue(resultado in -4..1, "resultado $resultado fora do intervalo -4..1")
    }

    @Test
    fun `rolar rejeita faces menores ou iguais a zero`() {
        assertFailsWith<IllegalArgumentException> { dadoVirtual.rolar(faces = 0) }
        assertFailsWith<IllegalArgumentException> { dadoVirtual.rolar(faces = -2) }
    }
}
