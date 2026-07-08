package personagem.ficha.info
import kotlinx.serialization.Serializable;

@Serializable
/**
 * Dados de vida disponiveis para descanso e recuperacao.
 */
data class DadosVida(
        val dado:Int,
        var total:Int
)
