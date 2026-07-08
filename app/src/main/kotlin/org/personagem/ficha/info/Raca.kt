package personagem.ficha.info
import kotlinx.serialization.Serializable

@Serializable
/**
 * Raca do personagem, incluindo deslocamento, idiomas e habilidades raciais.
 */
data class Raca(
    val nome: String,
    val deslocamento: Int,
    val idiomas:List<String>,
    val habilidades:List<String>
)
