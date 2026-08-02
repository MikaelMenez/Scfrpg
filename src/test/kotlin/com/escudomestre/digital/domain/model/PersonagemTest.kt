package com.escudomestre.digital.domain.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class PersonagemTest {

    private fun personagem(pvAtual: Int = 30, pvMax: Int = 30): Personagem =
        Personagem(
            nome = "Aragorn",
            raca = "Humano",
            classe = "Guerreiro",
            nivel = 3,
            pontosDeVidaAtual = pvAtual,
            pontosDeVidaMaximo = pvMax,
            classeArmadura = 16,
        )

    @Test
    fun `aplicar dano reduz os pontos de vida atuais`() {
        val p = personagem()
        p.aplicarDano(12)
        assertEquals(18, p.pontosDeVidaAtual)
    }

    @Test
    fun `aplicar dano nao reduz abaixo de zero`() {
        val p = personagem(pvAtual = 5)
        p.aplicarDano(20)
        assertEquals(0, p.pontosDeVidaAtual)
    }

    @Test
    fun `aplicar dano que zera a vida marca a entidade como derrotada`() {
        val p = personagem(pvAtual = 10)
        p.aplicarDano(10)
        assertEquals(0, p.pontosDeVidaAtual)
        assertEquals(EstadoCombate.DERROTADO, p.estado)
    }

    @Test
    fun `aplicar dano com valor negativo e rejeitado`() {
        assertFailsWith<IllegalArgumentException> { personagem().aplicarDano(-5) }
    }

    @Test
    fun `curar aumenta os pontos de vida atuais`() {
        val p = personagem(pvAtual = 10)
        p.curar(5)
        assertEquals(15, p.pontosDeVidaAtual)
    }

    @Test
    fun `curar nao ultrapassa o maximo`() {
        val p = personagem(pvAtual = 28)
        p.curar(10)
        assertEquals(30, p.pontosDeVidaAtual)
    }

    @Test
    fun `subir nivel incrementa o nivel`() {
        val p = personagem()
        p.subirNivel()
        assertEquals(4, p.nivel)
    }

    @Test
    fun `entidade entra em combate aguardando iniciativa`() {
        val p = personagem()
        assertEquals(EstadoCombate.AGUARDANDO_INICIATIVA, p.estado)
    }

    @Test
    fun `ordem de iniciativa atingida move de aguardando para em turno`() {
        val p = personagem()
        p.iniciarTurno()
        assertEquals(EstadoCombate.EM_TURNO, p.estado)
    }

    @Test
    fun `passar o turno retorna ao estado aguardando iniciativa`() {
        val p = personagem()
        p.iniciarTurno()
        p.passarTurno()
        assertEquals(EstadoCombate.AGUARDANDO_INICIATIVA, p.estado)
    }

    @Test
    fun `efeito de status aplicado deixa a entidade em turno aturdida`() {
        val p = personagem()
        p.iniciarTurno()
        p.aplicarStatusAturdido()
        assertEquals(EstadoCombate.ATURDIDO_INCAPACITADO, p.estado)
    }

    @Test
    fun `expiracao do efeito retorna a aguardando iniciativa`() {
        val p = personagem()
        p.iniciarTurno()
        p.aplicarStatusAturdido()
        p.expirarStatusAturdido()
        assertEquals(EstadoCombate.AGUARDANDO_INICIATIVA, p.estado)
    }

    @Test
    fun `entidade derrotada nao pode mudar de estado`() {
        val p = personagem(pvAtual = 3)
        p.aplicarDano(3)
        assertFailsWith<IllegalStateException> { p.iniciarTurno() }
        assertEquals(EstadoCombate.DERROTADO, p.estado)
    }

    @Test
    fun `transicao invalida e rejeitada`() {
        val p = personagem()
        assertFailsWith<IllegalArgumentException> { p.passarTurno() }
    }

    @Test
    fun `personagem possui itens no inventario um para muitos`() {
        val p = personagem()
        val item = Item(nome = "Espada Longa", peso = 2.5, quantidade = 1)
        p.adicionarItem(item)
        assertEquals(listOf(item), p.inventario)
    }

    @Test
    fun `personagem possui magias no grimorio um para muitos`() {
        val p = personagem()
        val magia = Magia(nome = "Bola de Fogo", nivel = 3)
        p.adicionarMagia(magia)
        assertEquals(listOf(magia), p.grimorio)
    }
}
