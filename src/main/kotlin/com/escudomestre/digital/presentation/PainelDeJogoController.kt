package com.escudomestre.digital.presentation

import com.escudomestre.digital.application.ObservadorDeMudanca
import com.escudomestre.digital.application.SimuladorDeCombate
import com.escudomestre.digital.domain.model.Atributo
import com.escudomestre.digital.domain.model.EstadoCombate
import com.escudomestre.digital.domain.model.Personagem
import com.escudomestre.digital.domain.repository.PersonagemRepository
import com.escudomestre.digital.domain.service.Vantagem
import javafx.collections.FXCollections
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.Scene
import javafx.scene.control.Label
import javafx.scene.control.ListCell
import javafx.scene.control.ListView
import javafx.scene.control.TextField
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyCodeCombination
import javafx.scene.input.KeyCombination
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import java.net.URL
import java.util.ResourceBundle

/**
 * Controlador do Painel de Jogo (Seção 6.6). Apresenta as fichas D&D 5e, coordena as
 * ações de mesa (atacar contra CA, rolar dano, aplicar dano) com o SimuladorDeCombate
 * e atua como observador (Observer) das mudanças de estado (RNF02).
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
    private lateinit var campoRacaClasse: TextField
    @FXML
    private lateinit var campoPv: TextField
    @FXML
    private lateinit var campoCa: TextField
    @FXML
    private lateinit var campoArma: TextField
    @FXML
    private lateinit var campoForca: Label
    @FXML
    private lateinit var campoDestreza: Label
    @FXML
    private lateinit var campoConstituicao: Label
    @FXML
    private lateinit var campoInteligencia: Label
    @FXML
    private lateinit var campoSabedoria: Label
    @FXML
    private lateinit var campoCarisma: Label
    @FXML
    private lateinit var campoCaAlvo: TextField
    @FXML
    private lateinit var campoValorDano: TextField
    @FXML
    private lateinit var campoResultadoRolagem: TextField

    private lateinit var app: MainApp
    private lateinit var simulador: SimuladorDeCombate
    private lateinit var personagemRepository: PersonagemRepository

    private val fichas = FXCollections.observableArrayList<Personagem>()

    /** Atalhos de teclado configuráveis das ações de mesa (RNF05). */
    private val atalhos = mutableMapOf<AcaoDeMesa, KeyCombination>()

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        configurarAtalhosPadrao()
        listaFichas.cellFactory = javafx.util.Callback { ListCellFicha() }
        listaFichas.selectionModel.selectedItemProperty().addListener { _, _, selecionado ->
            exibirPersonagem(selecionado)
        }
    }

    /** Liga o controlador ao aplicativo, simulador e repositório (RU01). */
    fun inicializar(
        app: MainApp,
        simulador: SimuladorDeCombate,
        personagemRepository: PersonagemRepository,
    ) {
        this.app = app
        this.simulador = simulador
        this.personagemRepository = personagemRepository
        listaFichas.items = fichas
        simulador.registrarObservador(this)
        carregarFichas()
    }

    /** Define a ficha atualmente em destaque no painel. */
    fun selecionar(personagem: Personagem) {
        val indice = fichas.indexOfFirst { it.id == personagem.id }
        if (indice >= 0) {
            listaFichas.selectionModel.select(indice)
            listaFichas.scrollTo(indice)
        }
    }

    /** Retorna à tela de seleção de fichas. */
    fun voltar() {
        simulador.removerObservador(this)
        app.mostrarSelecao()
    }

    /** Ataca contra a CA digitada usando as regras 5e (d20 + bônus vs CA). */
    fun atacarAlvo() {
        val personagem = personagemSelecionado() ?: return
        val caAlvo = campoCaAlvo.text.toIntOrNull() ?: return
        val resultado = simulador.testarAtaque(personagem, caAlvo)
        campoResultadoRolagem.text = when {
            resultado.critico -> "CRÍTICO! d20 ${resultado.rolagem.dadoBruto} → dano ${resultado.dano}"
            resultado.acertou -> "Acerto (d20 ${resultado.rolagem.resultado} ≥ CA $caAlvo) → dano ${resultado.dano}"
            else -> "Erro (d20 ${resultado.rolagem.resultado} < CA $caAlvo)"
        }
    }

    fun rolarDano() {
        val personagem = personagemSelecionado() ?: return
        val arma = personagem.armaEquipada
        val faces = arma?.facesDano ?: 6
        val modificador = if (arma == null) 0 else personagem.modificadorDe(personagem.atributoDeCombate)
        val resultado = simulador.rolarDano(personagem, faces = faces, modificador = modificador)
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
            AcaoDeMesa.ROLAR_ATAQUE -> atacarAlvo()
            AcaoDeMesa.ROLAR_DANO -> rolarDano()
            AcaoDeMesa.APLICAR_DANO -> aplicarDano()
        }
    }

    private fun personagemSelecionado(): Personagem? =
        listaFichas.selectionModel.selectedItem

    private fun exibirPersonagem(personagem: Personagem?) {
        campoNome.text = personagem?.nome ?: ""
        campoRacaClasse.text = personagem?.let { "${it.raca.rotulo} · ${it.classe.rotulo}" } ?: ""
        campoPv.text = personagem?.let { "${it.pontosDeVidaAtual}/${it.pontosDeVidaMaximo}" } ?: ""
        campoCa.text = personagem?.classeArmadura?.toString() ?: ""
        campoArma.text = personagem?.armaEquipada?.rotulo ?: "Sem arma (desarmado 1)"
        campoForca.text = personagem?.atributoLabel(Atributo.FORCA) ?: ""
        campoDestreza.text = personagem?.atributoLabel(Atributo.DESTREZA) ?: ""
        campoConstituicao.text = personagem?.atributoLabel(Atributo.CONSTITUICAO) ?: ""
        campoInteligencia.text = personagem?.atributoLabel(Atributo.INTELIGENCIA) ?: ""
        campoSabedoria.text = personagem?.atributoLabel(Atributo.SABEDORIA) ?: ""
        campoCarisma.text = personagem?.atributoLabel(Atributo.CARISMA) ?: ""
    }

    private fun Personagem.atributoLabel(atributo: Atributo): String =
        "${valorDe(atributo)} (${formatoBonus(modificadorDe(atributo))})"

    private fun formatoBonus(valor: Int): String = if (valor >= 0) "+$valor" else "$valor"

    private fun carregarFichas() {
        fichas.setAll(personagemRepository.listar())
    }

    /** Célula de ficha no painel: avatar, nome, raça/classe, nível e barra de PV. */
    private class ListCellFicha : ListCell<Personagem>() {
        override fun updateItem(personagem: Personagem?, vazio: Boolean) {
            super.updateItem(personagem, vazio)
            if (vazio || personagem == null) {
                text = null
                graphic = null
                return
            }
            val avatar = Label(personagem.nome.take(1).uppercase()).apply { styleClass += "avatar-ficha" }
            val nome = Label(personagem.nome).apply { styleClass += "titulo-card" }
            val detalhes = Label("${personagem.raca.rotulo} · ${personagem.classe.rotulo} · Nível ${personagem.nivel}")
                .apply { styleClass += "texto-mutado" }
            val pv = Label("PV ${personagem.pontosDeVidaAtual}/${personagem.pontosDeVidaMaximo} · CA ${personagem.classeArmadura}")
                .apply { styleClass += "rotulo" }
            val caixaTexto = VBox(4.0, nome, detalhes, pv)
            val estado = Label(personagem.estado.name.replace("_", " ")).apply {
                styleClass += "badge badge-estado"
                if (personagem.estado == EstadoCombate.DERROTADO) {
                    styleClass += "badge-estado-derrotado"
                }
            }
            val avatarCaixa = VBox(avatar).apply { alignment = javafx.geometry.Pos.TOP_CENTER }
            graphic = HBox(12.0, avatarCaixa, caixaTexto, estado)
            HBox.setHgrow(caixaTexto, Priority.ALWAYS)
            alignment = javafx.geometry.Pos.CENTER_LEFT
        }
    }
}
