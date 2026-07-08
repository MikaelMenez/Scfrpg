package personagem.ficha.inventario
import kotlinx.serialization.Serializable

@Serializable
/**
 * Arma carregada no inventario.
 *
 * O dano e texto para representar dados de RPG, como "1d6" ou "1d8".
 */
data class Arma(
    val nome: String,
    val dano: String,
    val tipo: String,
    val acuidade: Boolean,
    val distancia: Boolean,
    val alcance: String,
    val peso: Double,
    val propriedades: List<String>
)
