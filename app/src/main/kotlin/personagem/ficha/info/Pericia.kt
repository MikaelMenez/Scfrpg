package personagem.ficha.info
import kotlinx.serialization.Serializable

@Serializable
/**
 * Pericia da ficha e o atributo usado para seus testes.
 */
data class Pericia(
    val nome:String,
    val atributo:String,
    var proficiente:Boolean
)
