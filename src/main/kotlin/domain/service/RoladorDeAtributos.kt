package domain.service

import domain.model.DadoVirtual

object RoladorDeAtributos {

    const val ORCAMENTO_PONTOS = 27
    const val VALOR_MINIMO = 8
    const val VALOR_MAXIMO = 15

    private val CUSTOS = mapOf(
        8 to 0, 9 to 1, 10 to 2, 11 to 3, 12 to 4,
        13 to 5, 14 to 7, 15 to 9
    )

    fun rolar4d6DropLowest(): List<Int> {
        return (1..6).map {
            val dados = listOf(
                DadoVirtual.rolar(6, 0),
                DadoVirtual.rolar(6, 0),
                DadoVirtual.rolar(6, 0),
                DadoVirtual.rolar(6, 0)
            )
            dados.sortedDescending().take(3).sum()
        }
    }

    fun arrayFixo(): List<Int> {
        return listOf(15, 14, 13, 12, 10, 8)
    }

    fun custo(valor: Int): Int = CUSTOS[valor] ?: throw IllegalArgumentException(
        "Valor de atributo deve estar entre $VALOR_MINIMO e $VALOR_MAXIMO (recebido: $valor)"
    )

    fun custoTotal(distribuicao: List<Int>): Int = distribuicao.sumOf { custo(it) }

    fun pontosRestantes(distribuicao: List<Int>): Int = ORCAMENTO_PONTOS - custoTotal(distribuicao)

    fun compraDePontos(distribuicao: List<Int>): Boolean {
        return distribuicao.size == 6 &&
            distribuicao.all { it in VALOR_MINIMO..VALOR_MAXIMO } &&
            custoTotal(distribuicao) <= ORCAMENTO_PONTOS
    }

    fun podeAdicionar(valorAtual: Int, pontosRestantes: Int): Boolean {
        return valorAtual < VALOR_MAXIMO && custo(valorAtual + 1) <= pontosRestantes
    }

    fun podeDiminuir(valorAtual: Int): Boolean = valorAtual > VALOR_MINIMO
}