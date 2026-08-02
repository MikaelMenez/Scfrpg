package com.escudomestre.digital.domain.model

/**
 * Tipos de armadura do SRD 5e, que definem como a CA (Classe de Armadura) é calculada
 * a partir da base da armadura, do modificador de Destreza e da proficiência da classe.
 */
enum class TipoArmadura(val rotulo: String) {
    LEVE("Leve"),
    MEDIA("Média"),
    PESADA("Pesada"),
    ESCUDO("Escudo"),
}

/**
 * Armaduras e escudos do SRD 5e com a CA base e o modificador máximo de Destreza
 * aplicável (0 para armaduras pesadas, que ignoram Destreza; 2 para médias; null
 * para leves, que usam o modificador completo).
 *
 * CA final: `10 + mod(DEX)` sem armadura; com armadura, `base + mod(DEX) limitado
 * ao máximo`, e +2 se usar escudo (Regras 5e, Capítulo de Equipamento).
 */
enum class Armadura(
    val rotulo: String,
    val tipo: TipoArmadura,
    val caBase: Int,
    val maxModDestreza: Int? = null,
) {
    SEM_ARMADURA("Sem armadura", TipoArmadura.LEVE, 10, maxModDestreza = null),

    ACOLCHOADA("Acolchoada", TipoArmadura.LEVE, 11, maxModDestreza = null),
    COURO("Couro", TipoArmadura.LEVE, 11, maxModDestreza = null),
    COURO_BATIDO("Couro batido", TipoArmadura.LEVE, 12, maxModDestreza = null),

    PELE("Pele", TipoArmadura.MEDIA, 12, maxModDestreza = 2),
    COTA_DE_MALHA_LEVE("Cota de malha leve", TipoArmadura.MEDIA, 13, maxModDestreza = 2),
    COTA_DE_ESCAMAS("Cota de escamas", TipoArmadura.MEDIA, 14, maxModDestreza = 2),
    PEITORAL("Peitoral", TipoArmadura.MEDIA, 14, maxModDestreza = 2),
    MEIA_ARMADURA("Meia armadura", TipoArmadura.MEDIA, 15, maxModDestreza = 2),

    COTA_DE_ANEIS("Cota de anéis", TipoArmadura.PESADA, 14, maxModDestreza = 0),
    COTA_DE_MALHA("Cota de malha", TipoArmadura.PESADA, 16, maxModDestreza = 0),
    COTA_DE_LAMINAS("Cota de lâminas", TipoArmadura.PESADA, 17, maxModDestreza = 0),
    ARMADURA_DE_PLACAS("Armadura de placas", TipoArmadura.PESADA, 18, maxModDestreza = 0),

    ESCUDO("Escudo", TipoArmadura.ESCUDO, 2, maxModDestreza = null),
}
