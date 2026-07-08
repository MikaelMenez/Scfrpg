package personagem.funcoes
import personagem.ficha.info.Personagem
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

/**
 * Salva e recupera personagens serializados em JSON.
 */
class JsonService{
    private val pastaPersonagens = File("data/personagens")

    val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    /**
     * Carrega uma ficha salva para um usuario e nome especificos.
     */
    fun carregar(userId: Int, nome: String): Personagem?{
        val file = File(pastaPersonagens, "$userId/$nome.json")

        if (!file.exists()) return null

        return json.decodeFromString(file.readText())
    }

    /**
     * Persiste a ficha em data/personagens/{userId}/{nome}.json.
     */
    fun salvar(personagem: Personagem){
        val pasta = File(pastaPersonagens, personagem.userId.toString())

        if (!pasta.exists()) pasta.mkdirs()

        File(pasta, "${personagem.nome}.json"
        ).writeText(json.encodeToString(personagem))
    }

    /**
     * Remove a ficha salva, caso o arquivo exista.
     */
    fun excluir(userId: Int, nome: String) {
        File(pastaPersonagens, "$userId/$nome.json").delete()
    }
}
