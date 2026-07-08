package personagem.ficha.inventario
import kotlinx.serialization.Serializable

@Serializable
/**
 * Inventario separado por tipo para facilitar calculos de combate e exibicao.
 */
data class Inventario(
    var armas:MutableList<Arma>,
    var armaduras:MutableList<Armadura>,
    var itens:MutableList<Item>
)
