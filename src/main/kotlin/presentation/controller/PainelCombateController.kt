package presentation.controller

import domain.model.*
import domain.service.*
import infrastructure.persistence.*
import javafx.application.Platform
import javafx.collections.FXCollections
import javafx.fxml.FXML
import javafx.scene.control.*
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle

class PainelCombateController : PersonagemObserver {

    @FXML private lateinit var comboPersonagem: ComboBox<Personagem>
    @FXML private lateinit var comboEstado: ComboBox<EstadoCombate>
    @FXML private lateinit var lblNome: Label
    @FXML private lateinit var lblPV: Label
    @FXML private lateinit var barraPV: Rectangle
    @FXML private lateinit var lblCA: Label
    @FXML private lateinit var txtModAtaque: TextField
    @FXML private lateinit var txtDano: TextField
    @FXML private lateinit var checkVantagem: CheckBox
    @FXML private lateinit var checkDesvantagem: CheckBox
    @FXML private lateinit var txtResultado: TextField
    @FXML private lateinit var listHistorico: ListView<String>
    @FXML private lateinit var btnRolarAtaque: Button
    @FXML private lateinit var btnRolarDano: Button
    @FXML private lateinit var btnAplicarDano: Button
    @FXML private lateinit var btnCurar: Button
    @FXML private lateinit var btnLevelUp: Button
    @FXML private lateinit var vboxHistorico: VBox

    private val personagemDAO = PersonagemDAO()
    private val historicoDAO = HistoricoDAO()
    private val simulador = SimuladorDeCombate()
    private var personagemAtual: Personagem? = null

    @FXML
    fun initialize() {
        carregarPersonagens()
        comboPersonagem.selectionModel.selectedItemProperty().addListener { _, _, novo ->
            novo?.let { selecionarPersonagem(it) }
        }

        comboPersonagem.setCellFactory { _ ->
            object : ListCell<Personagem>() {
                override fun updateItem(item: Personagem?, empty: Boolean) {
                    super.updateItem(item, empty)
                    text = if (empty || item == null) null else item.nome
                }
            }
        }
        comboPersonagem.buttonCell = comboPersonagem.cellFactory.call(null)

        comboEstado.items = FXCollections.observableArrayList(EstadoCombate.values().toList())
        comboEstado.valueProperty().addListener { _, _, novo ->
            val p = personagemAtual ?: return@addListener
            if (novo != null && novo != p.estadoCombate) {
                p.estadoCombate = novo
                personagemDAO.atualizar(p)
                atualizarUI(p)
            }
        }

        btnRolarAtaque.setOnAction { executarRolarAtaque() }
        btnRolarDano.setOnAction { executarRolarDano() }
        btnAplicarDano.setOnAction { executarAplicarDano() }
        btnCurar.setOnAction { executarCurar() }
        btnLevelUp.setOnAction { executarLevelUp() }

        // Atalhos de teclado RNF05
        btnRolarAtaque.sceneProperty().addListener { _, _, newScene ->
            newScene?.addEventFilter(KeyEvent.KEY_PRESSED) { event ->
                when (event.code) {
                    KeyCode.F5 -> executarRolarAtaque()
                    KeyCode.F6 -> executarRolarDano()
                    KeyCode.F7 -> executarAplicarDano()
                    else -> {}
                }
            }
        }
    }

    private fun carregarPersonagens() {
        val lista = personagemDAO.listarTodos()
        comboPersonagem.items = FXCollections.observableArrayList(lista)
    }

    private fun selecionarPersonagem(p: Personagem) {
        personagemAtual?.removerObserver(this)
        personagemAtual = p
        p.adicionarObserver(this)
        atualizarUI(p)
        carregarHistorico(p)
    }

    private fun atualizarUI(p: Personagem) {
        lblNome.text = "${p.nome} (${p.raca} ${p.classe} Nv.${p.nivel})"
        lblPV.text = "PV: ${p.pontosDeVidaAtual} / ${p.pontosDeVidaMaximo}"
        lblCA.text = "CA: ${p.classeArmadura}"

        if (comboEstado.value != p.estadoCombate) {
            comboEstado.value = p.estadoCombate
        }

        val ratio = if (p.pontosDeVidaMaximo > 0) {
            p.pontosDeVidaAtual.toDouble() / p.pontosDeVidaMaximo.toDouble()
        } else 0.0
        barraPV.width = 300.0 * ratio.coerceIn(0.0, 1.0)
        barraPV.fill = when {
            ratio <= 0.25 -> Color.DARKRED
            ratio <= 0.5 -> Color.ORANGE
            else -> Color.DARKGREEN
        }
    }

    private fun carregarHistorico(p: Personagem) {
        val registros = historicoDAO.listarPorPersonagem(p.id)
        val items = registros.map { "[${it.tipoRolagem}] Resultado: ${it.resultado} | ${java.time.Instant.ofEpochMilli(it.timestamp)}" }
        listHistorico.items = FXCollections.observableArrayList(items)
    }

    private fun executarRolarAtaque() {
        val p = personagemAtual ?: return
        val mod = txtModAtaque.text.toIntOrNull() ?: 0
        val resultado = simulador.rolarAtaqueParaPersonagem(p, mod, checkVantagem.isSelected, checkDesvantagem.isSelected)
        txtResultado.text = resultado.toString()
        salvarHistorico(p)
        carregarHistorico(p)
    }

    private fun executarRolarDano() {
        val p = personagemAtual ?: return
        // dano derivado da arma equipada (quantidade, faces, bônus) — não acumula entre rolagens
        val (quantidade, faces, bonus) = p.dadosDeDano()
        val resultado = simulador.rolarDanoParaPersonagem(p, faces, quantidade, bonus)
        // preenche o campo usado por "Aplicar Dano" com o resultado rolado
        txtDano.text = resultado.toString()
        txtResultado.text = resultado.toString()
        salvarHistorico(p)
        carregarHistorico(p)
    }

    private fun executarAplicarDano() {
        val p = personagemAtual ?: return
        val valor = txtDano.text.toIntOrNull() ?: 0
        simulador.aplicarDanoERegistrar(p, valor)
        personagemDAO.atualizar(p)
        atualizarUI(p)
        txtResultado.text = "Dano aplicado: ${valor} (PV restante ${p.pontosDeVidaAtual})"
        salvarHistorico(p)
        carregarHistorico(p)
    }

    private fun executarCurar() {
        val p = personagemAtual ?: return
        val valor = txtDano.text.toIntOrNull() ?: 0
        p.curar(valor)
        personagemDAO.atualizar(p)
        carregarHistorico(p)
    }

    private fun executarLevelUp() {
        val p = personagemAtual ?: return
        p.subirNivel()
        personagemDAO.atualizar(p)
        atualizarUI(p)
        Alert(Alert.AlertType.INFORMATION, "${p.nome} subiu para o nível ${p.nivel}!").showAndWait()
    }

    private fun salvarHistorico(p: Personagem) {
        p.historico.forEach { h ->
            try {
                historicoDAO.inserir(h)
            } catch (e: Exception) {
                // ignora duplicatas ou erros de inserção
            }
        }
    }

    override fun onPersonagemAlterado(personagem: Personagem) {
        if (personagem.id == personagemAtual?.id) {
            Platform.runLater { atualizarUI(personagem) }
        }
    }
}
