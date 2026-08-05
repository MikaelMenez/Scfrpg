package domain.model

import domain.service.CatalogoDeMagias
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ItemMagiaTest {

    @Test
    fun `inventario deve permitir adicao e remocao`() {
        val p = Personagem("1", "Heroi", "HUMANO", "GUERREIRO", 1, 10, 10, 10)
        val item = Item("i1", "Espada", 2.0, 1)
        p.inventario.add(item)
        assertEquals(1, p.inventario.size)
        p.inventario.remove(item)
        assertTrue(p.inventario.isEmpty())
    }

    @Test
    fun `grimorio deve permitir adicao de magia`() {
        val p = Personagem("1", "Heroi", "HUMANO", "GUERREIRO", 1, 10, 10, 10)
        val magia = CatalogoDeMagias.porNome("Curar Ferimentos")!!.copiaParaGrimorio("m1")
        p.grimorio.add(magia)
        assertEquals(1, p.grimorio.size)
    }

    @Test
    fun `item deve ter propriedades corretas`() {
        val item = Item("i1", "Escudo", 5.0, 1)
        assertEquals("Escudo", item.nome)
        assertEquals(5.0, item.peso)
    }
}
