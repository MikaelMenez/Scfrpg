package domain.service

import domain.model.Classe
import domain.model.Raca
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ConstrutorDeFichaTest {

    @Test
    fun `deve criar personagem com atributos base`() {
        val p = ConstrutorDeFicha.construir(
            "Teste", Raca.HUMANO, Classe.GUERREIRO,
            mapOf("FOR" to 10, "DES" to 10, "CON" to 10, "INT" to 10, "SAB" to 10, "CAR" to 10)
        )
        assertEquals("Teste", p.nome)
        assertEquals(1, p.nivel)
        assertTrue(p.pontosDeVidaMaximo > 0)
    }

    @Test
    fun `deve aplicar bonus racial meio-elfo`() {
        val p = ConstrutorDeFicha.construir(
            "MeioElfo", Raca.MEIO_ELEFO, Classe.BARDO,
            mapOf("FOR" to 10, "DES" to 10, "CON" to 10, "INT" to 10, "SAB" to 10, "CAR" to 10)
        )
        assertEquals("MeioElfo", p.nome)
        assertEquals(1, p.nivel)
    }

    @Test
    fun `deve calcular CA baseada em DES`() {
        val p = ConstrutorDeFicha.construir(
            "Elfo", Raca.ELFO, Classe.LADINO,
            mapOf("FOR" to 10, "DES" to 14, "CON" to 10, "INT" to 10, "SAB" to 10, "CAR" to 10)
        )
        assertEquals(13, p.classeArmadura)
    }

    @Test
    fun `deve calcular bonus inicial de ataque`() {
        val bonus = ConstrutorDeFicha.calcularBonusInicialAtaque(16)
        assertEquals(3, bonus)
    }
}
