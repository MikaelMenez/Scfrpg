package com.escudomestre.digital.domain.model

/**
 * Catálogo de magias (SRD 5e/5.5) usado pelo grimório. Cada magia informa o
 * círculo, a escola, o tempo de conjuração, alcance, componentes, duração e as
 * classes que podem conjurá-la.
 */
object CatalogoDeMagias {

    private val magias: List<Magia> = listOf(
        Magia("Bola de Fogo", 3, EscolaDeMagia.EVOCACAO, "1 ação", "150 pés", "V, S, M", "Instantânea", descricao = "Explosão de chamas num raio de 6 m: cada alvo faz salvaguarda de Destreza ou recebe 8d6 de dano de fogo."),
        Magia("Míssil Mágico", 1, EscolaDeMagia.EVOCACAO, "1 ação", "120 pés", "V, S", "Instantânea", descricao = "Três dardos de energia que acertam automaticamente, causando 1d4+1 de dano de força cada."),
        Magia("Curar Ferimentos", 1, EscolaDeMagia.EVOCACAO, "1 ação", "Toque", "V, S", "Instantânea", descricao = "O toque restaura 1d8 + modificador de conjuração de pontos de vida. Cura +1d8 por círculo adicional."),
        Magia("Pancada Sônica", 0, EscolaDeMagia.EVOCACAO, "1 ação", "60 pés", "V, S", "Instantânea", descricao = "Truque de dano por trovão: 1d8 de dano sônico (cresce com o nível do conjurador)."),
        Magia("Dardo de Fogo", 0, EscolaDeMagia.EVOCACAO, "1 ação", "120 pés", "V, S", "Instantânea", descricao = "Truque: ataque de conjuração à distância causando 1d10 de dano de fogo."),
        Magia("Raio de Gelo", 0, EscolaDeMagia.EVOCACAO, "1 ação", "120 pés", "V, S", "Instantânea", descricao = "Truque: ataque de conjuração à distância causando 1d8 de dano de frio e reduzindo o deslocamento do alvo em 3 m."),
        Magia("Cura Suprema", 5, EscolaDeMagia.EVOCACAO, "1 ação", "60 pés", "V, S", "Instantânea", descricao = "Restaura até 70 pontos de vida de uma criatura."),
        Magia("Mãos Mágicas", 0, EscolaDeMagia.CONJUACAO, "1 ação", "30 pés", "V, S", "1 minuto", descricao = "Truque: cria uma mão espectral que manipula objetos à distância."),
        Magia("Armadura Arcana", 1, EscolaDeMagia.ABJURACAO, "1 ação", "Toque", "V, S, M", "8 horas", requerConcentracao = false, descricao = "Base da CA torna-se 13 + modificador de Destreza do alvo."),
        Magia("Escudo", 1, EscolaDeMagia.ABJURACAO, "Reação", "Pessoal", "V, S", "1 rodada", descricao = "Reação: +5 na CA até o início do próximo turno (incluindo contra o ataque disparador)."),
        Magia("Escudo da Fé", 1, EscolaDeMagia.ABJURACAO, "1 ação bônus", "60 pés", "V, S, M", "10 minutos", requerConcentracao = true, descricao = "Um halo protege o alvo, concedendo +2 na CA."),
        Magia("Barreira de Lâminas", 6, EscolaDeMagia.EVOCACAO, "1 ação", "90 pés", "V, S", "10 minutos", requerConcentracao = true, descricao = "Uma muralha de lâminas giratórias: 6d10 de dano por corte a quem cruzar ou ficar nela."),
        Magia("Sonho", 5, EscolaDeMagia.ILUSAO, "1 minuto", "Especial", "V, S, M", "8 horas", descricao = "Modela os sonhos de uma criatura; se você conhece o alvo, pode decidir o que ele sonha."),
        Magia("Invisibilidade", 2, EscolaDeMagia.ILUSAO, "1 ação", "Toque", "V, S, M", "1 hora", requerConcentracao = true, descricao = "O alvo fica invisível por 1 hora; o efeito termina se atacar ou conjurar uma magia."),
        Magia("Levitar", 2, EscolaDeMagia.TRANSMUTACAO, "1 ação", "60 pés", "V, S, M", "10 minutos", requerConcentracao = true, descricao = "O alvo flutua verticalmente até 6 m por rodada enquanto a magia durar."),
        Magia("Reduzir/Aumentar", 2, EscolaDeMagia.TRANSMUTACAO, "1 ação", "30 pés", "V, S, M", "1 minuto", requerConcentracao = true, descricao = "Altera o tamanho do alvo, dobrando ou reduzindo à metade seu tamanho e atributos físicos."),
        Magia("Bem e Mal", 1, EscolaDeMagia.ABJURACAO, "1 ação", "Toque", "V, S, M", "1 minuto", requerConcentracao = true, descricao = "Uma criatura voluntária é protegida contra celestiais, elementais, fadas e mortos-vivos por 1 minuto."),
        Magia("Regeneração", 7, EscolaDeMagia.TRANSMUTACAO, "1 minuto", "Toque", "V, S, M", "1 hora", descricao = "O alvo recupera 4d8+15 de PV e regenera 1 PV por rodada; membros perdidos voltam a crescer."),
        Magia("Curandeiro", 4, EscolaDeMagia.EVOCACAO, "1 ação", "Toque", "V, S", "Instantânea", descricao = "O toque remove doenças e o estado atordoado, e restaura 4d8 de pontos de vida."),
        Magia("Bola de Fogo Maior", 5, EscolaDeMagia.EVOCACAO, "1 ação", "150 pés", "V, S, M", "Instantânea", descricao = "Explosão de fogo num raio de 9 m: salvaguarda de Destreza ou 10d6 de dano de fogo."),
        Magia("Andarilho Selvagem", 4, EscolaDeMagia.TRANSMUTACAO, "1 ação", "Toque", "V, S, M", "24 horas", descricao = "O alvo atravessa qualquer obstáculo físico sem deixar rastros por 24 horas."),
        Magia("Ponte de Luz", 5, EscolaDeMagia.EVOCACAO, "1 ação bônus", "60 pés", "V, S, M", "10 minutos", requerConcentracao = true, descricao = "Cria uma ponte de luz sólida que pode sustentar criaturas."),
    )

    private val magiasPorClasse: Map<ClasseDePersonagem, List<String>> = mapOf(
        ClasseDePersonagem.MAGO to listOf(
            "Bola de Fogo", "Míssil Mágico", "Pancada Sônica", "Dardo de Fogo", "Raio de Gelo",
            "Mãos Mágicas", "Armadura Arcana", "Escudo", "Invisibilidade", "Levitar",
            "Reduzir/Aumentar", "Cura Suprema", "Barreira de Lâminas",
        ),
        ClasseDePersonagem.FEITICEIRO to listOf(
            "Bola de Fogo", "Míssil Mágico", "Pancada Sônica", "Dardo de Fogo", "Raio de Gelo",
            "Mãos Mágicas", "Armadura Arcana", "Escudo", "Invisibilidade", "Levitar",
            "Reduzir/Aumentar", "Cura Suprema", "Barreira de Lâminas",
        ),
        ClasseDePersonagem.CLERIGO to listOf(
            "Curar Ferimentos", "Cura Suprema", "Curandeiro", "Regeneração", "Bem e Mal",
            "Escudo da Fé", "Bola de Fogo Maior",
        ),
        ClasseDePersonagem.DRUIDA to listOf(
            "Curar Ferimentos", "Cura Suprema", "Curandeiro", "Regeneração", "Bem e Mal",
            "Andarilho Selvagem", "Ponte de Luz",
        ),
        ClasseDePersonagem.BARDO to listOf(
            "Pancada Sônica", "Mãos Mágicas", "Curar Ferimentos", "Invisibilidade", "Sonho",
            "Cura Suprema", "Barreira de Lâminas",
        ),
        ClasseDePersonagem.PALADINO to listOf(
            "Curar Ferimentos", "Bem e Mal", "Escudo da Fé", "Escudo",
        ),
        ClasseDePersonagem.PATRULHEIRO to listOf(
            "Curar Ferimentos", "Bem e Mal", "Invisibilidade", "Andarilho Selvagem",
        ),
        ClasseDePersonagem.BRUXO to listOf(
            "Pancada Sônica", "Dardo de Fogo", "Raio de Gelo", "Mãos Mágicas",
            "Escudo", "Bola de Fogo", "Invisibilidade", "Cura Suprema",
        ),
    )

    /** Todas as magias do catálogo, ordenadas por círculo e nome. */
    fun todas(): List<Magia> = magias

    /** Magias disponíveis para uma [classe], ordenadas por círculo e nome. */
    fun magiasDaClasse(classe: ClasseDePersonagem): List<Magia> {
        val nomes = magiasPorClasse[classe].orEmpty()
        return magias.filter { it.nome in nomes }.sortedWith(compareBy<Magia> { it.nivel }.thenBy { it.nome })
    }
}
