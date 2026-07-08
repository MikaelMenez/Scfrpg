package personagem.ficha.acao
import kotlinx.serialization.Serializable

@Serializable
/**
 * Descreve uma magia disponivel para personagens conjuradores.
 *
 * Magias de nivel 0 representam truques e nao consomem espacos de magia.
 */
data class Magia(
    val nome:String,
    val nivel:Int,
    val escola:String,
    val tempoConjuracao:String,
    val alcance:String,
    val componentes:String,
    val duracao:String,
    val descricao:String,
    val tipo: String,
    var estado: String
)
