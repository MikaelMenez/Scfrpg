package domain.model

import domain.service.SimuladorDeCombate
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CombatePersonagemTest {

    private fun personagem(
        arma: Arma? = null,
        armadura: Armadura? = null,
        escudo: Boolean = false,
        nivel: Int = 1,
    ) = Personagem(
        id = "1",
        nome = "H",
        raca = "HUMANO",
        classe = "GUERREIRO",
        nivel = nivel,
        pontosDeVidaAtual = 30,
        pontosDeVidaMaximo = 30,
        classeArmadura = 13,
        atributos = mutableMapOf(
            "FOR" to 18, "DES" to 14, "CON" to 16, "INT" to 10, "SAB" to 10, "CAR" to 10
        ),
        armaEquipada = arma,
        armaduraEquipada = armadura,
        escudoEquipado = escudo,
    )

    @Test
    fun `bonus de proficiencia segue o nivel`() {
        assertEquals(2, personagem().bonusProficiencia())
        assertEquals(3, personagem(nivel = 5).bonusProficiencia())
    }

    @Test
    fun `modificador de ataque soma proficiencia e atributo da arma`() {
        val comEspada = personagem(arma = Arma.ESPADA_LARGA)
        assertEquals(6, comEspada.modificadorAtaque()) // 2 + FOR(4)

        val comAdaga = personagem(arma = Arma.ADAGA)
        assertEquals(4, comAdaga.modificadorAtaque()) // 2 + DES(2)
    }

    @Test
    fun `dados de dano incluem bonus de Forca`() {
        val (quantidade, faces, bonus) = personagem(arma = Arma.ESPADA_LARGA).dadosDeDano()
        assertEquals(1, quantidade)
        assertEquals(8, faces)
        assertEquals(4, bonus)
    }

    @Test
    fun `dano desarmado usa 1 mais bonus de Forca`() {
        val (quantidade, faces, bonus) = personagem().dadosDeDano()
        assertEquals(0, quantidade)
        assertEquals(1, faces)
        assertEquals(4, bonus)
    }

    @Test
    fun `ca efetiva usa armadura escudo e destreza`() {
        assertEquals(13, personagem(armadura = Armadura.COURO).caEfetiva()) // 11 + DES(2)
        assertEquals(16, personagem(armadura = Armadura.MALHA).caEfetiva()) // 14 + 2 (cap)
        assertEquals(18, personagem(armadura = Armadura.PLACAS).caEfetiva())
        assertEquals(15, personagem(armadura = Armadura.COURO, escudo = true).caEfetiva()) // 13 + 2
    }

    @Test
    fun `descanso longo restaura todos os PV`() {
        val p = personagem()
        p.aplicarDano(12)
        assertEquals(18, p.pontosDeVidaAtual)
        p.descansoLongo()
        assertEquals(30, p.pontosDeVidaAtual)
    }

    @Test
    fun `simulador aplica dano quando acerta a CA`() {
        val atacante = personagem(arma = Arma.ESPADA_LARGA)
        val alvo = personagem()
        val sim = SimuladorDeCombate()
        val resultado = sim.atacar(atacante, alvo, valorDoDado = 15)
        assertTrue(resultado.acertou)
        assertTrue(alvo.pontosDeVidaAtual < alvo.pontosDeVidaMaximo)
    }

    @Test
    fun `critico dobra o dano`() {
        val atacante = personagem(arma = Arma.ESPADA_LARGA)
        val alvo = personagem()
        val sim = SimuladorDeCombate()
        val resultado = sim.atacar(atacante, alvo, valorDoDado = 20)
        assertTrue(resultado.critico)
        assertTrue(alvo.pontosDeVidaAtual <= alvo.pontosDeVidaMaximo - 10)
    }

    @Test
    fun `CD e bonus de magia via personagem usam atributo de conjuracao`() {
        val mago = Personagem(
            id = "9",
            nome = "M",
            raca = "HUMANO",
            classe = "MAGO",
            nivel = 1,
            pontosDeVidaAtual = 10,
            pontosDeVidaMaximo = 10,
            classeArmadura = 12,
            atributos = mutableMapOf("INT" to 18, "CAR" to 10, "SAB" to 10)
        )
        assertEquals(14, mago.cdDeResistencia(Atributo.INTELIGENCIA)) // 8 + 2 + 4
        assertEquals(6, mago.bonusAtaqueDeMagia(Atributo.INTELIGENCIA)) // 2 + 4
    }

    @Test
    fun `descanso curto nao altera PV nem morre`() {
        val p = personagem(nivel = 1)
        val antes = p.pontosDeVidaAtual
        p.descansoCurto()
        assertEquals(antes, p.pontosDeVidaAtual)
    }

    @Test
    fun `subir nivel com media rola hit dice medio mais mod`() {
        val p = personagem(nivel = 1) // GUERREIRO d10, CON 16 (+3)
        val antes = p.pontosDeVidaMaximo
        p.subirNivel(mediaAoNivelar = true)
        assertEquals(2, p.nivel)
        assertEquals(antes + 8, p.pontosDeVidaMaximo) // (10+1)/2 + 3
    }
}
