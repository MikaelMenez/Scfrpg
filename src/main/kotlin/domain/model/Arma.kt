package domain.model

/**
 * Armas do SRD 5e/2024. Armas de corpo-a-corpo somam Força ao dano; armas de [distancia]
 * ou [acuidade] usam Destreza. [danoEmDano] = false indica dano fixo (desarmado = 1).
 */
enum class Arma(
    val rotulo: String,
    val quantidadeDados: Int,
    val facesDano: Int,
    val danoEmDano: Boolean = true,
    val distancia: Boolean = false,
    val acuidade: Boolean = false,
) {
    DESARMADO("Desarmado", 0, 1, danoEmDano = false),
    ADAGA("Adaga", 1, 4, distancia = true, acuidade = true),
    LACO("Laço", 1, 4, distancia = true, acuidade = true),
    PORRETE("Porrete", 1, 4, acuidade = true),
    FOICE("Foice de Mão", 1, 4, acuidade = true),
    MACHADO_DE_MAO("Machado de Mão", 1, 6),
    MARTELO_LEVE("Martelo Leve", 1, 4),
    MACA("Maça", 1, 6),
    CLAVA("Clava", 1, 4),
    JAVALINA("Javelina", 1, 6, distancia = true),
    LANCA("Lança", 1, 6, distancia = true),
    ARCO_CURTO("Arco Curto", 1, 6, distancia = true),
    BORDAO("Bordão", 1, 6),
    CIMITARRA("Cimitarra", 1, 6, acuidade = true),
    ESPADA_CURTA("Espada Curta", 1, 6, acuidade = true),
    FUNDA("Funda", 1, 4, distancia = true),
    ARCO_LONGO("Arco Longo", 1, 8, distancia = true),
    BESTA_LEVE("Besta Leve", 1, 8, distancia = true),
    BESTA_PESADA("Besta Pesada", 1, 10, distancia = true),
    CLAVAO("Clavão", 1, 8),
    MACHADO_DE_GUERRA("Machado de Guerra", 1, 12, acuidade = true),
    MARRETA("Marreta", 1, 12),
    ESPADA_LARGA("Espada Larga", 1, 8),
    MALHO("Malho", 2, 6),
    ALABARDA("Alabarda", 1, 10);

    val atributoDeCombate: Atributo
        get() = if (distancia || acuidade) Atributo.DESTREZA else Atributo.FORCA
}

/** Retorna (quantidadeDeDados, bônusDeDano) da arma, escolhendo Força vs. Destreza conforme o tipo. */
fun Arma.danoComModificadores(modForca: Int, modDestreza: Int): Pair<Int, Int> {
    val mod = if (atributoDeCombate == Atributo.DESTREZA) modDestreza else modForca
    return if (danoEmDano) quantidadeDados to facesDano + mod else 0 to 1
}