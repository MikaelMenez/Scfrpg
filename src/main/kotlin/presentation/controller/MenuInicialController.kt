package presentation.controller

import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.Alert
import javafx.scene.control.Button
import javafx.stage.Stage

class MenuInicialController {

    @FXML
    private lateinit var btnNovoHeroi: Button

    @FXML
    private lateinit var btnGerenciarFichas: Button

    @FXML
    private lateinit var btnPainelCombate: Button

    @FXML
    fun initialize() {
        btnNovoHeroi.setOnAction { abrirTela("/fxml/criar_heroi.fxml", "Criar Novo Herói") }
        btnGerenciarFichas.setOnAction { abrirTela("/fxml/gerenciar_fichas.fxml", "Gerenciar Fichas") }
        btnPainelCombate.setOnAction { abrirTela("/fxml/painel_combate.fxml", "Painel de Combate e Simulador") }
    }

    private fun abrirTela(fxml: String, titulo: String) {
        try {
            val loader = FXMLLoader(javaClass.getResource(fxml))
            val root: Parent = loader.load()
            val stage = Stage()
            stage.title = titulo
            stage.scene = Scene(root)
            stage.scene.stylesheets.add(javaClass.getResource("/css/styles.css")?.toExternalForm())
            stage.show()
        } catch (e: Exception) {
            Alert(Alert.AlertType.ERROR).apply {
                title = "Erro"
                headerText = "Falha ao abrir tela"
                contentText = e.message
                showAndWait()
            }
        }
    }
}
