package domain.model

/**
 * Equipamento defensivo do SRD 5e/2024.
 *
 * Armaduras leves (11/12): CA = caBase + modDES.
 * Armaduras médias (13/14/15): CA = caBase + modDES (limitado por maxModDestreza).
 * Armaduras pesadas (16/17/18): CA = caBase fixa (Destreza não entra).
 * Escudo: soma +2 à CA (Armadura.ESCUDO.caBase).
 */
enum class Armadura(
    val rotulo: String,
    val caBase: Int,
    val maxModDestreza: Int? = null,
    val ignoraDestreza: Boolean = false,
) {
    NENHUMA("Nenhuma", 10),
    COURO("Couro", 11),
    COURO_BATIDO("Couro Batido", 12),
    MALHA_ANEL("Cota de Anéis", 13, maxModDestreza = 2),
    MALHA("Cota de Malha", 14, maxModDestreza = 2),
    MEIA_ARMADURA("Meia Armadura", 15, maxModDestreza = 2),
    BRUNES("Bruneas", 16, ignoraDestreza = true),
    PLACAS("Placas", 18, ignoraDestreza = true),
    ESCUDO("Escudo", 2);

    /** CA efetiva dado o modificador de Destreza e a presença de escudo (+2). */
    fun calcularCA(modDestreza: Int, temEscudo: Boolean): Int {
        var ca = if (ignoraDestreza) caBase else caBase + modDestreza
        if (maxModDestreza != null) ca = caBase + minOf(modDestreza, maxModDestreza)
        if (temEscudo) ca += ESCUDO.caBase
        return ca
    }
}