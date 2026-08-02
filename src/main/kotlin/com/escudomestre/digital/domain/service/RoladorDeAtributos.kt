package com.escudomestre.digital.domain.service

/**
 * Geração de pontuações de habilidade conforme as regras do SRD 5e:
 *
 *  - Rolagem: 4d6, descartando o menor dado de cada conjunto (AC de criação);
 *  - Array fixo: 15, 14, 13, 12, 10, 8;
 *  - Compra de pontos: 27 pontos, cada atributo parte de 8 e pode chegar a 15,
 *    com custo progressivo conforme a tabela do SRD.
 */
class RoladorDeAtributos(private val dadoVirtual: DadoVirtual = DadoVirtual()) {

    companion object {
        val ARRAY_FIXO = listOf(15, 14, 13, 12, 10, 8)
        val PONTOS_DISPONIVEIS = 27
        val VALOR_MINIMO_PONTO = 8
        val VALOR_MAXIMO_PONTO = 15
    }

    private val custoPorValor = mapOf(
        8 to 0, 9 to 1, 10 to 2, 11 to 3,
        12 to 4, 13 to 5, 14 to 7, 15 to 9,
    )

    /** Rola 4d6 descartando o menor, uma vez para cada atributo (6 valores). */
    fun rolar4d6DescartandoMenor(): List<Int> =
        (0 until 6).map { rolarUmConjunto() }

    /** Rola um conjunto de 4d6 e soma os três maiores valores. */
    fun rolarUmConjunto(): Int {
        val dados = (1..4).map { dadoVirtual.rolar(6) }
        return dados.sortedDescending().take(3).sum()
    }

    fun arrayFixo(): List<Int> = ARRAY_FIXO

    /** Custo em pontos para elevar um atributo a [valor] (tabela de compra do SRD). */
    fun custoDe(valor: Int): Int =
        custoPorValor[valor] ?: throw IllegalArgumentException("valor fora da faixa de compra: $valor")

    /**
     * Compra de pontos: distribui [pontos] entre seis atributos, respeitando a faixa
     * 8–15. [valores] deve conter exatamente 6 valores entre 8 e 15 cuja soma de custos
     * não ultrapasse [pontos]. Retorna true quando a distribuição é válida.
     */
    fun comprarPontos(valores: List<Int>, pontos: Int = PONTOS_DISPONIVEIS): Boolean {
        if (valores.size != 6) return false
        if (valores.any { it !in VALOR_MINIMO_PONTO..VALOR_MAXIMO_PONTO }) return false
        val gasto = valores.sumOf { custoDe(it) }
        return gasto <= pontos
    }

    /** Pontos ainda disponíveis para [valores] na compra de pontos (>= 0 se válido). */
    fun pontosRestantes(valores: List<Int>, pontos: Int = PONTOS_DISPONIVEIS): Int =
        pontos - valores.sumOf { custoDe(it) }
}
