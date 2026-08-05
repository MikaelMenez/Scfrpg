package domain.service

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class TestarAtaqueTest {

    @Test
    fun `critico natural 20 sempre acerta`() {
        val r = MotorDeRegras.testarAtaque(bonusAtaque = 0, caAlvo = 40, valorDoDado = 20)
        assertTrue(r.acertou)
        assertTrue(r.critico)
    }

    @Test
    fun `falha natural 1 sempre erra`() {
        val r = MotorDeRegras.testarAtaque(bonusAtaque = 20, caAlvo = 1, valorDoDado = 1)
        assertFalse(r.acertou)
        assertFalse(r.critico)
    }

    @Test
    fun `acerta quando total maior ou igual a CA`() {
        val r = MotorDeRegras.testarAtaque(bonusAtaque = 5, caAlvo = 15, valorDoDado = 10)
        assertTrue(r.acertou)
        assertEquals(15, r.total)
    }

    @Test
    fun `erra quando total menor que a CA`() {
        val r = MotorDeRegras.testarAtaque(bonusAtaque = 5, caAlvo = 16, valorDoDado = 10)
        assertFalse(r.acertou)
    }

    @Test
    fun `critico dobra dano`() {
        assertEquals(12, MotorDeRegras.aplicarDanoComCritico(6, true))
        assertEquals(6, MotorDeRegras.aplicarDanoComCritico(6, false))
    }

    @Test
    fun `dano desarmado eh 1 mais Forca`() {
        assertEquals(5, MotorDeRegras.danoDesarmado(4))
        assertEquals(1, MotorDeRegras.danoDesarmado(0))
    }
}