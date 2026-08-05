package domain.service

import domain.model.*

class SimuladorDeCombate {

    fun processarDano(personagem: Personagem, valor: Int) {
        personagem.aplicarDano(valor)
    }

    fun rolarAtaqueParaPersonagem(personagem: Personagem, modificador: Int, temVantagem: Boolean = false, temDesvantagem: Boolean = false): Int {
        val resultado = MotorDeRegras.rolarAtaque(modificador, temVantagem, temDesvantagem)
        val evento = RolagemEvento(
            personagemId = personagem.id,
            tipoRolagem = "ATAQUE",
            resultado = resultado
        )
        personagem.historico.add(Historico.registrar(evento))
        return resultado
    }

    fun rolarDanoParaPersonagem(personagem: Personagem, faces: Int, quantidade: Int, modificador: Int): Int {
        val resultado = MotorDeRegras.rolarDano(faces, quantidade, modificador)
        val evento = RolagemEvento(
            personagemId = personagem.id,
            tipoRolagem = "DANO",
            resultado = resultado
        )
        personagem.historico.add(Historico.registrar(evento))
        return resultado
    }

    fun aplicarDanoERegistrar(personagem: Personagem, valor: Int) {
        processarDano(personagem, valor)
        val evento = RolagemEvento(
            personagemId = personagem.id,
            tipoRolagem = "APLICAR_DANO",
            resultado = valor
        )
        personagem.historico.add(Historico.registrar(evento))
    }

    /**
     * Resolve um ataque completo (5e): rola d20 + bônus contra a CA do alvo, e
     * aplica o dano da arma do atacante em caso de acerto (dobrado num crítico).
     * Retorna o resultado do teste.
     */
    fun atacar(
        atacante: Personagem,
        alvo: Personagem,
        temVantagem: Boolean = false,
        temDesvantagem: Boolean = false,
        valorDoDado: Int = DadoVirtual.rolar(20, 0),
    ): ResultadoDeAtaque {
        val resultado = MotorDeRegras.testarAtaque(
            bonusAtaque = atacante.modificadorAtaque(),
            caAlvo = alvo.caEfetiva(),
            temVantagem = temVantagem,
            temDesvantagem = temDesvantagem,
            valorDoDado = valorDoDado,
        )
        rolarAtaqueParaPersonagem(atacante, atacante.modificadorAtaque(), temVantagem, temDesvantagem)
        if (resultado.acertou) {
            val (quantidade, faces, bonus) = atacante.dadosDeDano()
            val dano = if (quantidade > 0) MotorDeRegras.rolarDano(faces, quantidade, bonus)
            else bonus.coerceAtLeast(0) + faces
            aplicarDanoERegistrar(alvo, MotorDeRegras.aplicarDanoComCritico(dano, resultado.critico))
        }
        return resultado
    }
}
