package application

import domain.model.EstadoCombate
import domain.model.Personagem
import domain.service.SimuladorDeCombate
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SimuladorDeCombateTest {

    private fun heroi() = Personagem("1", "Heroi", "HUMANO", "GUERREIRO", 1, 20, 20, 15)

    @Test
    fun `simular combate deve registrar historico de ataque`() {
        val p = heroi()
        SimuladorDeCombate().rolarAtaqueParaPersonagem(p, 5)
        assertTrue(p.historico.isNotEmpty())
        assertEquals("ATAQUE", p.historico.last().tipoRolagem)
    }

    @Test
    fun `rolar dano deve registrar historico`() {
        val p = heroi()
        SimuladorDeCombate().rolarDanoParaPersonagem(p, 6, 1, 3)
        assertTrue(p.historico.any { it.tipoRolagem == "DANO" })
    }

    @Test
    fun `aplicar dano deve reduzir PV e registrar`() {
        val p = heroi()
        SimuladorDeCombate().aplicarDanoERegistrar(p, 5)
        assertEquals(15, p.pontosDeVidaAtual)
        assertTrue(p.historico.any { it.tipoRolagem == "APLICAR_DANO" })
    }

    @Test
    fun `processar dano direto deve alterar PV`() {
        val p = heroi()
        SimuladorDeCombate().processarDano(p, 8)
        assertEquals(12, p.pontosDeVidaAtual)
    }

    @Test
    fun `ataque com vantagem fica no intervalo com bonus`() {
        val p = heroi()
        val resultado = SimuladorDeCombate().rolarAtaqueParaPersonagem(p, 3, temVantagem = true)
        assertTrue(resultado in 4..23)
    }

    @Test
    fun `ataque com desvantagem fica no intervalo com bonus`() {
        val p = heroi()
        val resultado = SimuladorDeCombate().rolarAtaqueParaPersonagem(p, 2, temDesvantagem = true)
        assertTrue(resultado in 3..22)
    }

    @Test
    fun `aplicar dano fatal muda estado para derrotado`() {
        val p = heroi()
        SimuladorDeCombate().aplicarDanoERegistrar(p, 30)
        assertEquals(0, p.pontosDeVidaAtual)
        assertEquals(EstadoCombate.DERROTADO, p.estadoCombate)
    }

    @Test
    fun `registros de historico tem timestamp sequencial`() {
        val p = heroi()
        val sim = SimuladorDeCombate()
        sim.rolarAtaqueParaPersonagem(p, 1)
        sim.rolarDanoParaPersonagem(p, 6, 1, 0)
        val tempos = p.historico.map { it.timestamp }
        assertEquals(tempos.sorted(), tempos)
    }

    @Test
    fun `historico registra personagem e resultado`() {
        val p = heroi()
        SimuladorDeCombate().rolarAtaqueParaPersonagem(p, 5)
        val registro = p.historico.last()
        assertEquals(p.id, registro.personagemId)
        assertEquals("ATAQUE", registro.tipoRolagem)
        assertTrue(registro.resultado >= 1)
    }
}