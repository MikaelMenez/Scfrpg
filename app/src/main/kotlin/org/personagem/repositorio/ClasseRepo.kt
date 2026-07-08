package personagem.repositorio
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import personagem.ficha.info.Classe
import java.io.File

/**
 * Carrega e consulta classes cadastradas em data/classes.json.
 */
class ClasseRepo {
    private val json = Json {
        ignoreUnknownKeys = true
    }

    private val classes: MutableList<Classe> = mutableListOf()

    init {
        carregar()
    }

    private fun carregar() {
        // O arquivo deve conter uma lista JSON, mesmo que vazia: [].
        val texto = File("data/classes.json").readText()

        classes.addAll(json.decodeFromString(ListSerializer
            (Classe.serializer()),texto))

    }

    fun listar(): List<Classe> = classes

    fun buscar(nome: String): Classe? {
        return classes.find {
            it.nome.equals(nome, true)
        }
    }
}
