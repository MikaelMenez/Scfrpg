package presentation.controller

import domain.model.*
import domain.service.ConstrutorDeFicha
import domain.service.RoladorDeAtributos
import domain.service.SlotsDeMagia
import infrastructure.persistence.PersonagemDAO
import javafx.fxml.FXML
import javafx.scene.control.*
import javafx.stage.Stage

class CriarHeroiController {

    @FXML private lateinit var txtNome: TextField
    @FXML private lateinit var comboRaca: ComboBox<String>
    @FXML private lateinit var comboClasse: ComboBox<String>
    @FXML private lateinit var comboMetodo: ComboBox<String>
    @FXML private lateinit var txtFOR: TextField
    @FXML private lateinit var txtDES: TextField
    @FXML private lateinit var txtCON: TextField
    @FXML private lateinit var txtINT: TextField
    @FXML private lateinit var txtSAB: TextField
    @FXML private lateinit var txtCAR: TextField
    @FXML private lateinit var comboArma: ComboBox<String>
    @FXML private lateinit var comboArmadura: ComboBox<String>
    @FXML private lateinit var chkEscudo: CheckBox
    @FXML private lateinit var lblPontos: Label
    @FXML private lateinit var lblPreview: Label
    @FXML private lateinit var btnSalvar: Button
    @FXML private lateinit var btnRolar: Button

    @FXML private lateinit var btnMinFOR: Button
    @FXML private lateinit var btnMaxFOR: Button
    @FXML private lateinit var btnMinDES: Button
    @FXML private lateinit var btnMaxDES: Button
    @FXML private lateinit var btnMinCON: Button
    @FXML private lateinit var btnMaxCON: Button
    @FXML private lateinit var btnMinINT: Button
    @FXML private lateinit var btnMaxINT: Button
    @FXML private lateinit var btnMinSAB: Button
    @FXML private lateinit var btnMaxSAB: Button
    @FXML private lateinit var btnMinCAR: Button
    @FXML private lateinit var btnMaxCAR: Button

    private val personagemDAO = PersonagemDAO()
    private lateinit var campos: List<CampoAtributo>

    private class CampoAtributo(
        val sigla: String,
        val txt: TextField,
        val btnMin: Button,
        val btnMax: Button
    ) {
        val valor: Int
            get() = txt.text.toIntOrNull() ?: RoladorDeAtributos.VALOR_MINIMO
    }

    @FXML
    fun initialize() {
        campos = listOf(
            CampoAtributo("FOR", txtFOR, btnMinFOR, btnMaxFOR),
            CampoAtributo("DES", txtDES, btnMinDES, btnMaxDES),
            CampoAtributo("CON", txtCON, btnMinCON, btnMaxCON),
            CampoAtributo("INT", txtINT, btnMinINT, btnMaxINT),
            CampoAtributo("SAB", txtSAB, btnMinSAB, btnMaxSAB),
            CampoAtributo("CAR", txtCAR, btnMinCAR, btnMaxCAR)
        )

        comboRaca.items.addAll(Raca.values().map { it.name })
        comboClasse.items.addAll(Classe.values().map { it.name })
        comboMetodo.items.addAll("Rolagem de Dados", "Matriz Fixa", "Compra de Pontos")
        comboMetodo.selectionModel.selectFirst()

        comboArma.items.addAll(Arma.values().map { it.rotulo })
        comboArma.value = Arma.DESARMADO.rotulo
        comboArmadura.items.addAll(Armadura.values().filter { it != Armadura.ESCUDO }.map { it.rotulo })
        comboArmadura.value = Armadura.NENHUMA.rotulo

        campos.forEach { c ->
            c.btnMin.setOnAction { ajustarAtributo(c, -1) }
            c.btnMax.setOnAction { ajustarAtributo(c, +1) }
        }
        comboMetodo.valueProperty().addListener { _, _, _ -> modoDeAtributo() }
        listOf(comboRaca, comboClasse, comboArma, comboArmadura).forEach {
            it.valueProperty().addListener { _, _, _ -> atualizarPreview() }
        }
        chkEscudo.selectedProperty().addListener { _, _, _ -> atualizarPreview() }

        btnRolar.setOnAction { gerarAtributos() }
        btnSalvar.setOnAction { salvarHeroi() }

        ajeitarControles()
    }

    private fun ajeitarControles() {
        campos.forEach { it.txt.isEditable = true }
        modoDeAtributo()
    }

    private fun modoDeAtributo() {
        val compra = comboMetodo.value == "Compra de Pontos"
        campos.forEach { c ->
            c.txt.isEditable = !compra
            c.btnMin.isDisable = !compra
            c.btnMax.isDisable = !compra
        }
        if (compra) {
            gerarAtributos()
        }
        atualizarPontos()
        btnRolar.isDisable = compra
    }

    private fun gerarAtributos() {
        val valores = when (comboMetodo.value) {
            "Matriz Fixa" -> RoladorDeAtributos.arrayFixo()
            "Compra de Pontos" -> List(6) { RoladorDeAtributos.VALOR_MINIMO }
            else -> RoladorDeAtributos.rolar4d6DropLowest()
        }
        valores.forEachIndexed { i, v -> campos[i].txt.text = v.toString() }
        atualizarPontos()
    }

    private fun ajustarAtributo(campo: CampoAtributo, delta: Int) {
        val novosValores = campos.map { c -> if (c === campo) campo.valor + delta else c.valor }
        if (!RoladorDeAtributos.compraDePontos(novosValores)) {
            return
        }
        campo.txt.text = (campo.valor + delta).toString()
        atualizarPontos()
    }

    private fun atualizarPontos() {
        val valores = campos.map { it.valor }
        val pontos = RoladorDeAtributos.pontosRestantes(valores)
        val valido = RoladorDeAtributos.compraDePontos(valores)
        lblPontos.text = "Pontos restantes: $pontos"
        lblPontos.style = if (valido) "-fx-text-fill: green;" else "-fx-text-fill: red;"
        campos.forEach { c ->
            c.btnMax.isDisable = !RoladorDeAtributos.podeAdicionar(c.valor, pontos)
            c.btnMin.isDisable = !RoladorDeAtributos.podeDiminuir(c.valor)
        }
        atualizarPreview()
    }

    private fun armaAtual(): Arma =
        Arma.values().firstOrNull { it.rotulo == comboArma.value } ?: Arma.DESARMADO

    private fun armaduraAtual(): Armadura =
        Armadura.values().firstOrNull { it.rotulo == comboArmadura.value } ?: Armadura.NENHUMA

    private fun atualizarPreview() {
        try {
            val atributos = campos.associate { it.sigla to it.valor }
            val raca = Raca.valueOf(comboRaca.value)
            val classe = Classe.valueOf(comboClasse.value)
            val p = ConstrutorDeFicha.construir(
                "Preview", raca, classe, atributos,
                arma = armaAtual(), armadura = armaduraAtual(), escudo = chkEscudo.isSelected
            )
            val atributoConj = SlotsDeMagia.atributoDeConjuracao(classe)
            val (qtd, faces, bonus) = p.dadosDeDano()
            val dano = if (qtd > 0) "${qtd}d$faces+${bonus}" else "${faces}"
            lblPreview.text = "PV Máx: ${p.pontosDeVidaMaximo} | CA: ${p.caEfetiva()} | Ataque: +${p.modificadorAtaque()} ($dano) | " +
                "SobreHas: ${if (SlotsDeMagia.ehConjurador(classe)) "$atributoConj (CD ${p.cdDeResistencia(Atributo.deSigla(atributoConj) ?: Atributo.CARISMA)})" else "-"}"
        } catch (e: Exception) {
            lblPreview.text = "Preencha todos os campos"
        }
    }

    private fun salvarHeroi() {
        val nome = txtNome.text.trim()
        if (nome.isEmpty()) {
            Alert(Alert.AlertType.WARNING, "Nome é obrigatório!").showAndWait()
            return
        }
        try {
            val atributos = campos.associate { it.sigla to it.valor }
            if (comboMetodo.value == "Compra de Pontos" && !RoladorDeAtributos.compraDePontos(atributos.values.toList())) {
                Alert(Alert.AlertType.WARNING, "Orçamento de pontos excedido. Ajuste os atributos para gastar no máximo 27 pontos.").showAndWait()
                return
            }
            val raca = Raca.valueOf(comboRaca.value)
            val classe = Classe.valueOf(comboClasse.value)
            val personagem = ConstrutorDeFicha.construir(
                nome, raca, classe, atributos,
                arma = armaAtual(), armadura = armaduraAtual(), escudo = chkEscudo.isSelected
            )
            personagemDAO.inserir(personagem)
            Alert(Alert.AlertType.INFORMATION, "Herói '${personagem.nome}' criado com ID ${personagem.id}!").showAndWait()
            (btnSalvar.scene.window as Stage).close()
        } catch (e: Exception) {
            Alert(Alert.AlertType.ERROR, "Erro ao salvar: ${e.message}").showAndWait()
        }
    }
}