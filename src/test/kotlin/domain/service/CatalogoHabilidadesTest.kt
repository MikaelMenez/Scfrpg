package domain.service

import domain.model.Classe
import domain.model.HabilidadeDeClasse
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CatalogoHabilidadesTest {

    @Test
    fun `guerreiro retorna lista de habilidades`() {
        assertTrue(CatalogoHabilidades.habilidadesDaClasse(Classe.GUERREIRO).isNotEmpty())
    }

    @Test
    fun `todas as classes possuem habilidades catalogadas`() {
        Classe.values().forEach {
            assertTrue(CatalogoHabilidades.habilidadesDaClasse(it).isNotEmpty(), "faltam habilidades para $it")
        }
    }

    @Test
    fun `habilidadesAteNivel filtra pela nivel do personagem`() {
        val nivel1 = CatalogoHabilidades.habilidadesAteNivel(Classe.LADINO, 1)
        val nivel5 = CatalogoHabilidades.habilidadesAteNivel(Classe.LADINO, 5)
        assertTrue(nivel5.size >= nivel1.size)
        assertTrue(nivel1.all { it.nivel <= 1 })
    }

    @Test
    fun `toda habilidade tem nivel positivo e descricao`() {
        Classe.values().forEach { classe ->
            CatalogoHabilidades.habilidadesDaClasse(classe).forEach {
                assertTrue(it.nivel > 0)
                assertTrue(it.descricao.isNotBlank())
            }
        }
    }

    @Test
    fun `segundo folego do guerreiro desbloqueia no nivel 1`() {
        val habilidades = CatalogoHabilidades.habilidadesDaClasse(Classe.GUERREIRO)
        assertEquals(1, habilidades.first { it.nome == "Segundo Fôlego" }.nivel)
    }

    @Test
    fun `surto de acao aparece com nivel crescente`() {
        val dobrucoes = CatalogoHabilidades.habilidadesAteNivel(Classe.GUERREIRO, 12).map { it.nome }
        assertEquals(2, dobrucoes.indexOf("Surto de Ação"))
    }

    @Test
    fun `habilidades ordenadas crescente por nivel`() {
        Classe.values().forEach { classe ->
            val lista = CatalogoHabilidades.habilidadesAteNivel(classe, 20)
            assertTrue(lista.zipWithNext().all { (a, b) -> a.nivel <= b.nivel })
        }
    }

    @Test
    fun `habilidades retornam instancias do modelo`() {
        val habilidades = CatalogoHabilidades.habilidadesDaClasse(Classe.BARBARO)
        assertTrue(habilidades.isNotEmpty())
        habilidades.forEach { assertTrue(it is HabilidadeDeClasse) }
    }
}