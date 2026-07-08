package personagem.ficha.info
import kotlinx.serialization.Serializable

@Serializable
/**
 * Conjunto dos seis atributos principais usados nos calculos da ficha.
 */
data class Atributos(
    var forca: Int,
    var destreza: Int,
    var constituicao: Int,
    var inteligencia: Int,
    var sabedoria: Int,
    var carisma: Int
)
