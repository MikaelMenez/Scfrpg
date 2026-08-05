package domain.model

import kotlin.math.floor

/**
 * Bônus de proficiência por nível (SRD 5e / 2024):
 * 2 nos níveis 1–4, 3 nos 5–8, 4 nos 9–12, 5 nos 13–16, 6 nos 17–20.
 */
fun bonusDeProficiencia(nivel: Int): Int =
    if (nivel < 1) 0 else 2 + (nivel - 1) / 4

/** CD (classe de dificuldade) de resistência = 8 + proficiência + modificador. */
fun cdDeResistencia(atributo: Int, nivel: Int): Int =
    8 + bonusDeProficiencia(nivel) + modificadorDeAtributo(atributo)

/** Bônus de ataque de magia = proficiência + coeficiente do atributo de conjuração. */
fun bonusAtaqueDeMagia(atributo: Int, nivel: Int): Int =
    bonusDeProficiencia(nivel) + modificadorDeAtributo(atributo)

/** Modificador de atributo: (valor - 10) / 2 arredondado para baixo (16 → +3, 9 → −1). */
fun modificadorDeAtributo(valor: Int): Int =
    floor((valor - 10) / 2.0).toInt()