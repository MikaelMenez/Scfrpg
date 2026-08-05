package presentation.controller

import domain.model.*
import domain.service.CatalogoDeMagias
import domain.service.SlotsDeMagia
import infrastructure.persistence.*
import javafx.collections.FXCollections
import javafx.fxml.FXML
import javafx.scene.control.*
import javafx.scene.layout.VBox

class GerenciarFichasController : PersonagemObserver {

    @FXML private lateinit var listFichas: ListView<Personagem>
    @FXML private lateinit var txtNome: TextField
    @FXML private lateinit var txtRaca: TextField
    @FXML private lateinit var txtClasse: TextField
    @FXML private lateinit var txtNivel: TextField
    @FXML private lateinit var txtPVAtual: TextField
    @FXML private lateinit var txtPVMax: TextField
    @FXML private lateinit var txtCA: TextField
    @FXML private lateinit var btnSalvar: Button
    @FXML private lateinit var btnAdicionarItem: Button
    @FXML private lateinit var btnAdicionarMagia: Button
    @FXML private lateinit var vboxInventario: VBox
    @FXML private lateinit var vboxGrimorio: VBox

    private val personagemDAO = PersonagemDAO()
    private val itemDAO = ItemDAO()
    private val magiaDAO = MagiaDAO()
    private var personagemAtual: Personagem? = null

    @FXML
    fun initialize() {
        carregarFichas()
        listFichas.selectionModel.selectedItemProperty().addListener { _, _, novo ->
            novo?.let { carregarPersonagem(it) }
        }
        btnSalvar.setOnAction { salvarAlteracoes() }
        btnAdicionarItem.setOnAction { adicionarItem() }
        btnAdicionarMagia.setOnAction { adicionarMagia() }
    }

    private fun carregarFichas() {
        val fichas = personagemDAO.listarTodos()
        listFichas.items = FXCollections.observableArrayList(fichas)
        listFichas.setCellFactory { _ ->
            object : javafx.scene.control.ListCell<Personagem>() {
                override fun updateItem(item: Personagem?, empty: Boolean) {
                    super.updateItem(item, empty)
                    text = if (empty || item == null) null else item.nome
                }
            }
        }
    }

    private fun carregarPersonagem(p: Personagem) {
        personagemAtual?.removerObserver(this)
        personagemAtual = p
        p.adicionarObserver(this)

        txtNome.text = p.nome
        txtRaca.text = p.raca
        txtClasse.text = p.classe
        txtNivel.text = p.nivel.toString()
        txtPVAtual.text = p.pontosDeVidaAtual.toString()
        txtPVMax.text = p.pontosDeVidaMaximo.toString()
        txtCA.text = p.classeArmadura.toString()

        atualizarInventario(p)
        atualizarGrimorio(p)
    }

    private fun atualizarInventario(p: Personagem) {
        vboxInventario.children.clear()
        val itens = itemDAO.listarPorPersonagem(p.id)
        itens.forEach { item ->
            vboxInventario.children.add(Label("${item.nome} (x${item.quantidade})"))
        }
    }

    private fun atualizarGrimorio(p: Personagem) {
        vboxGrimorio.children.clear()
        val magias = magiaDAO.listarPorPersonagem(p.id)
        magias.forEach { magia ->
            val label = Label(criarEtiquetaMagia(magia))
            label.style = "preview"
            val contexto = ContextMenu()
            val preparar = MenuItem(if (magia.preparada) "Despreparar" else "Preparar")
            preparar.setOnAction {
                magia.preparada = !magia.preparada
                magiaDAO.atualizarStatus(magia.id, magia.preparada, magia.slotGasto)
                atualizarGrimorio(p)
            }
            val conjurar = MenuItem(if (magia.slotGasto) "Recuperar Slot" else "Conjurar (gastar slot)")
            conjurar.isDisable = magia.ehTruque
            conjurar.setOnAction {
                magia.slotGasto = !magia.slotGasto
                magiaDAO.atualizarStatus(magia.id, magia.preparada, magia.slotGasto)
                atualizarGrimorio(p)
            }
            val remover = MenuItem("Remover do Grimório")
            remover.setOnAction {
                magiaDAO.deletar(magia.id)
                atualizarGrimorio(p)
            }
            contexto.items.addAll(preparar, conjurar, remover)
            label.onContextMenuRequested = javafx.event.EventHandler { event ->
                contexto.show(label, event.screenX, event.screenY)
            }
            vboxGrimorio.children.add(label)
        }
    }

    private fun criarEtiquetaMagia(magia: Magia): String {
        val estado = when {
            magia.ehTruque -> "Truque"
            magia.slotGasto -> "Slot gasto"
            else -> "Pronta"
        }
        return "${magia.nome} (Nv ${magia.nivel}) [${estado}]"
    }

    private fun salvarAlteracoes() {
        val p = personagemAtual ?: return
        p.nome = txtNome.text
        p.raca = txtRaca.text
        p.classe = txtClasse.text
        p.nivel = txtNivel.text.toInt()
        p.pontosDeVidaAtual = txtPVAtual.text.toInt()
        p.pontosDeVidaMaximo = txtPVMax.text.toInt()
        p.classeArmadura = txtCA.text.toInt()
        personagemDAO.atualizar(p)
        p.notificarObservers()
        carregarFichas()
        Alert(Alert.AlertType.INFORMATION, "Ficha atualizada!").showAndWait()
    }

    private fun adicionarItem() {
        val p = personagemAtual ?: return
        val dialog = TextInputDialog("Espada Longa")
        dialog.headerText = "Nome do Item"
        val nome = dialog.showAndWait().orElse("")
        if (nome.isNotBlank()) {
            val item = Item(id = java.util.UUID.randomUUID().toString(), nome = nome, peso = 1.0, quantidade = 1)
            itemDAO.inserir(item, p.id)
            atualizarInventario(p)
        }
    }

    private fun adicionarMagia() {
        val p = personagemAtual ?: return
        val catalogo = CatalogoDeMagias.magias
        if (catalogo.isEmpty()) {
            Alert(Alert.AlertType.INFORMATION, "Catálogo de magias vazio.").showAndWait()
            return
        }
        val porRotulo = catalogo.associateBy { "${it.nome} (Nível ${it.nivel})" }
        val escolha = ChoiceDialog(porRotulo.keys.first(), porRotulo.keys.toList())
        escolha.headerText = "Selecione uma magia do SRD 5e"
        val rotulo = escolha.showAndWait().orElse("")
        val origem = porRotulo[rotulo] ?: return
        val magia = origem.copiaParaGrimorio(java.util.UUID.randomUUID().toString())
        magiaDAO.inserir(magia, p.id)
        atualizarGrimorio(p)
    }

    override fun onPersonagemAlterado(personagem: Personagem) {
        if (personagem.id == personagemAtual?.id) {
            javafx.application.Platform.runLater {
                txtPVAtual.text = personagem.pontosDeVidaAtual.toString()
                txtPVMax.text = personagem.pontosDeVidaMaximo.toString()
            }
        }
    }
}
