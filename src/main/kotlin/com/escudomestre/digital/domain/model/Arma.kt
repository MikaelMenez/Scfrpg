package com.escudomestre.digital.domain.model

/**
 * Armas do SRD 5e. Define o dado de dano, o atributo usado na rolagem de ataque e dano
 * (Força para corpo a corpo; Destreza para à distância e acuidade/finesse) e se é
 * simples ou marcial (controla a proficiência da classe).
 */
enum class Arma(
    val rotulo: String,
    val marcial: Boolean,
    val distancia: Boolean,
    val acuidade: Boolean,
    val facesDano: Int,
    val quantidadeDadosDano: Int = 1,
) {
    CACOETE("Cacete", marcial = false, distancia = false, acuidade = false, facesDano = 4),
    MAÇA("Maça", marcial = false, distancia = false, acuidade = false, facesDano = 6),
    CAJADO("Cajado", marcial = false, distancia = false, acuidade = false, facesDano = 6),
    MACHADINHA("Machadinha", marcial = false, distancia = false, acuidade = false, facesDano = 6),
    ADAGA("Adaga", marcial = false, distancia = false, acuidade = true, facesDano = 4),
    LANCA("Lança", marcial = false, distancia = false, acuidade = false, facesDano = 6),
    FOICE("Foice", marcial = false, distancia = false, acuidade = false, facesDano = 4),
    MARTELO_LEVE("Martelo leve", marcial = false, distancia = false, acuidade = false, facesDano = 4),

    ARCO_CURTO("Arco curto", marcial = false, distancia = true, acuidade = true, facesDano = 6),
    FUNDA("Funda", marcial = false, distancia = true, acuidade = true, facesDano = 4),
    BESTA_LEVE("Besta leve", marcial = false, distancia = true, acuidade = true, facesDano = 8),

    ESPADA_LONGA("Espada longa", marcial = true, distancia = false, acuidade = false, facesDano = 8),
    ESPADA_CURTA("Espada curta", marcial = true, distancia = false, acuidade = true, facesDano = 6),
    CIMITARRA("Cimitarra", marcial = true, distancia = false, acuidade = true, facesDano = 6),
    RAPIEIRA("Rapieira", marcial = true, distancia = false, acuidade = true, facesDano = 8),
    MACHADO_DE_BATALHA("Machado de batalha", marcial = true, distancia = false, acuidade = false, facesDano = 8),
    MARTELO_DE_GUERRA("Martelo de guerra", marcial = true, distancia = false, acuidade = false, facesDano = 8),
    MAÇA_ESTRELADA("Maça estrelada", marcial = true, distancia = false, acuidade = false, facesDano = 8),
    MANGUAL("Mangual", marcial = true, distancia = false, acuidade = false, facesDano = 8),
    MACHADO_GRANDE("Machado grande", marcial = true, distancia = false, acuidade = false, facesDano = 12),
    MAÇA_DE_GUERRA("Maça de guerra", marcial = true, distancia = false, acuidade = false, facesDano = 8),
    ALABARDA("Alabarda", marcial = true, distancia = false, acuidade = false, facesDano = 10),
    GLAIVE("Glaive", marcial = true, distancia = false, acuidade = false, facesDano = 10),
    CHICOTE("Chicote", marcial = true, distancia = false, acuidade = true, facesDano = 4),

    BESTA_DE_MAO("Besta de mão", marcial = true, distancia = true, acuidade = true, facesDano = 6),
    BESTA_PESADA("Besta pesada", marcial = true, distancia = true, acuidade = true, facesDano = 10),
    ARCO_LONGO("Arco longo", marcial = true, distancia = true, acuidade = true, facesDano = 8),
}
