package domain.model

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PersonagemTest {

    @Test
    fun `aplicar dano deve reduzir PV`() {
        val p = Personagem("1", "Heroi", "HUMANO", "GUERREIRO", 1, 20, 20, 15)
        p.aplicarDano(5)
        assertEquals(15, p.pontosDeVidaAtual)
    }

    @Test
    fun `PV zero deve mudar estado para DERROTADO`() {
        val p = Personagem("1", "Heroi", "HUMANO", "GUERREIRO", 1, 10, 20, 15)
        p.aplicarDano(15)
        assertEquals(0, p.pontosDeVidaAtual)
        assertEquals(EstadoCombate.DERROTADO, p.estadoCombate)
    }

    @Test
    fun `curar nao deve ultrapassar PV maximo`() {
        val p = Personagem("1", "Heroi", "HUMANO", "GUERREIRO", 1, 15, 20, 15)
        p.curar(10)
        assertEquals(20, p.pontosDeVidaAtual)
    }

    @Test
    fun `curar deve restaurar de DERROTADO`() {
        val p = Personagem("1", "Heroi", "HUMANO", "GUERREIRO", 1, 0, 20, 15)
        p.estadoCombate = EstadoCombate.DERROTADO
        p.curar(5)
        assertEquals(EstadoCombate.AGUARDANDO_INICIATIVA, p.estadoCombate)
    }

    @Test
    fun `subir nivel deve aumentar nivel e PV maximo`() {
        val p = Personagem("1", "Heroi", "HUMANO", "GUERREIRO", 1, 20, 20, 15)
        val pvAntes = p.pontosDeVidaMaximo
        p.subirNivel()
        assertEquals(2, p.nivel)
        assertTrue(p.pontosDeVidaMaximo > pvAntes)
    }

    @Test
    fun `observer deve ser notificado ao aplicar dano`() {
        val p = Personagem("1", "Heroi", "HUMANO", "GUERREIRO", 1, 20, 20, 15)
        var notificado = false
        p.adicionarObserver(object : PersonagemObserver {
            override fun onPersonagemAlterado(personagem: Personagem) {
                notificado = true
            }
        })
        p.aplicarDano(5)
        assertTrue(notificado)
    }
}
