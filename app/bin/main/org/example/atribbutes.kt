
package org.example

data class Attributes(
    var forca: Int,
    var destreza: Int,
    var constituicao: Int,
    var inteligencia: Int,
    var sabedoria: Int,
    var carisma: Int,
) {
    operator fun plus(outro: Attributes): Attributes =
        Attributes(
            forca = this.forca + outro.forca,
            destreza = this.destreza + outro.destreza,
            constituicao =
                this.constituicao + outro.constituicao,
            inteligencia = this.inteligencia + outro.inteligencia,
            sabedoria =
                this.sabedoria + outro.sabedoria,
            carisma = this.carisma + outro.carisma,
        )

    operator fun minus(outro: Attributes): Attributes =
        Attributes(
            forca = this.forca - outro.forca,
            destreza = this.destreza - outro.destreza,
            constituicao =
                this.constituicao - outro.constituicao,
            inteligencia = this.inteligencia - outro.inteligencia,
            sabedoria =
                this.sabedoria - outro.sabedoria,
            carisma = this.carisma - outro.carisma,
        )
}
