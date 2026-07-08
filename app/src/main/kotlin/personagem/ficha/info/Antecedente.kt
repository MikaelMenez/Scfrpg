package personagem.ficha.info
import kotlinx.serialization.Serializable
import personagem.ficha.inventario.Inventario

@Serializable
/**
 * Origem social ou historia previa do personagem.
 *
 * O antecedente adiciona pericias, idiomas, bonus e equipamento inicial.
 */
data class Antecedente(
    val nome:String,
    val pericias:List<Pericia>,
    val idiomas:List<String>,
    val bonus: Atributos,
    val inventario: Inventario
)
