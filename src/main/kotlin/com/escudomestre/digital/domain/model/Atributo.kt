package com.escudomestre.digital.domain.model

/**
 * Os seis atributos básicos de uma ficha D&D 5e (regras SRD).
 * O modificador de um atributo é `(valor - 10) / 2`, arredondado para baixo
 * (ex.: 16 → +3, 8 → -1, 3 → -4).
 */
enum class Atributo(val rotulo: String) {
    FORCA("Força"),
    DESTREZA("Destreza"),
    CONSTITUICAO("Constituição"),
    INTELIGENCIA("Inteligência"),
    SABEDORIA("Sabedoria"),
    CARISMA("Carisma"),
}

fun modificadorDeAtributo(valor: Int): Int = Math.floorDiv(valor - 10, 2)

/** Bônus de proficiência por nível (2, +1 a cada 4 níveis: 1–4 → +2, 5–8 → +3, ...). */
fun bonusDeProficiencia(nivel: Int): Int = 2 + (nivel - 1) / 4
