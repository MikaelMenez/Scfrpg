package presentation

import infrastructure.persistence.SQLiteDatabaseFactory
import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.stage.Stage

class MainApp : Application() {

    override fun start(primaryStage: Stage) {
        try {
            SQLiteDatabaseFactory.conectar("escudo.db")
        } catch (e: Exception) {
            println("Banco não encontrado. Criando novo...")
            SQLiteDatabaseFactory.conectar("escudo.db")
        }

        val loader = FXMLLoader(javaClass.getResource("/fxml/menu_inicial.fxml"))
        val root: Parent = loader.load()
        val scene = Scene(root, 1366.0, 768.0)
        scene.stylesheets.add(javaClass.getResource("/css/styles.css")?.toExternalForm())

        primaryStage.title = "O Escudo do Mestre Digital"
        primaryStage.scene = scene
        primaryStage.minWidth = 1366.0
        primaryStage.minHeight = 768.0
        primaryStage.show()
    }
}

fun main() {
    Application.launch(MainApp::class.java)
}
