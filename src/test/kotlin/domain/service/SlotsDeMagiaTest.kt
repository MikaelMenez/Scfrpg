package domain.service

import domain.model.Classe
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Testes dos espaços de magia segundo as tabelas oficiais de D&D 5e.
 */
class SlotsDeMagiaTest {

    @Test
    fun `conjurador pleno nivel 1 tem dois espacos de primeiro circulo`() {
        assertEquals(mapOf(1 to 2), SlotsDeMagia.slots(Classe.MAGO, 1))
    }

    @Test
    fun `menu conjurador pleno nivel 3 tem espacos de 1o e 2o circulo`() {
        assertEquals(mapOf(1 to 4, 2 to 2), SlotsDeMagia.slots(Classe.MAGO, 3))
    }

    @Test
    fun `entidade de feiticeiro nivel 5 tem espacos de terceiro circulo`() {
        assertEquals(mapOf(1 to 4, 2 to 3, 3 to 2), SlotsDeMagia.slots(Classe.FEITICEIRO, 5))
    }

    @Test
    fun `maior circulo de conjurador pleno nivel 9 e quinto`() {
        assertEquals(5, SlotsDeMagia.circulos(Classe.MAGO, 9))
    }

    @Test
    fun `maior circulo de mago nivel 17 e nono`() {
        assertEquals(9, SlotsDeMagia.circulos(Classe.MAGO, 17))
    }

    @Test
    fun `conjurador metade paladino nivel 1 usa nivel de conjuracao 1`() {
        assertEquals(1, SlotsDeMagia.nivelDeConjuracao(Classe.PALADINO, 1))
        assertEquals(mapOf(1 to 2), SlotsDeMagia.slots(Classe.PALADINO, 1))
    }

    @Test
    fun `conjurador metade nivel 4 tem tres espacos de primeiro circulo`() {
        assertEquals(2, SlotsDeMagia.nivelDeConjuracao(Classe.PATRULHEIRO, 4))
        assertEquals(mapOf(1 to 3), SlotsDeMagia.slots(Classe.PATRULHEIRO, 4))
    }

    @Test
    fun `bruxo nivel 3 tem dois espacos de segundo circulo`() {
        assertEquals(2, SlotsDeMagia.nivelDeConjuracao(Classe.BRUXO, 3))
        assertEquals(mapOf(2 to 2), SlotsDeMagia.slots(Classe.BRUXO, 3))
    }

    @Test
    fun `bruxo nivel 5 tem dois espacos de terceiro circulo`() {
        assertEquals(3, SlotsDeMagia.circulos(Classe.BRUXO, 5))
        assertEquals(mapOf(3 to 2), SlotsDeMagia.slots(Classe.BRUXO, 5))
    }

    @Test
    fun `bruxo ganha terceiro espaco no nivel 11`() {
        assertEquals(mapOf(5 to 3), SlotsDeMagia.slots(Classe.BRUXO, 11))
    }

    @Test
    fun `classe marcial nao conjura`() {
        assertFalse(SlotsDeMagia.ehConjurador(Classe.GUERREIRO))
        assertEquals(emptyMap<Int, Int>(), SlotsDeMagia.slots(Classe.GUERREIRO, 20))
        assertEquals(0, SlotsDeMagia.circulos(Classe.GUERREIRO, 20))
    }

    @Test
    fun `bardo clérigo druida e feiticeiro sao conjuradores plenos`() {
        listOf(Classe.BARDO, Classe.CLERIGO, Classe.DRUIDA, Classe.FEITICEIRO, Classe.MAGO).forEach {
            assertTrue(SlotsDeMagia.ehConjuradorPleno(it))
        }
    }

    @Test
    fun `paladino e patrulheiro sao conjuradores de metade`() {
        assertTrue(SlotsDeMagia.ehConjuradorMetade(Classe.PALADINO))
        assertTrue(SlotsDeMagia.ehConjuradorMetade(Classe.PATRULHEIRO))
    }

    @Test
    fun `bruxo e conjurador de pacto`() {
        assertTrue(SlotsDeMagia.ehConjuradorDePacto(Classe.BRUXO))
    }

    @Test
    fun `atributo de conjuracao por classe`() {
        assertEquals("INT", SlotsDeMagia.atributoDeConjuracao(Classe.MAGO))
        assertEquals("SAB", SlotsDeMagia.atributoDeConjuracao(Classe.CLERIGO))
        assertEquals("SAB", SlotsDeMagia.atributoDeConjuracao(Classe.DRUIDA))
        assertEquals("CAR", SlotsDeMagia.atributoDeConjuracao(Classe.BARDO))
        assertEquals("CAR", SlotsDeMagia.atributoDeConjuracao(Classe.FEITICEIRO))
        assertEquals("CAR", SlotsDeMagia.atributoDeConjuracao(Classe.BRUXO))
        assertEquals("CAR", SlotsDeMagia.atributoDeConjuracao(Classe.PALADINO))
        assertEquals("SAB", SlotsDeMagia.atributoDeConjuracao(Classe.PATRULHEIRO))
        assertEquals("-", SlotsDeMagia.atributoDeConjuracao(Classe.GUERREIRO))
    }

    @Test
    fun `numero de espacos cresce com o nivel do conjurador pleno`() {
        val espacosNiveis = listOf(1, 3, 5, 9, 17).map { SlotsDeMagia.slots(Classe.CLERIGO, it).values.sum() }
        assertTrue(espacosNiveis.zipWithNext().all { (a, b) -> b >= a })
    }
}