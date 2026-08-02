package com.escudomestre.digital.domain.model

import kotlin.test.Test
import kotlin.test.assertEquals

class HistoricoTest {

    @Test
    fun `registrar grava evento com personagem tipo resultado e timestamp`() {
        val historico = Historico()
        val evento = RolagemEvento(
            personagemId = "p1",
            tipoRolagem = TipoRolagem.ATACAR,
            resultado = 17,
            timestamp = 1000,
        )
        historico.registrar(evento)

        val registros = historico.registrosDaSessao()
        assertEquals(1, registros.size)
        assertEquals("p1", registros[0].personagemId)
        assertEquals(TipoRolagem.ATACAR, registros[0].tipoRolagem)
        assertEquals(17, registros[0].resultado)
        assertEquals(1000, registros[0].timestamp)
    }

    @Test
    fun `registros sao listados em ordem cronologica decrescente`() {
        val historico = Historico()
        historico.registrar(RolagemEvento("p1", TipoRolagem.ATACAR, 10, 1000))
        historico.registrar(RolagemEvento("p1", TipoRolagem.DANO, 8, 2000))
        historico.registrar(RolagemEvento("p1", TipoRolagem.MAGIA, 22, 1500))

        val registros = historico.registrosDaSessao()
        assertEquals(listOf(2000L, 1500L, 1000L), registros.map { it.timestamp })
    }
}
