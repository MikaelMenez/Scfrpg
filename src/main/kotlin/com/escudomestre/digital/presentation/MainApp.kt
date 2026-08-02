package com.escudomestre.digital.presentation

import com.escudomestre.digital.application.SimuladorDeCombate
import com.escudomestre.digital.infrastructure.persistence.DatabaseFactory
import com.escudomestre.digital.infrastructure.persistence.PersonagemDAO
import com.escudomestre.digital.infrastructure.persistence.repositories.ExposedHistoricoRepository
import com.escudomestre.digital.infrastructure.persistence.repositories.ExposedPersonagemRepository
import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.stage.Stage

/**
 * Classe de entrada da interface JavaFX (Seção 6.6).
 * Inicializa a persistência local (SQLite), monta o SimuladorDeCombate e
 * carrega o Painel de Jogo a partir do FXML.
 */
class MainApp : Application() {

    override fun start(stage: Stage) {
        DatabaseFactory.init()

        val personagemRepository = ExposedPersonagemRepository(PersonagemDAO())
        val simulador = SimuladorDeCombate(
            personagemRepository = personagemRepository,
            historicoRepository = ExposedHistoricoRepository(),
        )

        val recursoFxml = requireNotNull(
            javaClass.getResource("/com/escudomestre/digital/presentation/PainelDeJogo.fxml"),
        ) { "FXML do Painel de Jogo não encontrado" }
        val loader = FXMLLoader(recursoFxml)
        val root = loader.load<Parent>()
        val controller = loader.getController<PainelDeJogoController>()
        controller.inicializar(simulador, personagemRepository)

        val scene = Scene(root, 1366.0, 768.0)
        scene.stylesheets += requireNotNull(
            javaClass.getResource("/com/escudomestre/digital/presentation/estilo.css"),
        ).toExternalForm()
        controller.aplicarAtalhos(scene)

        stage.title = "O Escudo do Mestre Digital"
        stage.minWidth = 1366.0
        stage.minHeight = 768.0
        stage.scene = scene
        stage.show()
    }
}
