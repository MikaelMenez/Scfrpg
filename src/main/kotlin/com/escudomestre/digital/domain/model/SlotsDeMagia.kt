package com.escudomestre.digital.domain.model

/**
 * Tabela de slots de magia por classe e nível (SRD 5e/5.5).
 *
 * - Conjuradores plenos (Mago, Feiticeiro, Clérigo, Druida, Bardo): tabela completa.
 * - Meio conjuradores (Paladino, Patrulheiro): tabela plena com metade do nível
 *   (arredondada para cima) — o 1º nível não concede slots.
 * - Bruxo: Magia de Pacto — slots únicos recuperados em um descanso curto.
 * - Demais classes: nenhum slot (conjuração via subclasse, ignorada).
 */
object SlotsDeMagia {

    private val PLENO: Map<Int, Map<Int, Int>> = mapOf(
        1 to mapOf(1 to 2),
        2 to mapOf(1 to 3),
        3 to mapOf(1 to 4, 2 to 2),
        4 to mapOf(1 to 4, 2 to 3),
        5 to mapOf(1 to 4, 2 to 3, 3 to 2),
        6 to mapOf(1 to 4, 2 to 3, 3 to 3),
        7 to mapOf(1 to 4, 2 to 3, 3 to 3, 4 to 1),
        8 to mapOf(1 to 4, 2 to 3, 3 to 3, 4 to 2),
        9 to mapOf(1 to 4, 2 to 3, 3 to 3, 4 to 3, 5 to 1),
        10 to mapOf(1 to 4, 2 to 3, 3 to 3, 4 to 3, 5 to 2),
        11 to mapOf(1 to 4, 2 to 3, 3 to 3, 4 to 3, 5 to 2, 6 to 1),
        12 to mapOf(1 to 4, 2 to 3, 3 to 3, 4 to 3, 5 to 2, 6 to 1),
        13 to mapOf(1 to 4, 2 to 3, 3 to 3, 4 to 3, 5 to 2, 6 to 1, 7 to 1),
        14 to mapOf(1 to 4, 2 to 3, 3 to 3, 4 to 3, 5 to 2, 6 to 1, 7 to 1),
        15 to mapOf(1 to 4, 2 to 3, 3 to 3, 4 to 3, 5 to 2, 6 to 1, 7 to 1, 8 to 1),
        16 to mapOf(1 to 4, 2 to 3, 3 to 3, 4 to 3, 5 to 2, 6 to 1, 7 to 1, 8 to 1),
        17 to mapOf(1 to 4, 2 to 3, 3 to 3, 4 to 3, 5 to 2, 6 to 1, 7 to 1, 8 to 1, 9 to 1),
        18 to mapOf(1 to 4, 2 to 3, 3 to 3, 4 to 3, 5 to 3, 6 to 1, 7 to 1, 8 to 1, 9 to 1),
        19 to mapOf(1 to 4, 2 to 3, 3 to 3, 4 to 3, 5 to 3, 6 to 2, 7 to 1, 8 to 1, 9 to 1),
        20 to mapOf(1 to 4, 2 to 3, 3 to 3, 4 to 3, 5 to 3, 6 to 2, 7 to 2, 8 to 1, 9 to 1),
    )

    /** Magia de Pacto (Bruxo): quantidade de slots e círculo deles por nível. */
    private val PACTO: Map<Int, Pair<Int, Int>> = mapOf(
        1 to (1 to 1), 2 to (2 to 1),
        3 to (2 to 2), 4 to (2 to 2),
        5 to (2 to 3), 6 to (2 to 3),
        7 to (2 to 4), 8 to (2 to 4),
        9 to (2 to 5), 10 to (2 to 5),
        11 to (3 to 5), 12 to (3 to 5),
        13 to (3 to 5), 14 to (3 to 5),
        15 to (3 to 5), 16 to (3 to 5),
        17 to (4 to 5), 18 to (4 to 5),
        19 to (4 to 5), 20 to (4 to 5),
    )

    private val CONJURADORES_PLENOS = setOf(
        ClasseDePersonagem.MAGO,
        ClasseDePersonagem.FEITICEIRO,
        ClasseDePersonagem.CLERIGO,
        ClasseDePersonagem.DRUIDA,
        ClasseDePersonagem.BARDO,
    )

    private val MEIO_CONJURADORES = setOf(
        ClasseDePersonagem.PALADINO,
        ClasseDePersonagem.PATRULHEIRO,
    )

    /** Slots por círculo (mapa círculo → quantidade) para [classe] no [nivel]. */
    fun slots(classe: ClasseDePersonagem, nivel: Int): Map<Int, Int> = when {
        classe in CONJURADORES_PLENOS -> PLENO[nivel].orEmpty()
        classe in MEIO_CONJURADORES ->
            if (nivel < 2) emptyMap()
            else PLENO[nivelMeioConjurador(nivel)].orEmpty().filterKeys { it <= 5 }
        classe == ClasseDePersonagem.BRUXO -> {
            val (quantidade, circulo) = PACTO[nivel] ?: return emptyMap()
            mapOf(circulo to quantidade)
        }
        else -> emptyMap()
    }

    /** Círculos de magia conhecidos (para filtrar o grimório). */
    fun circulos(classe: ClasseDePersonagem, nivel: Int): Set<Int> {
        if (classe == ClasseDePersonagem.BRUXO) {
            val circulo = PACTO[nivel]?.second ?: return emptySet()
            return (1..circulo).toSet()
        }
        return slots(classe, nivel).keys
    }

    /** Nível pleno equivalente para meio conjuradores (nível/2 arredondado para cima). */
    private fun nivelMeioConjurador(nivel: Int): Int = (nivel + 1) / 2

    /** Atributo de conjuração da [classe] (usado no bônus de ataque e na CD de salvaguarda). */
    fun atributoDeConjuracao(classe: ClasseDePersonagem): Atributo? = when (classe) {
        ClasseDePersonagem.MAGO -> Atributo.INTELIGENCIA
        ClasseDePersonagem.CLERIGO, ClasseDePersonagem.DRUIDA -> Atributo.SABEDORIA
        ClasseDePersonagem.BARDO, ClasseDePersonagem.FEITICEIRO, ClasseDePersonagem.BRUXO -> Atributo.CARISMA
        ClasseDePersonagem.PALADINO, ClasseDePersonagem.PATRULHEIRO -> Atributo.SABEDORIA
        else -> null
    }
}
