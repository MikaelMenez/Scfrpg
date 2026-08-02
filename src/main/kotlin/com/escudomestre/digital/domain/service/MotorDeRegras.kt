package com.escudomestre.digital.domain.service

/**
 * Modo de rolagem aplicado pelo motor de regras sobre o PRNG (RF02).
 */
enum class Vantagem {
    /** Rolagem simples, uma única execução do PRNG. */
    NENHUMA,

    /** Executa duas rolagens e retorna o maior valor entre elas. */
    VANTAGEM,

    /** Executa duas rolagens e retorna o menor valor entre elas. */
    DESVANTAGEM,
}

/**
 * Resultado de uma rolagem processada pelo motor de regras.
 *
 * @param resultado valor final exibido (dado(s) + modificadores).
 * @param dados valores individuais sorteados pelo PRNG antes dos modificadores,
 *              1 valor para rolagem simples e 2 para vantagem/desvantagem.
 * @param dadoBruto valor do dado antes de qualquer modificador (d20 natural),
 *              usado para detectar críticos e falhas críticas nas regras do SRD 5e.
 */
data class ResultadoRolagem(
    val resultado: Int,
    val dados: List<Int>,
    val dadoBruto: Int = 0,
)

/**
 * Motor de regras de negócio (Regras de Negocio, Seção 6.6): processa modificadores
 * de atributos e aplica lógicas de vantagem/desvantagem sobre o DadoVirtual (RF02).
 */
class MotorDeRegras(private val dadoVirtual: DadoVirtual = DadoVirtual()) {

    fun rolarAtaque(
        modificador: Int,
        vantagem: Vantagem = Vantagem.NENHUMA,
    ): ResultadoRolagem = rolar(faces = 20, modificador = modificador, vantagem = vantagem)

    fun rolarDano(
        faces: Int,
        modificador: Int = 0,
        vantagem: Vantagem = Vantagem.NENHUMA,
    ): ResultadoRolagem = rolar(faces = faces, modificador = modificador, vantagem = vantagem)

    private fun rolar(
        faces: Int,
        modificador: Int,
        vantagem: Vantagem,
    ): ResultadoRolagem {
        if (vantagem == Vantagem.NENHUMA) {
            val bruto = dadoVirtual.rolar(faces)
            return ResultadoRolagem(
                resultado = bruto + modificador,
                dados = listOf(bruto + modificador),
                dadoBruto = bruto,
            )
        }

        val primeira = dadoVirtual.rolar(faces)
        val segunda = dadoVirtual.rolar(faces)
        val bruto = when (vantagem) {
            Vantagem.VANTAGEM -> maxOf(primeira, segunda)
            Vantagem.DESVANTAGEM -> minOf(primeira, segunda)
            Vantagem.NENHUMA -> error("caso tratado acima")
        }
        return ResultadoRolagem(
            resultado = bruto + modificador,
            dados = listOf(primeira + modificador, segunda + modificador),
            dadoBruto = bruto,
        )
    }
}
