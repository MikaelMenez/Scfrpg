package domain.model

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class HistoricoTest {

    @Test
    fun `registrar deve criar historico com dados do evento`() {
        val evento = RolagemEvento("p1", "ATAQUE", 18)
        val h = Historico.registrar(evento)
        assertEquals("p1", h.personagemId)
        assertEquals("ATAQUE", h.tipoRolagem)
        assertEquals(18, h.resultado)
        assertTrue(h.timestamp > 0)
    }

    @Test
    fun `registrar deve gerar UUID unico`() {
        val evento = RolagemEvento("p1", "DANO", 10)
        val h1 = Historico.registrar(evento)
        val h2 = Historico.registrar(evento)
        assertTrue(h1.id != h2.id)
    }
}
