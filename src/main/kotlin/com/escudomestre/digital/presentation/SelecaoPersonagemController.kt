package com.escudomestre.digital.presentation

import com.escudomestre.digital.domain.model.Personagem
import com.escudomestre.digital.domain.repository.PersonagemRepository
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.geometry.Pos
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.ProgressBar
import javafx.scene.layout.FlowPane
import javafx.scene.layout.HBox
import javafx.scene.layout.StackPane
import javafx.scene.layout.VBox
import java.net.URL
import java.util.ResourceBundle

/**
 * Tela de seleção de fichas estilo "perfis" (como Netflix): grandes cards-pôster
 * em um grid centralizado, com avatar, nível, classe e barra de PV, além de um
 * card de ação para criar uma nova ficha.
 */
class SelecaoPersonagemController : Initializable {

    @FXML
    private lateinit var containerCards: FlowPane
    @FXML
    private lateinit var estadoVazio: Label

    private lateinit var app: MainApp
    private lateinit var personagemRepository: PersonagemRepository

    override fun initialize(location: URL?, resources: ResourceBundle?) = Unit

    fun inicializar(app: MainApp, personagemRepository: PersonagemRepository) {
        this.app = app
        this.personagemRepository = personagemRepository
        carregarFichas()
    }

    fun criarNovaFicha() {
        app.mostrarCriacao()
    }

    private fun carregarFichas() {
        val fichas = personagemRepository.listar()
        containerCards.children.clear()
        fichas.forEach { containerCards.children += criarCardPose(it) }
        containerCards.children += criarCardNovo()
        estadoVazio.isVisible = fichas.isEmpty()
        estadoVazio.isManaged = fichas.isEmpty()
    }

    /** Card-pôster (Netflix) de um personagem salvo. */
    private fun criarCardPose(personagem: Personagem): VBox {
        val inicial = personagem.nome.take(1).uppercase()
        val avatar = Label(inicial).apply {
            styleClass += "avatar-poster"
        }
        val nivel = Label("Nível ${personagem.nivel}").apply {
            styleClass += "badge badge-ambar"
        }
        val bannerNivel = StackPane(nivel).apply {
            alignment = Pos.TOP_RIGHT
            translateX = -14.0
            translateY = 10.0
        }
        val poster = StackPane(avatar, bannerNivel).apply {
            styleClass += "poster-ficha"
        }

        val nome = Label(personagem.nome).apply {
            styleClass += "nome-poster"
        }
        val sub = Label("${personagem.raca.rotulo} · ${personagem.classe.rotulo}").apply {
            styleClass += "texto-mutado"
        }

        val pvBarra = ProgressBar(proporcaoPv(personagem)).apply {
            styleClass += "progresso-pv"
            prefWidth = 220.0
            maxWidth = 220.0
        }
        val pv = Label("PV ${personagem.pontosDeVidaAtual}/${personagem.pontosDeVidaMaximo} · CA ${personagem.classeArmadura}")
            .apply { styleClass += "rotulo" }

        val jogar = Button("Jogar").apply {
            styleClass += "botao"
            styleClass += "botao-primario"
            maxWidth = Double.MAX_VALUE
        }
        jogar.setOnAction { app.mostrarPainel(personagem) }

        val corpo = VBox(8.0, nome, sub, pvBarra, pv, jogar).apply {
            alignment = Pos.CENTER_LEFT
            padding = javafx.geometry.Insets(0.0, 14.0, 14.0, 14.0)
        }

        return VBox(0.0, poster, corpo).apply {
            styleClass += "card"
            styleClass += "card-poster"
            prefWidth = 280.0
            setOnMouseClicked { app.mostrarPainel(personagem) }
        }
    }

    /** Card de ação para criar uma nova ficha (estilo "novo perfil"). */
    private fun criarCardNovo(): VBox {
        val mais = Label("+").apply { styleClass += "mais-perfil" }
        val poster = StackPane(mais).apply { styleClass += "poster-novo" }
        val rotulo = Label("Nova Ficha").apply { styleClass += "nome-poster" }
        return VBox(10.0, poster, rotulo).apply {
            styleClass += "card"
            styleClass += "card-poster"
            prefWidth = 280.0
            setOnMouseClicked { app.mostrarCriacao() }
        }
    }

    private fun proporcaoPv(personagem: Personagem): Double {
        val maximo = personagem.pontosDeVidaMaximo
        return if (maximo <= 0) 0.0 else (personagem.pontosDeVidaAtual.toDouble() / maximo).coerceIn(0.0, 1.0)
    }
}
