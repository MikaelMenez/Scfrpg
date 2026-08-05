package domain.model

import domain.service.CatalogoDeMagias
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class Magia5eTest {

    private val bolaDeFogo: Magia = CatalogoDeMagias.porNome("Bola de Fogo")!!

    @Test
    fun `magia do catalogo tem todos os campos preenchidos`() {
        assertEquals("Bola de Fogo", bolaDeFogo.nome)
        assertEquals(3, bolaDeFogo.nivel)
        assertEquals(EscolaDeMagia.EVOCACAO, bolaDeFogo.escola)
        assertEquals("1 ação", bolaDeFogo.tempoConjuracao)
        assertTrue(bolaDeFogo.alcance.isNotBlank())
        assertTrue(bolaDeFogo.componentes.isNotBlank())
        assertTrue(bolaDeFogo.duracao.isNotBlank())
        assertFalse(bolaDeFogo.requerConcentracao)
        assertTrue(bolaDeFogo.descricao.isNotBlank())
    }

    @Test
    fun `truque deve ter nivel zero`() {
        val truque = CatalogoDeMagias.porNome("Dardo de Chama")!!
        assertTrue(truque.ehTruque)
        assertEquals(0, truque.nivel)
    }

    @Test
    fun `magia de nivel positivo nao e truque`() {
        assertFalse(bolaDeFogo.ehTruque)
    }

    @Test
    fun `copia para grimorio gera nova identidade e preparada`() {
        val copia = bolaDeFogo.copiaParaGrimorio("novo-id")
        assertEquals("novo-id", copia.id)
        assertTrue(copia.preparada)
        assertFalse(copia.slotGasto)
        assertEquals(bolaDeFogo.nome, copia.nome)
        assertEquals(bolaDeFogo.nivel, copia.nivel)
        assertEquals(bolaDeFogo.escola, copia.escola)
    }

    @Test
    fun `copia independe do objeto original`() {
        val copia = bolaDeFogo.copiaParaGrimorio("x")
        copia.nome = "Modificado"
        assertEquals("Bola de Fogo", bolaDeFogo.nome)
        assertEquals("Modificado", copia.nome)
    }

    @Test
    fun `escola deTexto deve fazer busca por nome`() {
        assertEquals(EscolaDeMagia.NECROMANCIA, EscolaDeMagia.deTexto("NECROMANCIA"))
        assertNull(EscolaDeMagia.deTexto("INVALIDA"))
        assertEquals(EscolaDeMagia.CONJURACAO, EscolaDeMagia.deTexto("conjuracao"))
    }

    @Test
    fun `todos os nomes de escola sao unicos`() {
        val nomes = EscolaDeMagia.entries.map { it.name }
        assertEquals(nomes.size, nomes.toSet().size)
    }

    @Test
    fun `catalogo nao deve ter magia sem nome`() {
        CatalogoDeMagias.magias.forEach { assertTrue(it.nome.isNotBlank()) }
    }
}