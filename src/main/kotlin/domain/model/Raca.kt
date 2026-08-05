package domain.model

enum class Raca(val bonus: Map<String, Int>) {
    ANAO(mapOf("CON" to 2)),
    ELFO(mapOf("DES" to 2)),
    HALFLING(mapOf("DES" to 2)),
    HUMANO(mapOf("FOR" to 1, "DES" to 1, "CON" to 1, "INT" to 1, "SAB" to 1, "CAR" to 1)),
    DRACONATO(mapOf("FOR" to 2, "CAR" to 1)),
    GNOMO(mapOf("INT" to 2)),
    MEIO_ELEFO(mapOf("CAR" to 2)),
    MEIO_ORC(mapOf("FOR" to 2, "CON" to 1)),
    TIEFLING(mapOf("INT" to 1, "CAR" to 2))
}
