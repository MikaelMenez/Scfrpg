package domain.service

import domain.model.Classe
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class CatalogoDeMagiasTest {

    @Test
    fun `mago tem magias no catalogo`() {
        val magias = CatalogoDeMagias.magiasDaClasse(Classe.MAGO)
        assertTrue(magias.isNotEmpty())
        assertTrue(magias.any { it.nome == "Bola de Fogo" })
    }

    @Test
    fun `guerreiro nao tem magias de classe`() {
        assertEquals(emptyList<Any>(), CatalogoDeMagias.magiasDaClasse(Classe.GUERREIRO))
    }

    @Test
    fun `todas as magias de uma classe sao validas`() {
        Classe.values().forEach { classe ->
            CatalogoDeMagias.magiasDaClasse(classe).forEach {
                assertTrue(it.nome.isNotBlank())
                assertTrue(it.nivel in 0..8)
                assertTrue(it.descricao.isNotBlank())
            }
        }
    }

    @Test
    fun `porNome busca ignorando caixa`() {
        assertNotNull(CatalogoDeMagias.porNome("bola de fogo"))
        assertNotNull(CatalogoDeMagias.porNome("BOLA DE FOGO"))
        assertNull(CatalogoDeMagias.porNome("Inexistente"))
    }

    @Test
    fun `porId retorna magia correspondente`() {
        val magia = CatalogoDeMagias.porId("Mísseis Mágicos")
        assertNotNull(magia)
        assertEquals("Mísseis Mágicos", magia.nome)
    }

    @Test
    fun `cada magia tem id igual ao nome`() {
        CatalogoDeMagias.magias.forEach { assertEquals(it.nome, it.id) }
    }

    @Test
    fun `todas_as_classes_com_conjuracao retornam apenas classes validas`() {
        val classes = CatalogoDeMagias.todasAsClasses()
        assertTrue(classes.isNotEmpty())
        classes.forEach { assertTrue(SlotsDeMagia.ehConjurador(it)) }
    }
}