package domain.service

import domain.model.DadoVirtual
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertTrue

class DadoVirtualTest {

    @Test
    fun `deve retornar valor entre 1 e faces`() {
        repeat(100) {
            val resultado = DadoVirtual.rolar(20, 0)
            assertTrue(resultado in 1..20)
        }
    }

    @Test
    fun `deve aplicar modificador positivo`() {
        val resultado = DadoVirtual.rolar(6, 5)
        assertTrue(resultado in 6..11)
    }

    @Test
    fun `deve lancar excecao para faces menor ou igual a zero`() {
        assertThrows<IllegalArgumentException> { DadoVirtual.rolar(0, 0) }
        assertThrows<IllegalArgumentException> { DadoVirtual.rolar(-1, 0) }
    }

    @Test
    fun `distribuicao d20 deve cobrir extremos em amostra grande`() {
        val resultados = (1..1000).map { DadoVirtual.rolar(20, 0) }
        assertTrue(resultados.any { it == 1 })
        assertTrue(resultados.any { it == 20 })
    }
}
