package com.escudomestre.digital.domain.model

import java.util.UUID

/**
 * Entidade central do domínio (DDD): ficha de personagem D&D 5e com os seis atributos,
 * raça, classe e equipamento, e os comportamentos de aplicar dano, curar e subir de
 * nível (Seção 7.2), além do ciclo de vida em combate (Seção 7.4).
 *
 * As estatísticas derivadas (modificadores, bônus de proficiência, Classe de Armadura e
 * bônus de ataque) são calculadas a partir dos atributos, raça, classe e equipamento,
 * seguindo as regras do SRD 5e.
 */
class Personagem(
    val id: String = UUID.randomUUID().toString(),
    val nome: String,
    val raca: Raca,
    val classe: ClasseDePersonagem,
    var nivel: Int = 1,
    val atributos: MutableMap<Atributo, Int>,
    var pontosDeVidaAtual: Int,
    var pontosDeVidaMaximo: Int,
    var armaEquipada: Arma? = null,
    var armaduraEquipada: Armadura = Armadura.SEM_ARMADURA,
    var escudoEquipado: Boolean = false,
    var estado: EstadoCombate = EstadoCombate.AGUARDANDO_INICIATIVA,
) {

    /** Log de rolagens gerado por este personagem (Personagem ..> Historico : gera). */
    val historico: Historico = Historico()

    private val itens = mutableListOf<Item>()
    private val magias = mutableListOf<Magia>()

    val inventario: List<Item> get() = itens.toList()
    val grimorio: List<Magia> get() = magias.toList()

    fun adicionarItem(item: Item) {
        itens.add(item)
    }

    fun adicionarMagia(magia: Magia) {
        magias.add(magia)
    }

    fun valorDe(atributo: Atributo): Int = atributos.getValue(atributo)

    fun modificadorDe(atributo: Atributo): Int = modificadorDeAtributo(valorDe(atributo))

    val bonusProficiencia: Int get() = bonusDeProficiencia(nivel)

    /**
     * Atributo usado nas rolagens de ataque e dano com a arma equipada: Destreza para
     * armas à distância ou de acuidade, Força para as demais (SRD 5e).
     */
    val atributoDeCombate: Atributo
        get() {
            val arma = armaEquipada
            return if (arma != null && (arma.distancia || arma.acuidade)) {
                Atributo.DESTREZA
            } else {
                Atributo.FORCA
            }
        }

    /** Bônus de ataque: proficiência (se proficiente na arma) + modificador de atributo. */
    val modificadorAtaque: Int
        get() {
            val arma = armaEquipada ?: return 0
            val bonusProficienciaArmada =
                if (classe.ehProficienteEmArma(arma)) bonusProficiencia else 0
            return bonusProficienciaArmada + modificadorDe(atributoDeCombate)
        }

    /** Classe de Armadura: base da armadura + Destreza (limitada) + escudo (SRD 5e). */
    val classeArmadura: Int
        get() {
            val modDestreza = modificadorDe(Atributo.DESTREZA)
            val limite = armaduraEquipada.maxModDestreza
            val bonusDestreza = if (limite == null) modDestreza else modDestreza.coerceIn(0, limite)
            val escudo = if (escudoEquipado) Armadura.ESCUDO.caBase else 0
            return armaduraEquipada.caBase + bonusDestreza + escudo
        }

    /**
     * Aplica [valor] de dano aos pontos de vida atuais, sem reduzi-los abaixo de zero.
     * Quando os pontos de vida chegam a zero, a entidade transita para DERROTADO
     * a partir de qualquer estado (Seção 7.4).
     */
    fun aplicarDano(valor: Int) {
        require(valor >= 0) { "valor de dano não pode ser negativo" }
        pontosDeVidaAtual = (pontosDeVidaAtual - valor).coerceAtLeast(0)
        if (pontosDeVidaAtual == 0) {
            estado = EstadoCombate.DERROTADO
        }
    }

    /** Cura [valor] de pontos de vida, sem ultrapassar o máximo. */
    fun curar(valor: Int) {
        require(valor >= 0) { "valor de cura não pode ser negativo" }
        pontosDeVidaAtual = (pontosDeVidaAtual + valor).coerceAtMost(pontosDeVidaMaximo)
    }

    /** Incrementa o nível do personagem. */
    fun subirNivel() {
        nivel += 1
    }

    /** Transição AguardandoIniciativa -> EmTurno: ordem de iniciativa atingida. */
    fun iniciarTurno() {
        estado = transitar(estado, EstadoCombate.EM_TURNO)
    }

    /** Transição EmTurno -> AguardandoIniciativa: ação encerrada / passar turno. */
    fun passarTurno() {
        estado = transitar(estado, EstadoCombate.AGUARDANDO_INICIATIVA)
    }

    /** Transição EmTurno -> AturdidoIncapacitado: efeito de status aplicado. */
    fun aplicarStatusAturdido() {
        estado = transitar(estado, EstadoCombate.ATURDIDO_INCAPACITADO)
    }

    /** Transição AturdidoIncapacitado -> AguardandoIniciativa: efeito expira. */
    fun expirarStatusAturdido() {
        estado = transitar(estado, EstadoCombate.AGUARDANDO_INICIATIVA)
    }

    private fun transitar(de: EstadoCombate, para: EstadoCombate): EstadoCombate {
        if (de == EstadoCombate.DERROTADO) {
            throw IllegalStateException("entidade derrotada não muda de estado")
        }
        val permitidas = TRANSICOES[de].orEmpty()
        require(para in permitidas) { "transição inválida de estado: $de -> $para" }
        return para
    }

    private companion object {
        val TRANSICOES: Map<EstadoCombate, List<EstadoCombate>> = mapOf(
            EstadoCombate.AGUARDANDO_INICIATIVA to listOf(EstadoCombate.EM_TURNO),
            EstadoCombate.EM_TURNO to listOf(
                EstadoCombate.AGUARDANDO_INICIATIVA,
                EstadoCombate.ATURDIDO_INCAPACITADO,
            ),
            EstadoCombate.ATURDIDO_INCAPACITADO to listOf(EstadoCombate.AGUARDANDO_INICIATIVA),
            EstadoCombate.DERROTADO to emptyList(),
        )
    }
}
