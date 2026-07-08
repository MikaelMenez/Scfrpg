package personagem.ficha.inventario
import kotlinx.serialization.Serializable

@Serializable
/**
 * Item comum do inventario, controlado por quantidade.
 */
data class Item(
    val nome: String,
    val peso: Double,
    var quantidade: Int
)
