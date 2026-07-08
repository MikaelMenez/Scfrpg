package personagem.repositorio
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import personagem.ficha.info.Raca;

import java.io.File

/**
 * Carrega e consulta racas cadastradas em data/racas.json.
 */
class RacaRepo {
    private val json = Json {
        ignoreUnknownKeys = true
    }

    private val racas: MutableList<Raca> = mutableListOf()

    init {
        carregar()
    }

    private fun carregar() {
        // O arquivo deve conter uma lista JSON, mesmo que vazia: [].
        val texto = File("data/racas.json").readText()

        racas.addAll(json.decodeFromString(ListSerializer
                (Raca.serializer()),texto))

    }

    fun listar(): List<Raca> = racas

    fun buscar(nome: String): Raca? {
        return racas.find {
            it.nome.equals(nome, true)
        }
    }
}
