package domain.service

import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class RoladorDeAtributosTest {

    @Test
    fun `4d6 drop lowest deve gerar 6 atributos`() {
        val atributos = RoladorDeAtributos.rolar4d6DropLowest()
        assertEquals(6, atributos.size)
        atributos.forEach { assertTrue(it in 3..18) }
    }

    @Test
    fun `array fixo deve retornar valores oficiais`() {
        assertEquals(listOf(15, 14, 13, 12, 10, 8), RoladorDeAtributos.arrayFixo())
    }

    @Test
    fun `compra de pontos valida com 27 pontos`() {
        assertTrue(RoladorDeAtributos.compraDePontos(listOf(15, 15, 15, 8, 8, 8)))
    }

    @Test
    fun `compra de pontos invalida acima de 27`() {
        assertFalse(RoladorDeAtributos.compraDePontos(listOf(15, 15, 15, 15, 8, 8)))
    }

    @Test
    fun `tabela de custo do SRD esta correta`() {
        assertEquals(0, RoladorDeAtributos.custo(8))
        assertEquals(1, RoladorDeAtributos.custo(9))
        assertEquals(2, RoladorDeAtributos.custo(10))
        assertEquals(3, RoladorDeAtributos.custo(11))
        assertEquals(4, RoladorDeAtributos.custo(12))
        assertEquals(5, RoladorDeAtributos.custo(13))
        assertEquals(7, RoladorDeAtributos.custo(14))
        assertEquals(9, RoladorDeAtributos.custo(15))
    }

    @Test
    fun `orçamento inicial de 27 pontos`() {
        assertEquals(27, RoladorDeAtributos.pontosRestantes(List(6) { 8 }))
    }

    @Test
    fun `pontos restantes diminuem conforme se gasta`() {
        assertEquals(18, RoladorDeAtributos.pontosRestantes(listOf(15, 8, 8, 8, 8, 8)))
    }

    @Test
    fun `custo total de uma distribuicao`() {
        assertEquals(27, RoladorDeAtributos.custoTotal(listOf(15, 15, 15, 8, 8, 8)))
    }

    @Test
    fun `pode adicionar permitido apenas dentro do orçamento`() {
        assertTrue(RoladorDeAtributos.podeAdicionar(8, 27))
        assertFalse(RoladorDeAtributos.podeAdicionar(15, 0))
        assertFalse(RoladorDeAtributos.podeAdicionar(8, 0))
    }

    @Test
    fun `pode diminuir no minimo e no maximo`() {
        assertFalse(RoladorDeAtributos.podeDiminuir(8))
        assertTrue(RoladorDeAtributos.podeDiminuir(15))
    }

    @Test
    fun `custo lanca excecao para valor fora do intervalo`() {
        assertThrows<IllegalArgumentException> { RoladorDeAtributos.custo(7) }
        assertThrows<IllegalArgumentException> { RoladorDeAtributos.custo(16) }
    }

    @Test
    fun `compra exige uma distribuicao de 6 valores`() {
        assertFalse(RoladorDeAtributos.compraDePontos(listOf(15, 15)))
        assertFalse(RoladorDeAtributos.compraDePontos(List(7) { 8 }))
    }
}