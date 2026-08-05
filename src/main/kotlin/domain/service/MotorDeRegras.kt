package domain.service

import domain.model.DadoVirtual
import domain.model.bonusDeProficiencia
import domain.model.modificadorDeAtributo

object MotorDeRegras {

    fun rolarComVantagem(faces: Int, modificador: Int = 0): Int {
        val r1 = DadoVirtual.rolar(faces, 0)
        val r2 = DadoVirtual.rolar(faces, 0)
        return maxOf(r1, r2) + modificador
    }

    fun rolarComDesvantagem(faces: Int, modificador: Int = 0): Int {
        val r1 = DadoVirtual.rolar(faces, 0)
        val r2 = DadoVirtual.rolar(faces, 0)
        return minOf(r1, r2) + modificador
    }

    fun rolarAtaque(modificador: Int, temVantagem: Boolean = false, temDesvantagem: Boolean = false): Int {
        return when {
            temVantagem && !temDesvantagem -> rolarComVantagem(20, modificador)
            temDesvantagem && !temVantagem -> rolarComDesvantagem(20, modificador)
            else -> DadoVirtual.rolar(20, modificador)
        }
    }

    fun rolarDano(faces: Int, quantidade: Int, modificador: Int): Int {
        var total = 0
        repeat(quantidade) {
            total += DadoVirtual.rolar(faces, 0)
        }
        return total + modificador
    }

    /** Dano desarmado (regra 5e): sempre 1 + modificador de Força. */
    fun danoDesarmado(modificadorForca: Int): Int = 1 + modificadorForca

    /**
     * Testa um ataque contra uma CA (regra 5e):
     * - natural 20 sempre acerta e é crítico (dano dobrado);
     * - natural 1 sempre erra;
     * - caso contrário, acerta se d20 + bônus >= CA.
     */
    fun testarAtaque(
        bonusAtaque: Int,
        caAlvo: Int,
        temVantagem: Boolean = false,
        temDesvantagem: Boolean = false,
        valorDoDado: Int = DadoVirtual.rolar(20, 0),
    ): ResultadoDeAtaque {
        val valor = valorDoDado
        val total = valor + bonusAtaque
        return when {
            valor == 20 -> ResultadoDeAtaque(acertou = true, critico = true, valorDoDado = valor, total = total)
            valor == 1 -> ResultadoDeAtaque(acertou = false, critico = false, valorDoDado = valor, total = total)
            total >= caAlvo -> ResultadoDeAtaque(acertou = true, critico = false, valorDoDado = valor, total = total)
            else -> ResultadoDeAtaque(acertou = false, critico = false, valorDoDado = valor, total = total)
        }
    }

    /** Dano final: normal ou dobrado em caso de crítico. */
    fun aplicarDanoComCritico(dano: Int, critico: Boolean): Int = if (critico) dano * 2 else dano

    fun calcularModificador(atributo: Int): Int = modificadorDeAtributo(atributo)

    fun bonusDeProficienciaPorNivel(nivel: Int): Int = bonusDeProficiencia(nivel)
}

/** Resultado de um teste de ataque (d20 contra CA). */
data class ResultadoDeAtaque(
    val acertou: Boolean,
    val critico: Boolean,
    val valorDoDado: Int,
    val total: Int,
)
