package com.escudomestre.digital.presentation

import com.escudomestre.digital.application.SimuladorDeCombate
import com.escudomestre.digital.domain.model.Personagem
import com.escudomestre.digital.domain.repository.HistoricoRepository
import com.escudomestre.digital.domain.repository.ItemRepository
import com.escudomestre.digital.domain.repository.MagiaRepository
import com.escudomestre.digital.domain.repository.PersonagemRepository
import com.escudomestre.digital.infrastructure.persistence.DatabaseFactory
import com.escudomestre.digital.infrastructure.persistence.PersonagemDAO
import com.escudomestre.digital.infrastructure.persistence.repositories.ExposedHistoricoRepository
import com.escudomestre.digital.infrastructure.persistence.repositories.ExposedItemRepository
import com.escudomestre.digital.infrastructure.persistence.repositories.ExposedMagiaRepository
import com.escudomestre.digital.infrastructure.persistence.repositories.ExposedPersonagemRepository
import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.stage.Stage

/**
 * Classe de entrada da interface JavaFX (Seção 6.6).
 * Inicializa a persistência local (SQLite), monta o SimuladorDeCombate e
 * orquestra a navegação entre as telas: Seleção de fichas → Criação de ficha
 * → Painel de Jogo.
 */
class MainApp : Application() {

    private lateinit var stage: Stage
    private lateinit var scene: Scene
    private lateinit var personagemRepository: PersonagemRepository
    private lateinit var itemRepository: ItemRepository
    private lateinit var magiaRepository: MagiaRepository
    private lateinit var simulador: SimuladorDeCombate
    private lateinit var historicoRepository: HistoricoRepository

    override fun start(stage: Stage) {
        DatabaseFactory.init()

        personagemRepository = ExposedPersonagemRepository(PersonagemDAO())
        itemRepository = ExposedItemRepository()
        magiaRepository = ExposedMagiaRepository()
        historicoRepository = ExposedHistoricoRepository()
        simulador = SimuladorDeCombate(
            personagemRepository = personagemRepository,
            historicoRepository = historicoRepository,
        )

        this.stage = stage
        stage.title = "O Escudo do Mestre Digital"
        stage.minWidth = 1366.0
        stage.minHeight = 768.0
        stage.show()

        mostrarSelecao()
    }

    /** Tela de seleção de fichas (RU01). */
    fun mostrarSelecao() {
        val loader = carregarLoader("SelecaoPersonagem.fxml")
        val controller = loader.getController<SelecaoPersonagemController>()
        controller.inicializar(this, personagemRepository)
        aplicarCena(loader.getRoot() as Parent)
    }

    /** Tela de criação de fichas (RU01). */
    fun mostrarCriacao() {
        val loader = carregarLoader("CriacaoPersonagem.fxml")
        val controller = loader.getController<CriacaoPersonagemController>()
        controller.inicializar(this, personagemRepository)
        aplicarCena(loader.getRoot() as Parent)
    }

    /** Painel de Jogo para a [personagem] selecionada (Seção 6.6). */
    fun mostrarPainel(personagem: Personagem) {
        val loader = carregarLoader("PainelDeJogo.fxml")
        val controller = loader.getController<PainelDeJogoController>()
        controller.inicializar(this, simulador, personagemRepository, itemRepository, magiaRepository, historicoRepository)
        controller.selecionar(personagem)
        aplicarCena(loader.getRoot() as Parent)
        controller.aplicarAtalhos(scene)
    }

    private fun carregarLoader(nomeFxml: String): FXMLLoader {
        val recursoFxml = requireNotNull(
            javaClass.getResource("/com/escudomestre/digital/presentation/$nomeFxml"),
        ) { "FXML $nomeFxml não encontrado" }
        return FXMLLoader(recursoFxml).apply { load<Parent>() }
    }

    private fun aplicarCena(root: Parent) {
        if (!::scene.isInitialized) {
            scene = Scene(root, 1366.0, 768.0)
            scene.stylesheets += requireNotNull(
                javaClass.getResource("/com/escudomestre/digital/presentation/estilo.css"),
            ).toExternalForm()
            stage.scene = scene
        } else {
            scene.root = root
        }
    }
}

/** Ponto de entrada da aplicação (gera a classe MainAppKt referenciada no Gradle). */
fun main() {
    Application.launch(MainApp::class.java)
}
