package domain.service

import domain.model.Classe
import domain.model.EscolaDeMagia
import domain.model.Magia

/**
 * Catálogo de magias do SRD 5e. Cada magia carrega todos os campos do modelo,
 * permitindo que o grimório do personagem seja fiel às regras do livro.
 */
object CatalogoDeMagias {

    private fun m(
        nome: String,
        nivel: Int,
        escola: EscolaDeMagia,
        tempoConjuracao: String,
        alcance: String,
        componentes: String,
        duracao: String,
        requerConcentracao: Boolean,
        descricao: String
    ) = Magia(
        id = nome,
        nome = nome,
        nivel = nivel,
        escola = escola,
        tempoConjuracao = tempoConjuracao,
        alcance = alcance,
        componentes = componentes,
        duracao = duracao,
        requerConcentracao = requerConcentracao,
        descricao = descricao,
        preparada = false,
        slotGasto = false
    )

    val magias: List<Magia> = listOf(
        m("Bola de Fogo", 3, EscolaDeMagia.EVOCACAO, "1 ação", "150 pés", "V, S, M", "Instantânea", false,
            "Explosão de fogo em raio de 20 pés. 8d6 de dano de fogo, reflexo reduz à metade."),
        m("Mísseis Mágicos", 1, EscolaDeMagia.EVOCACAO, "1 ação", "120 pés", "V, S", "Instantânea", false,
            "Cria 3 dardos de energia que atingem sempre. 1d4+1 de dano de força por dardo."),
        m("Curar Ferimentos", 1, EscolaDeMagia.EVOCACAO, "1 ação", "Toque", "V, S", "Instantânea", false,
            "Restaura 1d8 + modificador de conjuração pontos de vida."),
        m("Toque do Ataque Gélido", 0, EscolaDeMagia.NECROMANCIA, "1 ação", "120 pés", "V, S", "Instantânea", false,
            "Truque: raio frio causando 1d8 de dano necrótico e impedindo regeneração."),
        m("Escudo", 1, EscolaDeMagia.ABJURACAO, "1 reação", "Pessoal", "V, S", "1 rodada", false,
            "Reação: +5 de CA até o início do próximo turno."),
        m("Luz Sagrada", 0, EscolaDeMagia.EVOCACAO, "1 ação", "60 pés", "V, S", "Instantânea", false,
            "Truque: feixe de luz causando 1d8 de dano radiante (ou 1d10 contra mortos-vivos)."),
        m("Infligir Ferimentos", 1, EscolaDeMagia.NECROMANCIA, "1 ação", "Toque", "V, S", "Instantânea", false,
            "Toque necrótico causando 3d10 de dano."),
        m("Bênção", 1, EscolaDeMagia.ENCANTAMENTO, "1 ação", "30 pés", "V, S, M", "Concentração, até 1 minuto", true,
            "Até 3 criaturas somam 1d4 nos testes de ataque e resistência."),
        m("Dardo de Chama", 0, EscolaDeMagia.EVOCACAO, "1 ação", "120 pés", "V, S", "Instantânea", false,
            "Truque: chama causando 1d10 de dano de fogo."),
        m("Dormir", 1, EscolaDeMagia.ENCANTAMENTO, "1 ação", "90 pés", "V, S, M", "1 minuto", false,
            "Criaturas somando até 5d8 de PV caem no sono, por ordem de PV."),
        m("Lâmina Flamejante", 2, EscolaDeMagia.EVOCACAO, "1 ação bônus", "Pessoal", "V, S, M", "Concentração, até 10 minutos", true,
            "Espada de fogo causando 3d6 de dano de fogo ao acertar."),
        m("Manto do Herói", 4, EscolaDeMagia.ENCANTAMENTO, "1 ação", "Pessoal", "V, S, M", "1 minuto", false,
            "Imponente e inspirador por 1 minuto; vantagem em intimidação e aliados ganham inspiração."),
        m("Voo", 3, EscolaDeMagia.TRANSMUTACAO, "1 ação", "Toque", "V, S, M", "Concentração, até 10 minutos", true,
            "A criatura ganha velocidade de voo de 60 pés."),
        m("Invisibilidade", 2, EscolaDeMagia.ILUSAO, "1 ação", "Toque", "V, S, M", "Concentração, até 1 hora", true,
            "A criatura se torna invisível até atacar ou lançar magia."),
        m("Enfeitiçar Pessoa", 1, EscolaDeMagia.ENCANTAMENTO, "1 ação", "30 pés", "V, S", "1 hora", false,
            "Alvo humanóide considera você um amigo de confiança."),
        m("Raio de Fogo", 0, EscolaDeMagia.EVOCACAO, "1 ação", "60 pés", "V, S", "Instantânea", false,
            "Truque: fogo em linha causando 1d8 de dano."),
        m("Lâmina Sombria", 2, EscolaDeMagia.CONJURACAO, "1 ação bônus", "Pessoal", "V, S, M", "Concentração, até 1 minuto", true,
            "Espada sombria causando 2d8 de dano psíquico ou necrótico."),
        m("Amizade", 0, EscolaDeMagia.ENCANTAMENTO, "1 ação", "Toque", "S, M", "Concentração, até 1 minuto", true,
            "Truque: vantagem em testes de Carisma contra uma criatura."),
        m("Clarão Curativo", 2, EscolaDeMagia.EVOCACAO, "1 ação", "60 pés", "V", "Instantânea", false,
            "Explosão que restaura 3d8 de PV e causa dano radiante a mortos-vivos."),
        m("Toque do Vampiro", 3, EscolaDeMagia.NECROMANCIA, "1 ação", "Toque", "V, S", "Concentração, até 1 minuto", true,
            "Ataque corpo a corpo causando 3d6 de dano necrótico, curando metade do dano."),
        m("Nuvem de Névoa", 1, EscolaDeMagia.CONJURACAO, "1 ação", "120 pés", "V, S", "Concentração, até 10 minutos", true,
            "Esfera de névoa com raio de 20 pés criando visibilidade obscurecida."),
m("Proteção contra o Mal", 1, EscolaDeMagia.ABJURACAO, "1 ação", "Toque", "V, S, M", "Concentração, até 10 minutos", true,
            "A criatura fica protegida: demônios, mortos-vivos, elementais e similares têm desvantagem em ataques."),
        m("Cone de Frio", 3, EscolaDeMagia.EVOCACAO, "1 ação", "Pessoal (cone de 60 pés)", "V, S, M", "Instantânea", false,
            "Cone de frio causando 8d8 de dano de gelo; resistência reduz à metade."),
        m("Ferir Palavras", 0, EscolaDeMagia.ENCANTAMENTO, "1 ação bônus", "60 pés", "V", "Instantânea", false,
            "Truque: causa 1d6 de dano psíquico; resistência de Sabedoria evita.")
    )

    fun porNome(nome: String): Magia? = magias.firstOrNull { it.nome.equals(nome, true) }

    fun porId(id: String): Magia? = magias.firstOrNull { it.id == id }

    fun magiasDaClasse(classe: Classe): List<Magia> {
        val nomes = when (classe) {
            Classe.MAGO -> listOf(
                "Bola de Fogo", "Mísseis Mágicos", "Escudo", "Toque do Ataque Gélido",
                "Voo", "Invisibilidade", "Dardo de Chama", "Raio de Fogo", "Lâmina Flamejante"
            )
            Classe.FEITICEIRO -> listOf(
                "Bola de Fogo", "Mísseis Mágicos", "Escudo", "Dardo de Chama",
                "Voo", "Invisibilidade", "Dormir", "Enfeitiçar Pessoa"
            )
            Classe.BARDO -> listOf(
                "Dormir", "Enfeitiçar Pessoa", "Invisibilidade", "Voo",
                "Curar Ferimentos", "Manto do Herói", "Amizade"
            )
            Classe.BRUXO -> listOf(
                "Toque do Ataque Gélido", "Toque do Vampiro", "Invisibilidade",
                "Lâmina Sombria", "Voo"
            )
            Classe.CLERIGO -> listOf(
                "Curar Ferimentos", "Luz Sagrada", "Bênção", "Infligir Ferimentos",
                "Clarão Curativo"
            )
            Classe.DRUIDA -> listOf(
                "Curar Ferimentos", "Bênção", "Lâmina Flamejante", "Voo"
            )
            Classe.PALADINO -> listOf(
                "Curar Ferimentos", "Bênção", "Clarão Curativo", "Luz Sagrada"
            )
            Classe.PATRULHEIRO -> listOf(
                "Curar Ferimentos", "Bênção", "Lâmina Flamejante", "Amizade"
            )
            else -> emptyList()
        }
        return magias.filter { it.nome in nomes }
    }

    fun todasAsClasses(): List<Classe> = Classe.values().filter { ehClasseConjuradora(it) }

    private fun ehClasseConjuradora(classe: Classe): Boolean =
        SlotsDeMagia.ehConjurador(classe)
}