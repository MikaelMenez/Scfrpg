package com.escudomestre.digital.domain.model

/**
 * Raças jogáveis do SRD 5e com seus aumentos de pontuação de habilidade
 * (Ability Score Increases). O bônus é aplicado sobre o valor base do atributo.
 */
enum class Raca(val rotulo: String, val bonus: Map<Atributo, Int>) {
    ANÃO("Anão", mapOf(Atributo.CONSTITUICAO to 2)),
    ELFO("Elfo", mapOf(Atributo.DESTREZA to 2)),
    HALFLING("Halfling", mapOf(Atributo.DESTREZA to 2)),
    HUMANO("Humano", mapOf(
        Atributo.FORCA to 1,
        Atributo.DESTREZA to 1,
        Atributo.CONSTITUICAO to 1,
        Atributo.INTELIGENCIA to 1,
        Atributo.SABEDORIA to 1,
        Atributo.CARISMA to 1,
    )),
    DRACONATO("Draconato", mapOf(Atributo.FORCA to 2, Atributo.CARISMA to 1)),
    GNOMO("Gnomo", mapOf(Atributo.INTELIGENCIA to 2)),
    MEIO_ELFO("Meio-Elfo", mapOf(Atributo.CARISMA to 2)),
    MEIO_ORC("Meio-Orc", mapOf(Atributo.FORCA to 2, Atributo.CONSTITUICAO to 1)),
    TIEFLING("Tiefling", mapOf(Atributo.INTELIGENCIA to 1, Atributo.CARISMA to 2)),
}
