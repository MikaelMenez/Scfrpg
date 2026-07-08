package personagem.ficha.info
import personagem.ficha.inventario.Inventario
import kotlinx.serialization.Serializable
import personagem.ficha.acao.Ataque
import personagem.ficha.acao.Magia
import personagem.ficha.acao.SlotsMagia

@Serializable
/**
 * Ficha completa do personagem.
 *
 * Reune escolhas de criacao, estado atual, inventario e acoes prontas para uso
 * durante jogo.
 */
data class Personagem(
    val userId: Int,
    val nome: String,
    val raca: Raca,
    val classe: Classe,
    val atributos: Atributos,

    var nivel: Int,
    var experiencia: Int,

    var vidaMaxima: Int,
    var vidaAtual: Int,
    var vidaTemporaria: Int,
    var dadosVida: DadosVida,

    var classeArmadura: Int,
    var iniciativa: Double,
    var proficiencia: Int,
    var inspiracao: Boolean,
    var idiomas: MutableList<String>,
    var caracteristicas: MutableList<String>,
    var antecedente: Antecedente,

    var sucessosMorte: Int,
    var falhasMorte: Int,

    var inventario: Inventario,
    var magias: MutableList<Magia>,
    var ataques: MutableList<Ataque>,
    var slotsMagia: SlotsMagia
)
