package domain.service

import domain.model.EstadoCombate
import domain.model.Personagem

object MotorDeEscalonamento {

    fun ordenarIniciativa(personagens: List<Personagem>): List<Personagem> {
        return personagens.map { p ->
            val iniciativa = MotorDeRegras.rolarAtaque(0)
            p to iniciativa
        }.sortedByDescending { it.second }
            .map { it.first }
            .also { ordenados ->
                ordenados.forEachIndexed { index, p ->
                    p.estadoCombate = if (index == 0) EstadoCombate.EM_TURNO else EstadoCombate.AGUARDANDO_INICIATIVA
                }
            }
    }

    fun avancarTurno(ordenados: List<Personagem>, indiceAtual: Int): Pair<Personagem, Int> {
        val proximo = (indiceAtual + 1) % ordenados.size
        val atual = ordenados[indiceAtual]
        val seguinte = ordenados[proximo]

        if (atual.estadoCombate != EstadoCombate.DERROTADO && atual.estadoCombate != EstadoCombate.ATURDIDO_INCAPACITADO) {
            atual.estadoCombate = EstadoCombate.AGUARDANDO_INICIATIVA
        }
        seguinte.estadoCombate = EstadoCombate.EM_TURNO

        return seguinte to proximo
    }
}
