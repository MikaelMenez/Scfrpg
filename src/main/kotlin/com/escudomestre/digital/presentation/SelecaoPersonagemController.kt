package com.escudomestre.digital.presentation

import com.escudomestre.digital.domain.model.Personagem
import com.escudomestre.digital.domain.repository.PersonagemRepository
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.ProgressBar
import javafx.scene.layout.FlowPane
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import java.net.URL
import java.util.ResourceBundle

/**
 * Tela de seleção de fichas (RU01): apresenta os personagens persistidos como
 * cards, permitindo abrir uma ficha na mesa ou criar uma nova.
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
        fichas.forEach { containerCards.children += criarCard(it) }
        estadoVazio.isVisible = fichas.isEmpty()
        estadoVazio.isManaged = fichas.isEmpty()
    }

    private fun criarCard(personagem: Personagem): VBox {
        val avatar = Label(personagem.nome.take(1).uppercase()).apply {
            styleClass += "avatar-ficha"
        }
        val nome = Label(personagem.nome).apply { styleClass += "titulo-card" }
        val detalhes = Label("${personagem.raca.rotulo} · ${personagem.classe.rotulo}")
            .apply { styleClass += "texto-mutado" }
        val cabecalho = HBox(12.0, avatar, VBox(2.0, nome, detalhes)).apply {
            alignment = javafx.geometry.Pos.CENTER_LEFT
        }

        val badge = Label("Nível ${personagem.nivel}").apply { styleClass += "badge badge-ambar" }

        val pvBarra = ProgressBar(proporcaoPv(personagem)).apply {
            styleClass += "progresso-pv"
            prefWidth = 220.0
            maxWidth = 220.0
        }
        val pvTexto = Label("PV ${personagem.pontosDeVidaAtual}/${personagem.pontosDeVidaMaximo}")
            .apply { styleClass += "rotulo" }
        val ca = Label("Classe de Armadura ${personagem.classeArmadura}").apply { styleClass += "texto-mutado" }

        val abrir = Button("Abrir ficha").apply { styleClass += "botao botao-primario" }
        abrir.setOnMouseClicked { evento ->
            evento.consume()
            app.mostrarPainel(personagem)
        }

        return VBox(10.0, cabecalho, badge, pvBarra, pvTexto, ca, abrir).apply {
            styleClass += "card card-ficha"
            prefWidth = 260.0
            setOnMouseClicked { app.mostrarPainel(personagem) }
        }
    }

    private fun proporcaoPv(personagem: Personagem): Double {
        val maximo = personagem.pontosDeVidaMaximo
        return if (maximo <= 0) 0.0 else (personagem.pontosDeVidaAtual.toDouble() / maximo).coerceIn(0.0, 1.0)
    }
}
