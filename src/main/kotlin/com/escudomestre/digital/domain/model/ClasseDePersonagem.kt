package com.escudomestre.digital.domain.model

/**
 * Classes do SRD 5e com dado de vida (Hit Dice) e proficiências de armadura e armas.
 * O dado de vida determina os PV (máximo no 1º nível) e as proficiências definem
 * se o personagem soma o bônus de proficiência nas rolagens.
 */
enum class ClasseDePersonagem(
    val rotulo: String,
    val dadoDeVida: Int,
    val atributoPrincipal: Atributo,
    val armaduras: List<TipoArmadura>,
    val usaArmasSimples: Boolean = true,
    val usaArmasMarciais: Boolean = false,
) {
    BARBARO("Bárbaro", 12, Atributo.FORCA, listOf(TipoArmadura.LEVE, TipoArmadura.MEDIA, TipoArmadura.ESCUDO), usaArmasMarciais = true),
    BARDO("Bardo", 8, Atributo.CARISMA, listOf(TipoArmadura.LEVE)),
    CLERIGO("Clérigo", 8, Atributo.SABEDORIA, listOf(TipoArmadura.LEVE, TipoArmadura.MEDIA, TipoArmadura.ESCUDO)),
    DRUIDA("Druida", 8, Atributo.SABEDORIA, listOf(TipoArmadura.LEVE, TipoArmadura.MEDIA, TipoArmadura.ESCUDO)),
    GUERREIRO("Guerreiro", 10, Atributo.FORCA, listOf(TipoArmadura.LEVE, TipoArmadura.MEDIA, TipoArmadura.PESADA, TipoArmadura.ESCUDO), usaArmasMarciais = true),
    MONGE("Monge", 8, Atributo.DESTREZA, emptyList()),
    PALADINO("Paladino", 10, Atributo.FORCA, listOf(TipoArmadura.LEVE, TipoArmadura.MEDIA, TipoArmadura.PESADA, TipoArmadura.ESCUDO), usaArmasMarciais = true),
    PATRULHEIRO("Patrulheiro", 10, Atributo.DESTREZA, listOf(TipoArmadura.LEVE, TipoArmadura.MEDIA, TipoArmadura.ESCUDO), usaArmasMarciais = true),
    LADINO("Ladino", 8, Atributo.DESTREZA, listOf(TipoArmadura.LEVE)),
    FEITICEIRO("Feiticeiro", 6, Atributo.CARISMA, emptyList()),
    BRUXO("Bruxo", 8, Atributo.CARISMA, listOf(TipoArmadura.LEVE)),
    MAGO("Mago", 6, Atributo.INTELIGENCIA, emptyList()),

    ;

    fun ehProficienteEmArmadura(tipo: TipoArmadura): Boolean = tipo in armaduras

    fun ehProficienteEmArma(arma: Arma): Boolean =
        (arma.marcial && usaArmasMarciais) || (!arma.marcial && usaArmasSimples)
}
