package personagem.ficha.info
import kotlinx.serialization.Serializable

@Serializable
/**
 * Classe de personagem, com dados de vida, proficiencias, habilidades e
 * informacoes de conjuracao quando aplicavel.
 */
data class Classe(
    val nome: String,
    val vidaBase: Int,
    val testesResistencia: List<String>,
    val pericias: List<Pericia>,
    val habilidades:List<String>,
    val conjurador:Boolean,
    val atributoConjuracao: String,
    val magiasIniciais: List<String>,
    val dadoVida:Int
)
