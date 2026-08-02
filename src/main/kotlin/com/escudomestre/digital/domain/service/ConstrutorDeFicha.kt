package com.escudomestre.digital.domain.service

import com.escudomestre.digital.domain.model.Armadura
import com.escudomestre.digital.domain.model.Arma
import com.escudomestre.digital.domain.model.Atributo
import com.escudomestre.digital.domain.model.ClasseDePersonagem
import com.escudomestre.digital.domain.model.Personagem
import com.escudomestre.digital.domain.model.Raca
import com.escudomestre.digital.domain.model.modificadorDeAtributo

/**
 * Construção de fichas D&D 5e (RU01): aplica os aumentos raciais de habilidade aos
 * valores base, calcula os PV (máximo do dado de vida no 1º nível + mod. de Constituição
 * e média + mod. nos níveis seguintes), e monta o [Personagem] com equipamento.
 */
class ConstrutorDeFicha {

    /** Valor médio por nível após o 1º (arredondado para cima): dado de vida/2 + 1. */
    private fun mediaDadoDeVida(dadoDeVida: Int): Int = dadoDeVida / 2 + 1

    fun pontosDeVidaMaximo(classe: ClasseDePersonagem, nivel: Int, modConstituicao: Int): Int {
        require(nivel >= 1) { "nível deve ser maior que zero" }
        val noPrimeiroNivel = classe.dadoDeVida + modConstituicao
        val porNivelSeguinte = mediaDadoDeVida(classe.dadoDeVida) + modConstituicao
        return noPrimeiroNivel + (nivel - 1) * porNivelSeguinte
    }

    /**
     * Monta uma ficha a partir dos [valoresBase] (rolagem/array/compra), aplicando os
     * bônus raciais de [raca]. Os PV iniciam no máximo calculado.
     */
    fun construir(
        nome: String,
        raca: Raca,
        classe: ClasseDePersonagem,
        nivel: Int = 1,
        valoresBase: Map<Atributo, Int>,
        arma: Arma? = null,
        armadura: Armadura = Armadura.SEM_ARMADURA,
        escudo: Boolean = false,
    ): Personagem {
        val atributos = mutableMapOf<Atributo, Int>()
        Atributo.entries.forEach { atributo ->
            val base = valoresBase[atributo] ?: 10
            val bonusRacial = raca.bonus[atributo] ?: 0
            atributos[atributo] = base + bonusRacial
        }
        val pvMaximo = pontosDeVidaMaximo(classe, nivel, modificadorDeAtributo(atributos[Atributo.CONSTITUICAO]!!))
        return Personagem(
            nome = nome,
            raca = raca,
            classe = classe,
            nivel = nivel,
            atributos = atributos,
            pontosDeVidaAtual = pvMaximo,
            pontosDeVidaMaximo = pvMaximo,
            armaEquipada = arma,
            armaduraEquipada = armadura,
            escudoEquipado = escudo,
        )
    }
}
