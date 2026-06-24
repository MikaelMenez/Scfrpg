package org.example

import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.stage.Stage

// 1. Criamos a classe que estende o Application do JavaFX
class App : Application() {
    override fun start(stage: Stage) {
        try {
            // Carrega a sua tela inicial que movemos para a pasta resources
            val root = FXMLLoader.load<Parent>(javaClass.getResource("/telas/tela_inicial.fxml"))
            stage.scene = Scene(root)
            stage.title = "Nexus RPG"
            stage.show()
        } catch (e: Exception) {
            println("Erro ao carregar a tela inicial do JavaFX:")
            e.printStackTrace()
        }
    }
}

// 2. A função main do Kotlin que o Gradle chama, agora ligando o JavaFX
fun main(args: Array<String>) {
    Application.launch(App::class.java, *args)
}