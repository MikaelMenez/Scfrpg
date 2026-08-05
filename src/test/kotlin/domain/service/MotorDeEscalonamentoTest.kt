package domain.service

import domain.model.EstadoCombate
import domain.model.Personagem
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class MotorDeEscalonamentoTest {

    private fun personagem(nome: String) =
        Personagem(nome, nome, "HUMANO", "GUERREIRO", 1, 20, 20, 15)

    @Test
    fun `ordenar iniciativa marca apenas o primeiro como em turno`() {
        val lista = MotorDeEscalonamento.ordenarIniciativa(
            listOf(personagem("A"), personagem("B"), personagem("C"))
        )
        assertEquals(3, lista.size)
        assertEquals(1, lista.count { it.estadoCombate == EstadoCombate.EM_TURNO })
        assertEquals(2, lista.count { it.estadoCombate == EstadoCombate.AGUARDANDO_INICIATIVA })
    }

    @Test
    fun `ordenar iniciativa preserva o conjunto de personagens`() {
        val originais = listOf(personagem("A"), personagem("B"), personagem("C"))
        val ordenados = MotorDeEscalonamento.ordenarIniciativa(originais)
        assertEquals(originais.map { it.id }.toSet(), ordenados.map { it.id }.toSet())
    }

    @Test
    fun `avancar turno passa para o proximo e reseta o atual`() {
        val ordenados = listOf(personagem("A"), personagem("B"))
        ordenados[0].estadoCombate = EstadoCombate.EM_TURNO
        val (seguinte, indice) = MotorDeEscalonamento.avancarTurno(ordenados, 0)
        assertEquals("B", seguinte.id)
        assertEquals(1, indice)
        assertEquals(EstadoCombate.EM_TURNO, ordenados[1].estadoCombate)
        assertEquals(EstadoCombate.AGUARDANDO_INICIATIVA, ordenados[0].estadoCombate)
    }

    @Test
    fun `avancar turno volta ao inicio no fim da rodada`() {
        val ordenados = listOf(personagem("A"), personagem("B"))
        ordenados[1].estadoCombate = EstadoCombate.EM_TURNO
        val (seguinte, indice) = MotorDeEscalonamento.avancarTurno(ordenados, 1)
        assertEquals("A", seguinte.id)
        assertEquals(0, indice)
        assertEquals(EstadoCombate.EM_TURNO, ordenados[0].estadoCombate)
    }

    @Test
    fun `avancar turno preserva derrotado no combatente atual`() {
        val ordenados = listOf(personagem("A"), personagem("B"))
        ordenados[0].estadoCombate = EstadoCombate.DERROTADO
        MotorDeEscalonamento.avancarTurno(ordenados, 0)
        assertEquals(EstadoCombate.DERROTADO, ordenados[0].estadoCombate)
    }

    @Test
    fun `avancar turno de inimigo atordoado mantem estado`() {
        val ordenados = listOf(personagem("A"), personagem("B"))
        ordenados[0].estadoCombate = EstadoCombate.ATURDIDO_INCAPACITADO
        MotorDeEscalonamento.avancarTurno(ordenados, 0)
        assertEquals(EstadoCombate.ATURDIDO_INCAPACITADO, ordenados[0].estadoCombate)
    }

    @Test
    fun `personagem derrotado nao e marcado como em turno`() {
        val lista = MotorDeEscalonamento.ordenarIniciativa(
            listOf(personagem("A"), personagem("B"), personagem("C"))
        )
        lista[0].estadoCombate = EstadoCombate.DERROTADO
        MotorDeEscalonamento.avancarTurno(lista, 0)
        assertTrue(lista.none { it.estadoCombate == EstadoCombate.DERROTADO && it.id == lista[0].id && it.estadoCombate == EstadoCombate.EM_TURNO })
    }
}