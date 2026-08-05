package domain.service

import domain.model.*

object ConstrutorDeFicha {

    fun construir(
        nome: String,
        raca: Raca,
        classe: Classe,
        atributosBase: Map<String, Int>,
        arma: Arma = Arma.DESARMADO,
        armadura: Armadura = Armadura.NENHUMA,
        escudo: Boolean = false,
    ): Personagem {
        val bonusRacial = raca.bonus
        val atributosFinais = atributosBase.mapValues { (chave, valor) ->
            valor + (bonusRacial[chave] ?: 0)
        }

        val modificadorCON = MotorDeRegras.calcularModificador(atributosFinais["CON"] ?: 10)
        val hitDice = classe.hitDice
        val pvMaximo = hitDice + modificadorCON

        val id = java.util.UUID.randomUUID().toString()

        return Personagem(
            id = id,
            nome = nome,
            raca = raca.name,
            classe = classe.name,
            nivel = 1,
            pontosDeVidaAtual = pvMaximo,
            pontosDeVidaMaximo = pvMaximo,
            classeArmadura = armadura.calcularCA(
                MotorDeRegras.calcularModificador(atributosFinais["DES"] ?: 10),
                escudo
            ),
            atributos = atributosFinais.toMutableMap(),
            armaEquipada = arma,
            armaduraEquipada = armadura,
            escudoEquipado = escudo,
        )
    }

    fun calcularBonusInicialAtaque(atributoPrincipal: Int): Int {
        return MotorDeRegras.calcularModificador(atributoPrincipal)
    }
}
