package domain.model

import kotlin.random.Random

object DadoVirtual {
    fun rolar(faces: Int, modificador: Int = 0): Int {
        require(faces > 0) { "O dado deve ter pelo menos 1 face." }
        return Random.nextInt(1, faces + 1) + modificador
    }
}
