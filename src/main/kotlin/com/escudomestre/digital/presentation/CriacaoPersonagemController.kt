package com.escudomestre.digital.presentation

import com.escudomestre.digital.domain.model.Armadura
import com.escudomestre.digital.domain.model.Arma
import com.escudomestre.digital.domain.model.Atributo
import com.escudomestre.digital.domain.model.ClasseDePersonagem
import com.escudomestre.digital.domain.model.Raca
import com.escudomestre.digital.domain.model.bonusDeProficiencia
import com.escudomestre.digital.domain.model.modificadorDeAtributo
import com.escudomestre.digital.domain.repository.PersonagemRepository
import com.escudomestre.digital.domain.service.ConstrutorDeFicha
import com.escudomestre.digital.domain.service.RoladorDeAtributos
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.CheckBox
import javafx.scene.control.ComboBox
import javafx.scene.control.Label
import javafx.scene.control.TextField
import java.net.URL
import java.util.ResourceBundle

/**
 * Tela de criação de fichas D&D 5e (RU01): raça e classe em listas suspensas,
 * atributos gerados por rolagem (4d6 descartando o menor), array fixo ou compra
 * de pontos, equipamento (arma, armadura, escudo) e validação dos obrigatórios (AC 1.3).
 */
class CriacaoPersonagemController : Initializable {

    enum class MetodoDeAtributos(val rotulo: String) {
        ROLAR("Rolar (4d6 descartar menor)"),
        ARRAY_FIXO("Array fixo (15, 14, 13, 12, 10, 8)"),
        COMPRA_DE_PONTOS("Compra de pontos (27)"),
    }

    @FXML
    private lateinit var campoNome: TextField
    @FXML
    private lateinit var comboRaca: ComboBox<Raca>
    @FXML
    private lateinit var comboClasse: ComboBox<ClasseDePersonagem>
    @FXML
    private lateinit var campoNivel: TextField
    @FXML
    private lateinit var comboMetodo: ComboBox<MetodoDeAtributos>
    @FXML
    private lateinit var campoForca: TextField
    @FXML
    private lateinit var campoDestreza: TextField
    @FXML
    private lateinit var campoConstituicao: TextField
    @FXML
    private lateinit var campoInteligencia: TextField
    @FXML
    private lateinit var campoSabedoria: TextField
    @FXML
    private lateinit var campoCarisma: TextField
    @FXML
    private lateinit var modForca: Label
    @FXML
    private lateinit var modDestreza: Label
    @FXML
    private lateinit var modConstituicao: Label
    @FXML
    private lateinit var modInteligencia: Label
    @FXML
    private lateinit var modSabedoria: Label
    @FXML
    private lateinit var modCarisma: Label
    @FXML
    private lateinit var labelPontos: Label
    @FXML
    private lateinit var comboArma: ComboBox<Arma>
    @FXML
    private lateinit var comboArmadura: ComboBox<Armadura>
    @FXML
    private lateinit var checkEscudo: CheckBox
    @FXML
    private lateinit var resumoPv: Label
    @FXML
    private lateinit var resumoCa: Label
    @FXML
    private lateinit var resumoAtaque: Label
    @FXML
    private lateinit var resumoProficiencia: Label
    @FXML
    private lateinit var resumoBonoRacial: Label
    @FXML
    private lateinit var labelErro: Label

    private val rolador = RoladorDeAtributos()
    private val construtor = ConstrutorDeFicha()

    private lateinit var app: MainApp
    private lateinit var personagemRepository: PersonagemRepository

    private val valoresRolados = mutableMapOf<Atributo, Int>()
    private var pontosRestantes = RoladorDeAtributos.PONTOS_DISPONIVEIS

    private val camposDeAtributo = mapOf(
        Atributo.FORCA to ::campoForca,
        Atributo.DESTREZA to ::campoDestreza,
        Atributo.CONSTITUICAO to ::campoConstituicao,
        Atributo.INTELIGENCIA to ::campoInteligencia,
        Atributo.SABEDORIA to ::campoSabedoria,
        Atributo.CARISMA to ::campoCarisma,
    )
    private val modulosDeAtributo = mapOf(
        Atributo.FORCA to ::modForca,
        Atributo.DESTREZA to ::modDestreza,
        Atributo.CONSTITUICAO to ::modConstituicao,
        Atributo.INTELIGENCIA to ::modInteligencia,
        Atributo.SABEDORIA to ::modSabedoria,
        Atributo.CARISMA to ::modCarisma,
    )

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        comboRaca.items.setAll(Raca.entries)
        comboClasse.items.setAll(ClasseDePersonagem.entries)
        comboMetodo.items.setAll(MetodoDeAtributos.entries)
        comboMetodo.value = MetodoDeAtributos.ROLAR
        comboArma.items.setAll(Arma.entries)
        comboArmadura.items.setAll(Armadura.entries)
        comboArmadura.value = Armadura.SEM_ARMADURA

        comboRaca.valueProperty().addListener { _, _, _ -> atualizarResumo() }
        comboClasse.valueProperty().addListener { _, _, _ -> atualizarResumo() }
        campoNivel.textProperty().addListener { _, _, _ -> atualizarResumo() }
        comboArma.valueProperty().addListener { _, _, _ -> atualizarResumo() }
        comboArmadura.valueProperty().addListener { _, _, _ -> atualizarResumo() }
        checkEscudo.selectedProperty().addListener { _, _, _ -> atualizarResumo() }
        comboMetodo.valueProperty().addListener { _, _, _ ->
            atualizarResumo()
        }
        camposDeAtributo.values.forEach { campo ->
            campo.get().textProperty().addListener { _, _, _ ->
                if (comboMetodo.value == MetodoDeAtributos.COMPRA_DE_PONTOS) {
                    atualizarPontosEValidar()
                }
                atualizarResumo()
            }
        }
    }

    fun inicializar(app: MainApp, personagemRepository: PersonagemRepository) {
        this.app = app
        this.personagemRepository = personagemRepository
    }

    fun cancelar() {
        app.mostrarSelecao()
    }

    fun gerarAtributos() {
        when (comboMetodo.value) {
            MetodoDeAtributos.ROLAR -> {
                val valores = rolador.rolar4d6DescartandoMenor()
                aplicarValores(Atributo.entries.zip(valores).toMap())
            }
            MetodoDeAtributos.ARRAY_FIXO -> {
                aplicarValores(Atributo.entries.zip(rolador.arrayFixo()).toMap())
            }
            MetodoDeAtributos.COMPRA_DE_PONTOS -> {
                pontosRestantes = RoladorDeAtributos.PONTOS_DISPONIVEIS
                aplicarValores(Atributo.entries.associateWith { RoladorDeAtributos.VALOR_MINIMO_PONTO })
            }
        }
        atualizarPontosEValidar()
        atualizarResumo()
    }

    fun salvar() {
        val nome = campoNome.text.trim()
        if (nome.isEmpty()) {
            mostrarErro("O campo Nome é obrigatório (AC 1.3).")
            campoNome.requestFocus()
            return
        }
        val raca = comboRaca.value
        val classe = comboClasse.value
        if (raca == null || classe == null) {
            mostrarErro("Selecione a raça e a classe (AC 1.3).")
            return
        }
        val nivel = campoNivel.text.trim().toIntOrNull()
            ?: return mostrarErro("Nível deve ser um número inteiro.")
        if (nivel <= 0) {
            mostrarErro("Nível deve ser maior que zero.")
            return
        }
        if (valoresRolados.isEmpty()) {
            mostrarErro("Gere os atributos antes de salvar (AC 1.3).")
            return
        }
        if (comboMetodo.value == MetodoDeAtributos.COMPRA_DE_PONTOS && pontosRestantes < 0) {
            mostrarErro("A compra de pontos excedeu o orçamento de 27 pontos.")
            return
        }

        val personagem = construtor.construir(
            nome = nome,
            raca = raca,
            classe = classe,
            nivel = nivel,
            valoresBase = valoresRolados,
            arma = comboArma.value,
            armadura = comboArmadura.value,
            escudo = checkEscudo.isSelected,
        )
        personagemRepository.criar(personagem)
        ocultarErro()
        app.mostrarSelecao()
    }

    private fun aplicarValores(valores: Map<Atributo, Int>) {
        valoresRolados.clear()
        valoresRolados.putAll(valores)
        Atributo.entries.forEach { atributo ->
            camposDeAtributo[atributo]!!.get().text = valores[atributo].toString()
        }
    }

    private fun atualizarPontosEValidar() {
        if (comboMetodo.value != MetodoDeAtributos.COMPRA_DE_PONTOS) {
            camposDeAtributo.values.forEach { it.get().isEditable = false }
            labelPontos.isVisible = false
            labelPontos.isManaged = false
            return
        }
        camposDeAtributo.values.forEach { it.get().isEditable = true }
        val valores = lerValoresDosCampos()
        if (valores.all { it in RoladorDeAtributos.VALOR_MINIMO_PONTO..RoladorDeAtributos.VALOR_MAXIMO_PONTO }) {
            pontosRestantes = rolador.pontosRestantes(valores)
        }
        labelPontos.text = "Pontos restantes: $pontosRestantes"
        labelPontos.isVisible = true
        labelPontos.isManaged = true
    }

    private fun lerValoresDosCampos(): List<Int> =
        camposDeAtributo.values.map { it.get().text.trim().toIntOrNull() ?: Int.MAX_VALUE }

    private fun atualizarResumo() {
        if (!::campoForca.isInitialized) return
        val raca = comboRaca.value
        val classe = comboClasse.value

        Atributo.entries.forEach { atributo ->
            val valor = valoresRolados[atributo]
            if (valor != null) {
                val comBonoRacial = valor + (raca?.bonus?.get(atributo) ?: 0)
                val mod = modificadorDeAtributo(comBonoRacial)
                modulosDeAtributo[atributo]!!.get().text = "(${if (mod >= 0) "+" else ""}$mod)"
            } else {
                modulosDeAtributo[atributo]!!.get().text = ""
            }
        }

        labelPontos.text = "Pontos restantes: $pontosRestantes"

        if (raca == null || classe == null || valoresRolados.isEmpty()) {
            resumoPv.text = ""
            resumoCa.text = ""
            resumoAtaque.text = ""
            resumoProficiencia.text = ""
            resumoBonoRacial.text = ""
            return
        }

        val nivel = campoNivel.text.trim().toIntOrNull() ?: 1
        val personagem = construtor.construir(
            nome = campoNome.text.ifBlank { "Novo" },
            raca = raca,
            classe = classe,
            nivel = nivel,
            valoresBase = valoresRolados,
            arma = comboArma.value,
            armadura = comboArmadura.value,
            escudo = checkEscudo.isSelected,
        )

        resumoPv.text = "PV máximo: ${personagem.pontosDeVidaMaximo} (d${classe.dadoDeVida} + CON)"
        resumoCa.text = "Classe de Armadura: ${personagem.classeArmadura}"
        resumoAtaque.text = "Bônus de ataque: ${formatoBonus(personagem.modificadorAtaque)}" +
            (if (personagem.armaEquipada != null) " (${personagem.armaEquipada!!.rotulo})" else "")
        resumoProficiencia.text = "Bônus de proficiência: +${bonusDeProficiencia(nivel)}"
        resumoBonoRacial.text = "Bônus racial ${raca.rotulo}: " +
            raca.bonus.entries.joinToString(", ") { (atributo, bonus) ->
                "${atributo.rotulo} +$bonus"
            }
    }

    private fun formatoBonus(valor: Int): String = if (valor >= 0) "+$valor" else "$valor"

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
