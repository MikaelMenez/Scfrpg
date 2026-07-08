package personagem.repositorio
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import personagem.ficha.info.Antecedente
import java.io.File

/**
 * Carrega e consulta antecedentes cadastrados em data/antecedentes.json.
 */
class AntecedenteRepo {
    private val json = Json {
        ignoreUnknownKeys = true
    }

    private val antecedentes: MutableList<Antecedente> = mutableListOf()

    init {
        carregar()
    }

    private fun carregar() {
        // O arquivo deve conter uma lista JSON, mesmo que vazia: [].
        val texto = File("data/antecedentes.json").readText()

        antecedentes.addAll(json.decodeFromString(ListSerializer
            (Antecedente.serializer()),texto))

    }

    fun listar(): List<Antecedente> = antecedentes

    fun buscar(nome: String): Antecedente? {
        return antecedentes.find {
            it.nome.equals(nome, true)
        }
    }
}
