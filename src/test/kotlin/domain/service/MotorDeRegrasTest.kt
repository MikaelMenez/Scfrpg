package domain.service

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class MotorDeRegrasTest {

    @Test
    fun `vantagem deve retornar valor dentro do intervalo esperado`() {
        repeat(50) {
            val vantagem = MotorDeRegras.rolarComVantagem(20, 0)
            assertTrue(vantagem in 1..20)
        }
    }

    @Test
    fun `desvantagem deve retornar valor dentro do intervalo esperado`() {
        repeat(50) {
            val desvantagem = MotorDeRegras.rolarComDesvantagem(20, 0)
            assertTrue(desvantagem in 1..20)
        }
    }

    @Test
    fun `modificador deve calcular corretamente`() {
        assertEquals(0, MotorDeRegras.calcularModificador(10))
        assertEquals(3, MotorDeRegras.calcularModificador(16))
        assertEquals(-1, MotorDeRegras.calcularModificador(9))
    }

    @Test
    fun `rolar dano deve somar quantidade de dados`() {
        val dano = MotorDeRegras.rolarDano(6, 2, 3)
        assertTrue(dano in 5..15)
    }

    @Test
    fun `rolar ataque com vantagem retorna valor valido`() {
        val r = MotorDeRegras.rolarAtaque(5, true, false)
        assertTrue(r in 6..25)
    }

    @Test
    fun `rolar ataque com desvantagem retorna valor valido`() {
        val r = MotorDeRegras.rolarAtaque(3, false, true)
        assertTrue(r in 4..23)
    }
}
