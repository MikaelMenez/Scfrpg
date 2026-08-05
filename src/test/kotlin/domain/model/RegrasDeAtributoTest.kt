package domain.model

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class RegrasDeAtributoTest {

    @Test
    fun `modificador de atributo usa arredondamento para baixo`() {
        assertEquals(-5, modificadorDeAtributo(1))
        assertEquals(-1, modificadorDeAtributo(9))
        assertEquals(0, modificadorDeAtributo(10))
        assertEquals(2, modificadorDeAtributo(14))
        assertEquals(4, modificadorDeAtributo(18))
    }

    @Test
    fun `bonus de proficiencia segue tabela 5e`() {
        assertEquals(2, bonusDeProficiencia(1))
        assertEquals(2, bonusDeProficiencia(4))
        assertEquals(3, bonusDeProficiencia(5))
        assertEquals(4, bonusDeProficiencia(9))
        assertEquals(5, bonusDeProficiencia(13))
        assertEquals(6, bonusDeProficiencia(17))
        assertEquals(6, bonusDeProficiencia(20))
        assertEquals(0, bonusDeProficiencia(0))
    }

    @Test
    fun `CD de resistencia usa proficiencia e atributo`() {
        assertEquals(14, cdDeResistencia(18, 1))
        assertEquals(12, cdDeResistencia(14, 1))
    }

    @Test
    fun `bonus de ataque de magia usa proficiencia e atributo`() {
        assertEquals(7, bonusAtaqueDeMagia(18, 5))
        assertEquals(5, bonusAtaqueDeMagia(16, 3))
    }

    @Test
    fun `deSigla resolve nomes e siglas`() {
        assertEquals(Atributo.FORCA, Atributo.deSigla("FOR"))
        assertEquals(Atributo.DESTREZA, Atributo.deSigla("DES"))
        assertEquals(Atributo.CONSTITUICAO, Atributo.deSigla("CON"))
        assertEquals(Atributo.INTELIGENCIA, Atributo.deSigla("INT"))
        assertEquals(Atributo.SABEDORIA, Atributo.deSigla("SAB"))
        assertEquals(Atributo.CARISMA, Atributo.deSigla("CAR"))
        assertEquals(null, Atributo.deSigla("XYZ"))
    }
}