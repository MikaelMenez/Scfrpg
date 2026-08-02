package com.escudomestre.digital.domain.model

import java.util.UUID

/**
 * Entidade central do domínio (DDD): concentra os atributos de jogo e os comportamentos
 * de aplicar dano, curar e subir de nível (Seção 7.2), além do ciclo de vida em combate
 * modelado pelo diagrama de estados (Seção 7.4).
 *
 * Relaciona-se em cardinalidade um-para-muitos com Item (Inventario) e Magia (Grimorio),
 * e gera registros de rolagem na entidade Historico.
 */
class Personagem(
    val id: String = UUID.randomUUID().toString(),
    val nome: String,
    val raca: String,
    val classe: String,
    var nivel: Int = 1,
    var pontosDeVidaAtual: Int,
    var pontosDeVidaMaximo: Int,
    var classeArmadura: Int = 10,
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
