package com.escudomestre.digital.presentation

import com.escudomestre.digital.application.ObservadorDeMudanca
import com.escudomestre.digital.application.SimuladorDeCombate
import com.escudomestre.digital.domain.model.Personagem
import com.escudomestre.digital.domain.repository.PersonagemRepository
import com.escudomestre.digital.domain.service.Vantagem
import javafx.collections.FXCollections
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.Scene
import javafx.scene.control.ListView
import javafx.scene.control.TextField
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyCodeCombination
import javafx.scene.input.KeyCombination
import java.net.URL
import java.util.ResourceBundle

/**
 * Controlador do Painel de Jogo (Seção 6.6). Apresenta as fichas, coordena as ações
 * de mesa (rolar ataque, rolar dano, aplicar dano) com o SimuladorDeCombate e atua
 * como observador (Observer) das mudanças de estado (RNF02).
 */
class PainelDeJogoController : Initializable, ObservadorDeMudanca {

    enum class AcaoDeMesa {
        ROLAR_ATAQUE,
        ROLAR_DANO,
        APLICAR_DANO,
    }

    @FXML
    private lateinit var listaFichas: ListView<Personagem>
    @FXML
    private lateinit var campoNome: TextField
    @FXML
    private lateinit var campoPv: TextField
    @FXML
    private lateinit var campoValorDano: TextField
    @FXML
    private lateinit var campoResultadoRolagem: TextField

    private lateinit var simulador: SimuladorDeCombate
    private lateinit var personagemRepository: PersonagemRepository

    private val fichas = FXCollections.observableArrayList<Personagem>()

    /** Atalhos de teclado configuráveis das ações de mesa (RNF05). */
    private val atalhos = mutableMapOf<AcaoDeMesa, KeyCombination>()

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        configurarAtalhosPadrao()
        listaFichas.selectionModel.selectedItemProperty().addListener { _, _, selecionado ->
            exibirPersonagem(selecionado)
        }
    }

    /** Liga o controlador ao simulador e ao repositório, e carrega as fichas (RU01). */
    fun inicializar(simulador: SimuladorDeCombate, personagemRepository: PersonagemRepository) {
        this.simulador = simulador
        this.personagemRepository = personagemRepository
        listaFichas.items = fichas
        simulador.registrarObservador(this)
        carregarFichas()
    }

    fun rolarAtaque() {
        val personagem = personagemSelecionado() ?: return
        val resultado = simulador.rolarAtaque(personagem, Vantagem.NENHUMA)
        campoResultadoRolagem.text = "Ataque: ${resultado.resultado}"
    }

    fun rolarDano() {
        val personagem = personagemSelecionado() ?: return
        val resultado = simulador.rolarDano(personagem, faces = 8)
        campoResultadoRolagem.text = "Dano: ${resultado.resultado}"
    }

    fun aplicarDano() {
        val personagem = personagemSelecionado() ?: return
        val valor = campoValorDano.text.toIntOrNull() ?: 1
        simulador.processarDano(personagem, valor)
    }

    /** Configura ou reconfigura o atalho de uma ação de mesa (RNF05). */
    fun definirAtalho(acao: AcaoDeMesa, combinacao: KeyCombination) {
        atalhos[acao] = combinacao
    }

    /** Registra os atalhos configurados nos aceleradores da [scene]. */
    fun aplicarAtalhos(scene: Scene) {
        scene.accelerators.clear()
        atalhos.forEach { (acao, combinacao) ->
            scene.accelerators[combinacao] = Runnable { executar(acao) }
        }
    }

    override fun notificarMudancaDeEstado(personagem: Personagem) {
        carregarFichas()
        if (listaFichas.selectionModel.selectedItem?.id == personagem.id) {
            exibirPersonagem(personagem)
        }
    }

    private fun configurarAtalhosPadrao() {
        atalhos[AcaoDeMesa.ROLAR_ATAQUE] = KeyCodeCombination(KeyCode.F5)
        atalhos[AcaoDeMesa.ROLAR_DANO] = KeyCodeCombination(KeyCode.F6)
        atalhos[AcaoDeMesa.APLICAR_DANO] = KeyCodeCombination(KeyCode.F7)
    }

    private fun executar(acao: AcaoDeMesa) {
        when (acao) {
            AcaoDeMesa.ROLAR_ATAQUE -> rolarAtaque()
            AcaoDeMesa.ROLAR_DANO -> rolarDano()
            AcaoDeMesa.APLICAR_DANO -> aplicarDano()
        }
    }

    private fun personagemSelecionado(): Personagem? =
        listaFichas.selectionModel.selectedItem

    private fun exibirPersonagem(personagem: Personagem?) {
        campoNome.text = personagem?.nome ?: ""
        campoPv.text = personagem?.pontosDeVidaAtual?.toString() ?: ""
    }

    private fun carregarFichas() {
        fichas.setAll(personagemRepository.listar())
    }
}
