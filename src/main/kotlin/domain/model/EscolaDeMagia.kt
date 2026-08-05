package domain.model

enum class EscolaDeMagia(val nomeExibicao: String) {
    ABJURACAO("Abjuração"),
    CONJURACAO("Conjuração"),
    DIVINACAO("Adivinhação"),
    ENCANTAMENTO("Encantamento"),
    EVOCACAO("Evocação"),
    ILUSAO("Ilusão"),
    NECROMANCIA("Necromancia"),
    TRANSMUTACAO("Transmutação");

    companion object {
        fun deTexto(texto: String): EscolaDeMagia? = entries.firstOrNull { it.name.equals(texto, true) }
    }
}