package domain.model

enum class Classe(val hitDice: Int, val nomeExibicao: String) {
    BARBARO(12, "Bárbaro"),
    BARDO(8, "Bardo"),
    BRUXO(8, "Bruxo"),
    CLERIGO(8, "Clérigo"),
    DRUIDA(8, "Druida"),
    FEITICEIRO(6, "Feiticeiro"),
    GUERREIRO(10, "Guerreiro"),
    LADINO(8, "Ladino"),
    MAGO(6, "Mago"),
    MONGE(8, "Monge"),
    PALADINO(10, "Paladino"),
    PATRULHEIRO(10, "Patrulheiro")
}
