package domain.model

enum class Atributo(val rotulo: String, val sigla: String) {
    FORCA("Força", "FOR"),
    DESTREZA("Destreza", "DES"),
    CONSTITUICAO("Constituição", "CON"),
    INTELIGENCIA("Inteligência", "INT"),
    SABEDORIA("Sabedoria", "SAB"),
    CARISMA("Carisma", "CAR");

    companion object {
        fun deSigla(sigla: String): Atributo? =
            entries.firstOrNull { it.sigla.equals(sigla, true) || it.name.equals(sigla, true) }
    }
}