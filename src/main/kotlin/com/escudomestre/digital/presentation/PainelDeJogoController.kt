package com.escudomestre.digital.presentation

import com.escudomestre.digital.application.ObservadorDeMudanca
import com.escudomestre.digital.application.SimuladorDeCombate
import com.escudomestre.digital.domain.model.Atributo
import com.escudomestre.digital.domain.model.CatalogoDeMagias
import com.escudomestre.digital.domain.model.CatalogoHabilidades
import com.escudomestre.digital.domain.model.EstadoCombate
import com.escudomestre.digital.domain.model.HabilidadeDeClasse
import com.escudomestre.digital.domain.model.Item
import com.escudomestre.digital.domain.model.Magia
import com.escudomestre.digital.domain.model.Personagem
import com.escudomestre.digital.domain.model.RolagemEvento
import com.escudomestre.digital.domain.model.SlotsDeMagia
import com.escudomestre.digital.domain.model.TipoRolagem
import com.escudomestre.digital.domain.repository.HistoricoRepository
import com.escudomestre.digital.domain.repository.ItemRepository
import com.escudomestre.digital.domain.repository.MagiaRepository
import com.escudomestre.digital.domain.repository.PersonagemRepository
import javafx.collections.FXCollections
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.ComboBox
import javafx.scene.control.Label
import javafx.scene.control.ListCell
import javafx.scene.control.ListView
import javafx.scene.control.TextField
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyCodeCombination
import javafx.scene.input.KeyCombination
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.layout.Region
import javafx.scene.layout.VBox
import javafx.util.StringConverter
import java.net.URL
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.ResourceBundle
import java.util.UUID

/**
 * Controlador do Painel de Jogo (Seção 6.6). Apresenta a ficha D&D 5.5 selecionada,
 * coordena as ações de mesa (atacar, rolar dano, aplicar dano) com o SimuladorDeCombate
 * e organiza em abas as habilidades de classe, o grimório (magias + slots) e o
 * inventário. Atua como observador (Observer) das mudanças de estado (RNF02).
 */
class PainelDeJogoController : Initializable, ObservadorDeMudanca {

    enum class AcaoDeMesa {
        ROLAR_ATAQUE,
        ROLAR_DANO,
        APLICAR_DANO,
    }

    @FXML private lateinit var listaFichas: ListView<Personagem>

    @FXML private lateinit var campoNome: TextField
    @FXML private lateinit var campoRacaClasse: TextField
    @FXML private lateinit var campoPv: TextField
    @FXML private lateinit var campoCa: TextField
    @FXML private lateinit var campoArma: TextField
    @FXML private lateinit var campoForca: Label
    @FXML private lateinit var campoDestreza: Label
    @FXML private lateinit var campoConstituicao: Label
    @FXML private lateinit var campoInteligencia: Label
    @FXML private lateinit var campoSabedoria: Label
    @FXML private lateinit var campoCarisma: Label
    @FXML private lateinit var campoCaAlvo: TextField
    @FXML private lateinit var campoValorDano: TextField
    @FXML private lateinit var campoResultadoRolagem: TextField

    @FXML private lateinit var labelConjuracao: Label
    @FXML private lateinit var labelSlots: Label
    @FXML private lateinit var listaHabilidades: ListView<HabilidadeDeClasse>

    @FXML private lateinit var labelSlotsGrimorio: Label
    @FXML private lateinit var comboMagia: ComboBox<Magia>
    @FXML private lateinit var listaMagias: ListView<Magia>

    @FXML private lateinit var campoItemNome: TextField
    @FXML private lateinit var campoItemPeso: TextField
    @FXML private lateinit var campoItemQuantidade: TextField
    @FXML private lateinit var labelPesoTotal: Label
    @FXML private lateinit var listaItens: ListView<Item>

    @FXML private lateinit var listaHistorico: ListView<RolagemEvento>

    private val magiasExibidas = FXCollections.observableArrayList<Magia>()
    private val itens = FXCollections.observableArrayList<Item>()
    private val habilidades = FXCollections.observableArrayList<HabilidadeDeClasse>()
    private val eventos = FXCollections.observableArrayList<RolagemEvento>()

    private lateinit var app: MainApp
    private lateinit var simulador: SimuladorDeCombate
    private lateinit var personagemRepository: PersonagemRepository
    private lateinit var itemRepository: ItemRepository
    private lateinit var magiaRepository: MagiaRepository
    private lateinit var historicoRepository: HistoricoRepository

    private val fichas = FXCollections.observableArrayList<Personagem>()
    private val atalhos = mutableMapOf<AcaoDeMesa, KeyCombination>()
    private var selecionadoId: String? = null

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        listaFichas.cellFactory = javafx.util.Callback { ListCellFicha() }
        listaFichas.selectionModel.selectedItemProperty().addListener { _, _, selecionado ->
            selecionadoId = selecionado?.id
            exibirPersonagem(selecionado)
        }
        listaMagias.items = magiasExibidas
        listaMagias.cellFactory = javafx.util.Callback { ListCellMagia() }
        listaItens.items = itens
        listaItens.cellFactory = javafx.util.Callback { ListCellItem() }
        listaHabilidades.items = habilidades
        listaHabilidades.cellFactory = javafx.util.Callback { ListCellHabilidade() }
        listaHistorico.items = eventos
        listaHistorico.cellFactory = javafx.util.Callback { ListCellHistorico() }
        comboMagia.setConverter(object : javafx.util.StringConverter<Magia>() {
            override fun toString(m: Magia?): String = if (m == null) "" else "${m.nome} (${m.rotuloNivel})"
            override fun fromString(s: String?): Magia? = null
        })
    }

    fun inicializar(
        app: MainApp,
        simulador: SimuladorDeCombate,
        personagemRepository: PersonagemRepository,
        itemRepository: ItemRepository,
        magiaRepository: MagiaRepository,
        historicoRepository: HistoricoRepository,
    ) {
        this.app = app
        this.simulador = simulador
        this.personagemRepository = personagemRepository
        this.itemRepository = itemRepository
        this.magiaRepository = magiaRepository
        this.historicoRepository = historicoRepository
        listaFichas.items = fichas
        simulador.registrarObservador(this)
        carregarFichas()
        carregarHistorico()
    }

    fun selecionar(personagem: Personagem) {
        val indice = fichas.indexOfFirst { it.id == personagem.id }
        if (indice >= 0) {
            listaFichas.selectionModel.select(indice)
            listaFichas.scrollTo(indice)
        }
    }

    fun voltar() {
        simulador.removerObservador(this)
        app.mostrarSelecao()
    }

    fun atacarAlvo() {
        val personagem = personagemSelecionado() ?: return
        val caAlvo = campoCaAlvo.text.toIntOrNull() ?: return
        val resultado = simulador.testarAtaque(personagem, caAlvo)
        campoResultadoRolagem.text = when {
            resultado.critico -> "CRÍTICO! d20 ${resultado.rolagem.dadoBruto} → dano ${resultado.dano}"
            resultado.acertou -> "Acerto (d20 ${resultado.rolagem.resultado} ≥ CA $caAlvo) → dano ${resultado.dano}"
            else -> "Erro (d20 ${resultado.rolagem.resultado} < CA $caAlvo)"
        }
        carregarHistorico()
    }

    fun rolarDano() {
        val personagem = personagemSelecionado() ?: return
        val arma = personagem.armaEquipada
        val faces = arma?.facesDano ?: 6
        val modificador = if (arma == null) 0 else personagem.modificadorDe(personagem.atributoDeCombate)
        val resultado = simulador.rolarDano(personagem, faces = faces, modificador = modificador)
        campoResultadoRolagem.text = "Dano: ${resultado.resultado}"
        carregarHistorico()
    }

    fun aplicarDano() {
        val personagem = personagemSelecionado() ?: return
        val valor = campoValorDano.text.toIntOrNull() ?: 1
        simulador.processarDano(personagem, valor)
    }

    fun adicionarItem() {
        val personagem = personagemSelecionado() ?: return
        val nome = campoItemNome.text.trim()
        if (nome.isEmpty()) return
        val peso = campoItemPeso.text.toDoubleOrNull() ?: 0.0
        val quantidade = (campoItemQuantidade.text.toIntOrNull() ?: 1).coerceAtLeast(1)
        itemRepository.criar(personagem.id, Item(nome = nome, peso = peso, quantidade = quantidade))
        campoItemNome.clear()
        carregarInventario(personagem)
    }

    fun adicionarMagia() {
        val personagem = personagemSelecionado() ?: return
        val modelo = comboMagia.value ?: return
        magiaRepository.criar(personagem.id, modelo.copy(id = UUID.randomUUID().toString()))
        carregarGrimorio(personagem)
    }

    /** Aciona o gatilho de nível (UC06): aumenta nível, PV máximos/atuais e atualiza habilidades/slots. */
    fun subirNivel() {
        val personagem = personagemSelecionado() ?: return
        val crescimento = personagem.classe.dadoDeVida / 2 + 1 + personagem.modificadorDe(Atributo.CONSTITUICAO)
        personagem.subirNivel()
        personagem.pontosDeVidaMaximo += crescimento
        personagem.pontosDeVidaAtual = (personagem.pontosDeVidaAtual + crescimento).coerceAtMost(personagem.pontosDeVidaMaximo)
        personagemRepository.atualizar(personagem)
        exibirPersonagem(personagem)
    }

    fun definirAtalho(acao: AcaoDeMesa, combinacao: KeyCombination) {
        atalhos[acao] = combinacao
    }

    fun aplicarAtalhos(scene: Scene) {
        scene.accelerators.clear()
        atalhos.forEach { (acao, combinacao) ->
            scene.accelerators[combinacao] = Runnable { executarAcao(acao) }
        }
    }

    override fun notificarMudancaDeEstado(personagem: Personagem) {
        if (selecionadoId == personagem.id) {
            exibirPersonagem(personagemSelecionado())
        }
        carregarHistorico()
    }

    private fun executarAcao(acao: AcaoDeMesa) {
        when (acao) {
            AcaoDeMesa.ROLAR_ATAQUE -> atacarAlvo()
            AcaoDeMesa.ROLAR_DANO -> rolarDano()
            AcaoDeMesa.APLICAR_DANO -> aplicarDano()
        }
    }

    private fun personagemSelecionado(): Personagem? = listaFichas.selectionModel.selectedItem

    private fun exibirPersonagem(personagem: Personagem?) {
        if (personagem == null) {
            limparCamposDeAtributo()
            habilidades.clear()
            magiasExibidas.clear()
            itens.clear()
            labelConjuracao.text = ""
            labelSlots.text = ""
            labelSlotsGrimorio.text = ""
            labelPesoTotal.text = ""
            return
        }
        campoNome.text = personagem.nome
        campoRacaClasse.text = "${personagem.raca.rotulo} · ${personagem.classe.rotulo}"
        campoPv.text = "${personagem.pontosDeVidaAtual}/${personagem.pontosDeVidaMaximo}"
        campoCa.text = personagem.classeArmadura.toString()
        campoArma.text = personagem.armaEquipada?.rotulo ?: "Sem arma (desarmado 1)"
        campoForca.text = rotuloDe(personagem, Atributo.FORCA)
        campoDestreza.text = rotuloDe(personagem, Atributo.DESTREZA)
        campoConstituicao.text = rotuloDe(personagem, Atributo.CONSTITUICAO)
        campoInteligencia.text = rotuloDe(personagem, Atributo.INTELIGENCIA)
        campoSabedoria.text = rotuloDe(personagem, Atributo.SABEDORIA)
        campoCarisma.text = rotuloDe(personagem, Atributo.CARISMA)

        carregarHabilidades(personagem)
        carregarGrimorio(personagem)
        carregarInventario(personagem)
    }

    private fun carregarHabilidades(personagem: Personagem) {
        habilidades.setAll(CatalogoHabilidades.habilidadesAteNivel(personagem.classe, personagem.nivel))

        val atributoConjuracao = SlotsDeMagia.atributoDeConjuracao(personagem.classe)
        if (atributoConjuracao == null) {
            labelConjuracao.text = "${personagem.classe.rotulo} não conjura magias."
            labelSlots.text = ""
        } else {
            val bonus = personagem.modificadorDe(atributoConjuracao) + personagem.bonusProficiencia
            labelConjuracao.text = "Conjuração (${atributoConjuracao.rotulo}) · Bônus de ataque ${formatoBonus(bonus)} · CD ${8 + bonus}"
            labelSlots.text = "Slots: ${formatarSlots(personagem)}"
        }
    }

    private fun carregarGrimorio(personagem: Personagem) {
        val conhecidas = magiaRepository.listarPorPersonagem(personagem.id)
        magiasExibidas.setAll(conhecidas)
        labelSlotsGrimorio.text = "Slots (${personagem.classe.rotulo} nível ${personagem.nivel}): ${formatarSlots(personagem)}"

        val circulos = SlotsDeMagia.circulos(personagem.classe, personagem.nivel) + 0
        val jaConhecidas = conhecidas.map { it.nome }.toSet()
        val disponiveis = CatalogoDeMagias.magiasDaClasse(personagem.classe)
            .filter { it.nivel == 0 || it.nivel in circulos }
            .filterNot { it.nome in jaConhecidas }
        comboMagia.items.setAll(disponiveis)
    }

    private fun carregarInventario(personagem: Personagem) {
        itens.setAll(itemRepository.listarPorPersonagem(personagem.id))
        labelPesoTotal.text = "Peso total: ${itens.sumOf { i -> i.peso * i.quantidade }} kg"
    }

    private fun formatarSlots(personagem: Personagem): String {
        val sl = SlotsDeMagia.slots(personagem.classe, personagem.nivel)
        return if (sl.isEmpty()) "sem slots"
        else sl.entries.sortedBy { it.key }.joinToString(" · ") { (c, q) -> "$q × ${c}º" }
    }

    private fun recarregarTudo() {
        personagemSelecionado()?.let { p ->
            carregarGrimorio(p)
            carregarInventario(p)
        }
    }

    private fun limparCamposDeAtributo() {
        campoNome.text = ""
        campoRacaClasse.text = ""
        campoPv.text = ""
        campoCa.text = ""
        campoArma.text = ""
        listOf(campoForca, campoDestreza, campoConstituicao, campoInteligencia, campoSabedoria, campoCarisma)
            .forEach { it.text = "" }
    }

    private fun rotuloDe(personagem: Personagem, atributo: Atributo): String =
        "${personagem.valorDe(atributo)} (${formatoBonus(personagem.modificadorDe(atributo))})"

    private fun formatoBonus(valor: Int): String = if (valor >= 0) "+$valor" else "$valor"

    private fun carregarFichas() {
        fichas.setAll(personagemRepository.listar())
    }

    private fun carregarHistorico() {
        val nomes = fichas.associate { it.id to it.nome }
        eventos.setAll(historicoRepository.listarDaSessao())
        mapaNomes = nomes
    }

    private val formatoHora = DateTimeFormatter.ofPattern("HH:mm:ss")
    private var mapaNomes: Map<String, String> = emptyMap()

    private inner class ListCellFicha : ListCell<Personagem>() {
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
            val estado = Label(personagem.estadoExibicao).apply {
                styleClass += "badge"
                styleClass += "badge-estado"
                if (personagem.estado == EstadoCombate.DERROTADO) styleClass += "badge-estado-derrotado"
            }
            val avatarCaixa = VBox(avatar).apply { alignment = Pos.TOP_CENTER }
            graphic = HBox(12.0, avatarCaixa, caixaTexto, estado)
            HBox.setHgrow(caixaTexto, Priority.ALWAYS)
            alignment = Pos.CENTER_LEFT
        }
    }

    private inner class ListCellItem : ListCell<Item>() {
        override fun updateItem(item: Item?, vazio: Boolean) {
            super.updateItem(item, vazio)
            if (vazio || item == null) {
                text = null
                graphic = null
                return
            }
            val nome = Label(item.nome).apply { styleClass += "titulo-card" }
            val detalhes = Label("qtd ${item.quantidade} · peso ${item.peso} kg (total ${item.peso * item.quantidade} kg)")
                .apply { styleClass += "texto-mutado" }
            val remover = Button("Remover").apply { styleClass += "botao botao-perigo" }
            remover.setOnAction {
                itemRepository.excluir(item.id)
                recarregarTudo()
            }
            val caixa = VBox(3.0, nome, detalhes)
            graphic = HBox(12.0, caixa, remover)
            HBox.setHgrow(caixa, Priority.ALWAYS)
            alignment = Pos.CENTER_LEFT
        }
    }

    private inner class ListCellMagia : ListCell<Magia>() {
        override fun updateItem(magia: Magia?, vazio: Boolean) {
            super.updateItem(magia, vazio)
            if (vazio || magia == null) {
                text = null
                graphic = null
                return
            }
            val nome = Label(magia.nome).apply { styleClass += "titulo-card" }
            val circulo = Label(magia.rotuloNivel).apply { styleClass += "badge" }
            val escola = Label(magia.escola.rotulo).apply { styleClass += "badge badge-ambar" }
            val cabecalho = HBox(8.0, nome, circulo, escola)

            val detalhes = Label(
                "${magia.tempoConjuracao} · ${magia.alcance} · ${magia.componentes} · ${magia.duracao}" +
                    if (magia.requerConcentracao) " · concentração" else ""
            ).apply { styleClass += "texto-mutado" }
            val descricao = Label(if (magia.descricao.isBlank()) "" else "▸ ${magia.descricao}").apply {
                styleClass += "texto-mutado"
                isWrapText = true
            }
            val corpo = VBox(2.0, detalhes, descricao)

            val preparar = Button(if (magia.preparada) "Despreparar" else "Preparar").apply {
                styleClass += "botao"
                styleClass += "botao-secundario"
            }
            preparar.setOnAction {
                magia.preparada = !magia.preparada
                magiaRepository.atualizar(magia)
                recarregarTudo()
            }
            val slot = Button(if (magia.slotGasto) "Slot livre" else "Gastar slot").apply {
                styleClass += "botao"
                styleClass += "botao-secundario"
                isDisable = magia.nivel == 0
            }
            slot.setOnAction {
                magia.slotGasto = !magia.slotGasto
                magiaRepository.atualizar(magia)
                recarregarTudo()
            }
            val remover = Button("Remover").apply {
                styleClass += "botao"
                styleClass += "botao-perigo"
            }
            remover.setOnAction {
                magiaRepository.excluir(magia.id)
                recarregarTudo()
            }
            val acoes = HBox(8.0, preparar, slot, remover).apply { alignment = Pos.CENTER_RIGHT }
            graphic = VBox(8.0, cabecalho, corpo, acoes)
        }
    }

    private inner class ListCellHabilidade : ListCell<HabilidadeDeClasse>() {
        override fun updateItem(habilidade: HabilidadeDeClasse?, vazio: Boolean) {
            super.updateItem(habilidade, vazio)
            if (vazio || habilidade == null) {
                text = null
                graphic = null
                return
            }
            val nome = Label("${habilidade.nome} (${habilidade.nivel}º)").apply { styleClass += "titulo-card" }
            val descricao = Label(habilidade.descricao).apply {
                styleClass += "texto-mutado"
                isWrapText = true
            }
            graphic = VBox(4.0, nome, descricao)
            setPrefHeight(Region.USE_COMPUTED_SIZE)
        }
    }

    private inner class ListCellHistorico : ListCell<RolagemEvento>() {
        override fun updateItem(evento: RolagemEvento?, vazio: Boolean) {
            super.updateItem(evento, vazio)
            if (vazio || evento == null) {
                text = null
                graphic = null
                return
            }
            val hora = Instant.ofEpochMilli(evento.timestamp)
                .atZone(ZoneId.systemDefault())
                .format(formatoHora)
            val nome = mapaNomes[evento.personagemId] ?: "Personagem"
            val tipo = Label("[$hora] $nome ${rotuloDoTipo(evento.tipoRolagem)}").apply { styleClass += "titulo-card" }
            val resultado = Label("▸ Resultado: ${evento.resultado}").apply { styleClass += "texto-mutado" }
            graphic = VBox(4.0, tipo, resultado)
            setPrefHeight(Region.USE_COMPUTED_SIZE)
        }
    }
}

private val Personagem.estadoExibicao: String get() = estado.name.replace("_", " ")

private fun rotuloDoTipo(tipoRolagem: TipoRolagem): String = when (tipoRolagem) {
    TipoRolagem.ATACAR -> "Ataque"
    TipoRolagem.DANO -> "Dano"
    TipoRolagem.MAGIA -> "Magia"
}