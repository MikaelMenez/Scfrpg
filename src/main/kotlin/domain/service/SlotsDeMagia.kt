package domain.service

import domain.model.Classe

/**
 * Regras de conjuração de D&D 5e: conjuradores plenos, metade (Paladino/Patrulheiro)
 * e pacto (Bruxo). Tabelas oficiais de espaços de magia por nível.
 */
object SlotsDeMagia {

    private val conjuradoresPlenos = setOf(
        Classe.BARDO, Classe.CLERIGO, Classe.DRUIDA, Classe.FEITICEIRO, Classe.MAGO
    )

    private val conjuradoresMetade = setOf(Classe.PALADINO, Classe.PATRULHEIRO)

    // [círculo 1..9] por nível de conjurador (0 = não conjurador ainda)
    private val TABELA_SLOTS = arrayOf(
        intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0),
        intArrayOf(2, 0, 0, 0, 0, 0, 0, 0, 0),
        intArrayOf(3, 0, 0, 0, 0, 0, 0, 0, 0),
        intArrayOf(4, 2, 0, 0, 0, 0, 0, 0, 0),
        intArrayOf(4, 3, 0, 0, 0, 0, 0, 0, 0),
        intArrayOf(4, 3, 2, 0, 0, 0, 0, 0, 0),
        intArrayOf(4, 3, 3, 0, 0, 0, 0, 0, 0),
        intArrayOf(4, 3, 3, 1, 0, 0, 0, 0, 0),
        intArrayOf(4, 3, 3, 2, 0, 0, 0, 0, 0),
        intArrayOf(4, 3, 3, 3, 1, 0, 0, 0, 0),
        intArrayOf(4, 3, 3, 3, 2, 0, 0, 0, 0),
        intArrayOf(4, 3, 3, 3, 2, 1, 0, 0, 0),
        intArrayOf(4, 3, 3, 3, 2, 1, 0, 0, 0),
        intArrayOf(4, 3, 3, 3, 2, 1, 1, 0, 0),
        intArrayOf(4, 3, 3, 3, 2, 1, 1, 0, 0),
        intArrayOf(4, 3, 3, 3, 2, 1, 1, 1, 0),
        intArrayOf(4, 3, 3, 3, 2, 1, 1, 1, 0),
        intArrayOf(4, 3, 3, 3, 2, 1, 1, 1, 1),
        intArrayOf(4, 3, 3, 3, 2, 1, 1, 1, 1),
        intArrayOf(4, 3, 3, 3, 2, 1, 1, 2, 1),
        intArrayOf(4, 3, 3, 3, 2, 1, 1, 2, 2)
    )

    // Pacto do Bruxo: (nível do pacto) -> número de espaços no nível de magia do pacto
    private val TABELA_PACTO = mapOf(
        1 to (1 to 1),
        2 to (1 to 1),
        3 to (2 to 2),
        4 to (2 to 2),
        5 to (3 to 2),
        6 to (3 to 2),
        7 to (4 to 2),
        8 to (4 to 2),
        9 to (5 to 2),
        10 to (5 to 2),
        11 to (5 to 3),
        12 to (5 to 3),
        13 to (5 to 3),
        14 to (5 to 3),
        15 to (5 to 3),
        16 to (5 to 3),
        17 to (5 to 4),
        18 to (5 to 4),
        19 to (5 to 4),
        20 to (5 to 4)
    )

    fun ehConjurador(classe: Classe): Boolean =
        classe in conjuradoresPlenos || classe in conjuradoresMetade || classe == Classe.BRUXO

    fun ehConjuradorPleno(classe: Classe): Boolean = classe in conjuradoresPlenos

    fun ehConjuradorMetade(classe: Classe): Boolean = classe in conjuradoresMetade

    fun ehConjuradorDePacto(classe: Classe): Boolean = classe == Classe.BRUXO

    fun nivelDeConjuracao(classe: Classe, nivelDeClasse: Int): Int = when {
        classe in conjuradoresPlenos -> nivelDeClasse
        classe in conjuradoresMetade -> (nivelDeClasse + 1) / 2
        classe == Classe.BRUXO -> TABELA_PACTO[nivelDeClasse.coerceIn(1, 20)]?.first ?: 0
        else -> 0
    }

    /**
     * Retorna os espaços de magia disponíveis: Map<círculo do espaço, quantidade>.
     * Para o Bruxo, o círculo representa o nível do pacto.
     */
    fun slots(classe: Classe, nivelDeClasse: Int): Map<Int, Int> = when {
        classe in conjuradoresPlenos || classe in conjuradoresMetade -> {
            val nivelConj = nivelDeConjuracao(classe, nivelDeClasse)
            TABELA_SLOTS[nivelConj].withIndex()
                .filter { it.value > 0 }
                .associate { (it.index + 1) to it.value }
        }
        classe == Classe.BRUXO -> {
            val (nivelPacto, quantidade) = TABELA_PACTO[nivelDeClasse.coerceIn(1, 20)]
                ?: (1 to 0)
            mapOf(nivelPacto to quantidade)
        }
        else -> emptyMap()
    }

    /** Maior círculo de magia que a classe pode conjurar no nível informado. */
    fun circulos(classe: Classe, nivelDeClasse: Int): Int {
        if (!ehConjurador(classe)) return 0
        return if (classe == Classe.BRUXO) TABELA_PACTO[nivelDeClasse.coerceIn(1, 20)]?.first ?: 0
        else TABELA_SLOTS[nivelDeConjuracao(classe, nivelDeClasse)].lastIndexOfFirst { it > 0 } + 1
    }

    fun atributoDeConjuracao(classe: Classe): String = when (classe) {
        Classe.BARDO, Classe.BRUXO, Classe.FEITICEIRO, Classe.PALADINO -> "CAR"
        Classe.CLERIGO, Classe.DRUIDA, Classe.PATRULHEIRO -> "SAB"
        Classe.MAGO -> "INT"
        else -> "-"
    }

    private fun IntArray.lastIndexOfFirst(predicate: (Int) -> Boolean): Int {
        var indice = -1
        forEachIndexed { i, valor -> if (predicate(valor)) indice = i }
        return indice
    }
}