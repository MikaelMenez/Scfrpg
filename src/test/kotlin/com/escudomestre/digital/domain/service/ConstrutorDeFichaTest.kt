package com.escudomestre.digital.domain.service

import com.escudomestre.digital.domain.model.Armadura
import com.escudomestre.digital.domain.model.Arma
import com.escudomestre.digital.domain.model.Atributo
import com.escudomestre.digital.domain.model.ClasseDePersonagem
import com.escudomestre.digital.domain.model.Raca
import com.escudomestre.digital.domain.model.bonusDeProficiencia
import com.escudomestre.digital.domain.model.modificadorDeAtributo
import kotlin.test.Test
import kotlin.test.assertEquals

class ConstrutorDeFichaTest {

    private val construtor = ConstrutorDeFicha()

    private val valoresBase = mapOf(
        Atributo.FORCA to 15,
        Atributo.DESTREZA to 13,
        Atributo.CONSTITUICAO to 14,
        Atributo.INTELIGENCIA to 10,
        Atributo.SABEDORIA to 10,
        Atributo.CARISMA to 8,
    )

    @Test
    fun `modificador de atributo segue a regra de arredondar para baixo`() {
        assertEquals(3, modificadorDeAtributo(16))
        assertEquals(-1, modificadorDeAtributo(8))
        assertEquals(0, modificadorDeAtributo(10))
        assertEquals(-4, modificadorDeAtributo(3))
    }

    @Test
    fun `bonus de proficiencia cresce a cada quatro nives`() {
        assertEquals(2, bonusDeProficiencia(1))
        assertEquals(2, bonusDeProficiencia(4))
        assertEquals(3, bonusDeProficiencia(5))
        assertEquals(4, bonusDeProficiencia(9))
        assertEquals(6, bonusDeProficiencia(20))
    }

    @Test
    fun `raca aplica o aumento de habilidade do SRD`() {
        val anao = construtor.construir(
            nome = "Gimli", raca = Raca.ANÃO, classe = ClasseDePersonagem.GUERREIRO,
            valoresBase = valoresBase,
        )
        val humano = construtor.construir(
            nome = "Aragorn", raca = Raca.HUMANO, classe = ClasseDePersonagem.GUERREIRO,
            valoresBase = valoresBase,
        )

        assertEquals(16, anao.valorDe(Atributo.CONSTITUICAO))
        assertEquals(15, anao.valorDe(Atributo.FORCA))
        assertEquals(16, humano.valorDe(Atributo.FORCA))
    }

    @Test
    fun `pv maximo no primeiro nivel usa o dado de vida completo mais constituicao`() {
        val guerreiro = construtor.construir(
            nome = "Aragorn", raca = Raca.HUMANO, classe = ClasseDePersonagem.GUERREIRO,
            nivel = 1, valoresBase = valoresBase,
        )
        val barbaro = construtor.construir(
            nome = "Conan", raca = Raca.HUMANO, classe = ClasseDePersonagem.BARBARO,
            nivel = 1, valoresBase = valoresBase,
        )

        assertEquals(12, guerreiro.pontosDeVidaMaximo) // 10 (d10) + 2 (CON 15 → mod +2)
        assertEquals(14, barbaro.pontosDeVidaMaximo)   // 12 (d12) + 2 (CON)
    }

    @Test
    fun `pv cresce com a media do dado de vida nos niveis seguintes`() {
        val primeiroNivel = construtor.pontosDeVidaMaximo(ClasseDePersonagem.GUERREIRO, 1, 2)
        val medioPorNivel = construtor.pontosDeVidaMaximo(ClasseDePersonagem.GUERREIRO, 1, 2) + 2 * 8

        assertEquals(12, primeiroNivel)
        assertEquals(28, medioPorNivel)

        val guerreiroNivel3 = construtor.construir(
            nome = "Aragorn", raca = Raca.HUMANO, classe = ClasseDePersonagem.GUERREIRO,
            nivel = 3, valoresBase = valoresBase,
        )
        assertEquals(28, guerreiroNivel3.pontosDeVidaMaximo)
    }

    @Test
    fun `classe armadura considera armadura leve destreza e escudo`() {
        val personagem = construtor.construir(
            nome = "Aragorn", raca = Raca.HUMANO, classe = ClasseDePersonagem.GUERREIRO,
            valoresBase = valoresBase, armadura = Armadura.COURO, escudo = true,
        )

        assertEquals(15, personagem.classeArmadura) // 11 (couro) + 2 (DEX 13) + 2 (escudo)
    }

    @Test
    fun `classe armadura de armadura pesada ignora destreza`() {
        val personagem = construtor.construir(
            nome = "Aragorn", raca = Raca.HUMANO, classe = ClasseDePersonagem.GUERREIRO,
            valoresBase = valoresBase, armadura = Armadura.ARMADURA_DE_PLACAS,
        )

        assertEquals(18, personagem.classeArmadura)
    }

    @Test
    fun `bonus de ataque soma proficiencia e atributo quando proficiente`() {
        val personagem = construtor.construir(
            nome = "Aragorn", raca = Raca.HUMANO, classe = ClasseDePersonagem.GUERREIRO,
            nivel = 3, valoresBase = valoresBase, arma = Arma.ESPADA_LONGA,
        )

        assertEquals(5, personagem.modificadorAtaque) // +2 (prof nível 3) + 3 (FOR 16)
    }

    @Test
    fun `arma de acuidade usa destreza no bonus de ataque`() {
        val personagem = construtor.construir(
            nome = "Legolas", raca = Raca.ELFO, classe = ClasseDePersonagem.GUERREIRO,
            nivel = 3, valoresBase = valoresBase, arma = Arma.RAPIEIRA,
        )

        assertEquals(4, personagem.modificadorAtaque) // +2 (prof) + 2 (DEX 15, elfo +2)
    }
}
