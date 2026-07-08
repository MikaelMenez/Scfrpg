package personagem.ficha.acao
import kotlinx.serialization.Serializable

@Serializable
/**
 * Agrupa os espacos de magia do personagem por nivel de magia.
 */
data class SlotsMagia(
    val espacos:MutableList<NivelMagia>
)

@Serializable
/**
 * Controla quantos espacos existem e quantos ja foram usados em um nivel.
 */
data class NivelMagia(
    val nivel:Int,
    var maximo:Int,
    var usados:Int
)
