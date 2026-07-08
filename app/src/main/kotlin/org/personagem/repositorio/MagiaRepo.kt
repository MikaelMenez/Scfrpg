package personagem.repositorio
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import personagem.ficha.acao.Magia
import java.io.File

/**
 * Carrega e consulta magias cadastradas em data/magias.json.
 */
class MagiaRepo {
    private val json = Json {
        ignoreUnknownKeys = true
    }

    private val magias: MutableList<Magia> = mutableListOf()

    init {
        carregar()
    }

    private fun carregar() {
        // O arquivo deve conter uma lista JSON, mesmo que vazia: [].
        val texto = File("data/magias.json").readText()

        magias.addAll(json.decodeFromString(ListSerializer
            (Magia.serializer()),texto))

    }

    fun listar(): List<Magia> = magias

    fun buscar(nome: String): Magia? {
        return magias.find {
            it.nome.equals(nome, true)
        }
    }
}
