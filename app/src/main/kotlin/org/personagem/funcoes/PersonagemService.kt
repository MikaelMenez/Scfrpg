package personagem.funcoes
import personagem.ficha.info.Antecedente
import personagem.ficha.info.Atributos
import personagem.ficha.info.Classe
import personagem.ficha.info.DadosVida
import personagem.ficha.info.Personagem
import personagem.ficha.info.Raca
import personagem.ficha.inventario.Inventario

class PersonagemService(
    private val combateService: CombateService,
    private val magiaService: MagiaService
){
    /**
     * Monta uma ficha inicial completa a partir das escolhas do jogador.
     */
    fun criarPersonagem(
        userId: Int,
        nome: String,
        raca: Raca,
        classe: Classe,
        atributos: Atributos,
        antecedente: Antecedente,
        inventario: Inventario
    ): Personagem {
        val nivel = 1
        val vidaMaxima = combateService.calcularPV(classe, atributos, nivel)

        return Personagem(
            userId = userId,
            nome = nome,
            raca = raca,
            classe = classe,
            atributos = atributos,
            nivel = nivel,
            experiencia = 0,
            vidaMaxima = vidaMaxima,
            vidaAtual = vidaMaxima,
            vidaTemporaria = 0,
            dadosVida = DadosVida(
                dado = classe.dadoVida,
                total = 1
            ),
            classeArmadura = combateService.calcularCA(atributos),
            iniciativa = combateService.calcularIniciativa(atributos),
            proficiencia = 2,
            inspiracao = false,
            idiomas = (
                    raca.idiomas +
                            antecedente.idiomas
                    ).distinct().toMutableList(),
            caracteristicas = mutableListOf(),
            antecedente = antecedente,
            sucessosMorte = 0,
            falhasMorte = 0,
            inventario = inventario,
            magias = mutableListOf(),
            ataques = mutableListOf(),
            slotsMagia = magiaService.calcularEspacos(classe, nivel)
        ).apply {
            // Equipamentos do antecedente entram depois da criacao da ficha base.
            inventario.itens.addAll(antecedente.inventario.itens)
            inventario.armaduras.addAll(antecedente.inventario.armaduras)
            inventario.armas.addAll(antecedente.inventario.armas)

            // Ataques e magias dependem do inventario e da classe escolhida.
            ataques.addAll(combateService.gerarAtaques(this))

            if (classe.conjurador) {
                magias.addAll(magiaService.magiasIniciais(classe))
            }
        }
    }
}
