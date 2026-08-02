package com.escudomestre.digital.domain.service

import kotlin.random.Random

/**
 * Serviço de domínio responsável pela geração de números pseudoaleatórios (PRNG),
 * simulando a rolagem de dados virtuais (Seção 6.1 / 7.2).
 */
open class DadoVirtual(private val random: Random = Random.Default) {

    /**
     * Rola um dado de [faces] faces e aplica o [modificador] ao resultado.
     * Retorna um inteiro no intervalo `[1 + modificador, faces + modificador]`.
     */
    open fun rolar(faces: Int, modificador: Int = 0): Int {
        require(faces > 0) { "faces deve ser maior que zero" }
        return random.nextInt(faces) + 1 + modificador
    }
}
