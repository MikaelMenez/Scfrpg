package com.escudomestre.digital.presentation

import com.escudomestre.digital.domain.model.Personagem
import com.escudomestre.digital.domain.repository.PersonagemRepository
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.Label
import javafx.scene.control.TextField
import java.net.URL
import java.util.ResourceBundle

/**
 * Tela de criação de fichas (RU01): valida os campos obrigatórios (AC 1.3)
 * e persiste um novo personagem antes de retornar à seleção.
 */
class CriacaoPersonagemController : Initializable {

    @FXML
    private lateinit var campoNome: TextField
    @FXML
    private lateinit var campoRaca: TextField
    @FXML
    private lateinit var campoClasse: TextField
    @FXML
    private lateinit var campoNivel: TextField
    @FXML
    private lateinit var campoPvMaximo: TextField
    @FXML
    private lateinit var campoPvAtual: TextField
    @FXML
    private lateinit var campoClasseArmadura: TextField
    @FXML
    private lateinit var campoModificadorAtaque: TextField
    @FXML
    private lateinit var labelErro: Label

    private lateinit var app: MainApp
    private lateinit var personagemRepository: PersonagemRepository

    override fun initialize(location: URL?, resources: ResourceBundle?) = Unit

    fun inicializar(app: MainApp, personagemRepository: PersonagemRepository) {
        this.app = app
        this.personagemRepository = personagemRepository
    }

    fun cancelar() {
        app.mostrarSelecao()
    }

    fun salvar() {
        val nome = campoNome.text.trim()
        if (nome.isEmpty()) {
            mostrarErro("O campo Nome é obrigatório (AC 1.3).")
            campoNome.requestFocus()
            return
        }

        val nivel = campoNivel.text.trim().toIntOrNull()
            ?: return mostrarErro("Nível deve ser um número inteiro.")
        val pvMaximo = campoPvMaximo.text.trim().toIntOrNull()
            ?: return mostrarErro("PV máximo deve ser um número inteiro.")
        val pvAtual = campoPvAtual.text.trim().toIntOrNull()
            ?: return mostrarErro("PV atual deve ser um número inteiro.")
        val classeArmadura = campoClasseArmadura.text.trim().toIntOrNull()
            ?: return mostrarErro("Classe de Armadura deve ser um número inteiro.")
        val modificadorAtaque = campoModificadorAtaque.text.trim().toIntOrNull()
            ?: return mostrarErro("Modificador de ataque deve ser um número inteiro.")

        if (pvMaximo <= 0) {
            mostrarErro("PV máximo deve ser maior que zero.")
            return
        }
        if (pvAtual < 0 || pvAtual > pvMaximo) {
            mostrarErro("PV atual deve estar entre 0 e o PV máximo.")
            return
        }
        if (nivel <= 0) {
            mostrarErro("Nível deve ser maior que zero.")
            return
        }

        val personagem = Personagem(
            nome = nome,
            raca = campoRaca.text.trim(),
            classe = campoClasse.text.trim(),
            nivel = nivel,
            pontosDeVidaAtual = pvAtual,
            pontosDeVidaMaximo = pvMaximo,
            classeArmadura = classeArmadura,
            modificadorAtaque = modificadorAtaque
        )
        personagemRepository.criar(personagem)
        ocultarErro()
        app.mostrarSelecao()
    }

    private fun mostrarErro(mensagem: String) {
        labelErro.text = mensagem
        labelErro.isVisible = true
        labelErro.isManaged = true
    }

    private fun ocultarErro() {
        labelErro.isVisible = false
        labelErro.isManaged = false
    }
}
