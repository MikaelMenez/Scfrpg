package domain.model

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class EquipamentoTest {

    @Test
    fun `arma de acuidade usa Destreza`() {
        assertEquals(Atributo.DESTREZA, Arma.ADAGA.atributoDeCombate)
        assertEquals(Atributo.DESTREZA, Arma.CIMITARRA.atributoDeCombate)
    }

    @Test
    fun `arma corpo a corpo usa Forca`() {
        assertEquals(Atributo.FORCA, Arma.ESPADA_LARGA.atributoDeCombate)
        assertEquals(Atributo.FORCA, Arma.MALHO.atributoDeCombate)
    }

    @Test
    fun `dano com modificadores soma bonus correto`() {
        val forca = Arma.ESPADA_LARGA.danoComModificadores(4, 2)
        assertEquals(1, forca.first)
        assertEquals(12, forca.second) // 1d8 + 4
    }

    @Test
    fun `armadura leve adiciona Destreza`() {
        assertEquals(13, Armadura.COURO.calcularCA(2, false))
        assertEquals(14, Armadura.COURO_BATIDO.calcularCA(2, false))
    }

    @Test
    fun `armadura media limita Destreza`() {
        assertEquals(16, Armadura.MALHA.calcularCA(3, false)) // 14 + (cap 2)
        assertEquals(16, Armadura.MALHA.calcularCA(5, false))  // ainda 2 (cap)
    }

    @Test
    fun `armadura pesada ignora Destreza`() {
        assertEquals(18, Armadura.PLACAS.calcularCA(5, false))
        assertEquals(16, Armadura.BRUNES.calcularCA(0, false))
    }

    @Test
    fun `escudo soma 2 a CA`() {
        assertEquals(15, Armadura.COURO.calcularCA(2, true))
        assertEquals(10, Armadura.NENHUMA.calcularCA(0, false))
    }
}