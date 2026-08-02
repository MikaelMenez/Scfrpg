package com.escudomestre.digital

import com.escudomestre.digital.domain.model.Armadura
import com.escudomestre.digital.domain.model.Arma
import com.escudomestre.digital.domain.model.Atributo
import com.escudomestre.digital.domain.model.ClasseDePersonagem
import com.escudomestre.digital.domain.model.Personagem
import com.escudomestre.digital.domain.model.Raca

/**
 * Fábrica de personagens para os testes de integração e unidade.
 * Guerreiro humano nivel 3 com Espada Longa e Cota de Malha, PV máximo 30.
 */
object FichasDeTeste {

    val ATRIBUTOS: Map<Atributo, Int> = mapOf(
        Atributo.FORCA to 16,
        Atributo.DESTREZA to 15,
        Atributo.CONSTITUICAO to 15,
        Atributo.INTELIGENCIA to 11,
        Atributo.SABEDORIA to 11,
        Atributo.CARISMA to 9,
    )

    fun guerreiro(
        nome: String = "Aragorn",
        nivel: Int = 3,
        pvAtual: Int = 30,
        atributos: Map<Atributo, Int> = ATRIBUTOS,
        arma: Arma? = Arma.ESPADA_LONGA,
        armadura: Armadura = Armadura.COTA_DE_MALHA,
    ): Personagem = Personagem(
        nome = nome,
        raca = Raca.HUMANO,
        classe = ClasseDePersonagem.GUERREIRO,
        nivel = nivel,
        atributos = mutableMapOf<Atributo, Int>().apply { putAll(atributos) },
        pontosDeVidaAtual = pvAtual,
        pontosDeVidaMaximo = 30,
        armaEquipada = arma,
        armaduraEquipada = armadura,
    )
}
