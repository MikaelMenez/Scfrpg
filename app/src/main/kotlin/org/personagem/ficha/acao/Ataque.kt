package personagem.ficha.acao
import kotlinx.serialization.Serializable

@Serializable
/**
 * Ataque exibido na ficha do personagem.
 *
 * O bonus ja inclui proficiencia e modificador de atributo calculados pelo
 * CombateService. O dano fica como texto para aceitar formulas como "1d8+3".
 */
data class Ataque(
    val nome:String,
    val bonus:Int,
    val dano:String,
    val tipo:String
)
