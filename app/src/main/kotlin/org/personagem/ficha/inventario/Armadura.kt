package personagem.ficha.inventario
import kotlinx.serialization.Serializable

@Serializable
/**
 * Armadura equipada ou carregada pelo personagem.
 */
data class Armadura(
    val nome: String,
    val classe: Int,
    val tipo: String,
    val forcaMinima: Int,
    val desvantagemFurtividade: Boolean
)
