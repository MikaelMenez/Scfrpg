package domain.service

import domain.model.Classe
import domain.model.HabilidadeDeClasse

/**
 * Catálogo de habilidades de classe do SRD 5e, com fidelidade às regras do livro.
 */
object CatalogoHabilidades {

    private fun h(nome: String, nivel: Int, descricao: String) = HabilidadeDeClasse(nome, nivel, descricao)

    private val habilidades = mapOf(
        Classe.GUERREIRO to listOf(
            h("Estilo de Combate", 1, "Adota um estilo de combate escolhido (Arqueria, Defesa, Duelo, Armas Grandes)."),
            h("Segundo Fôlego", 1, "Ação bônus: recupera 1d10 + nível de Guerreiro de PV uma vez por descanso curto."),
            h("Surto de Ação", 2, "Ação bônus: realiza uma ação adicional extra uma vez por descanso curto."),
            h("Arquétipo de Combate", 3, "Escolhe um arquétipo de lutador (Campeão, Cavaleiro-Arcano ou Cavaleiro da Muralha)."),
            h("Ataque Extra", 5, "Pode atacar duas vezes sempre que realiza a ação de Atacar."),
            h("Indomável", 9, "Uma vez por descanso longo, relança uma resistência fracassada."),
            h("Ataque Extra (3)", 11, "Pode atacar três vezes com a ação de Atacar.")
        ),
        Classe.LADINO to listOf(
            h("Perícia", 1, "Ganha proficiência em Agilidade, Percepção e outras 4 perícias."),
            h("Ataque Furtivo", 1, "Causa 1d6 extras de dano ao acertar com vantagem ou com um aliado do alvo a 5 pés."),
            h("Ação Ardilosa", 2, "Ação bônus: Esquiva, Desengajar ou Esconder."),
            h("Arquétipo de Ladino", 3, "Escolhe um arquétipo de Ladino."),
            h("Ataque Extra", 5, "Recebe a perícia Necessidade e ganha Ataque Extra."),
            h("Furtividade Suprema", 11, "Não pode tirar menos que 10 em testes de Destreza (Agilidade)."),
            h("Ataque Furtivo (10d6)", 20, "Causa 10d6 de dano de furtividade com o ataque furtivo.")
        ),
        Classe.BARBARO to listOf(
            h("Fúria", 1, "Entra em fúria por 1 minuto: vantagem em testes de Força e resistência a dano físico."),
            h("Defesa sem Armadura", 2, "CA = 10 + modificador de Destreza + modificador de Constituição."),
            h("Ataque Imprudente", 2, "Vantagem nos ataques, mas inimigos ganham vantagem contra você."),
            h("Caminho Primitivo", 3, "Escolhe um caminho primitivo."),
            h("Ataque Extra", 5, "Pode atacar duas vezes com a ação de Atacar."),
            h("Fúria Cruel", 9, "A fúria causa dano extra ao acertar em combate corpo a corpo."),
            h("Vitalidade Imortal", 15, "Recupera um dado de vida quando fica abaixo da metade dos PV.")
        ),
        Classe.MONGE to listOf(
            h("Artes Marciais", 1, "Usa dados de artes marciais (d4) e pode usar Destreza em vez de Força."),
            h("Defesa sem Armadura", 1, "CA = 10 + modificador de Destreza + modificador de Sabedoria."),
            h("Ki", 2, "Ganha pontos de Ki, recuperados em descanso curto, para habilidades marciais."),
            h("Maneira Desarmada", 2, "Gasta 1 Ki para derrubar, empurrar ou atordoar um alvo atingido."),
            h("Tradição Monástica", 3, "Escolhe uma tradição monástica."),
            h("Movimento Acelerado", 5, "Pode deslizar pelo ar e realizar Ataque Extra."),
            h("Mente Serena", 7, "Vantagem em resistências de Sabedoria e liberta-se de medo/encantamento.")
        ),
        Classe.PALADINO to listOf(
            h("Sentido Divino", 1, "Sente a presença de celestiais, demônios e mortos-vivos num raio de 60 pés."),
            h("Imposição de Mãos", 1, "Ação: restaura PV em um total de 5 x nível de Paladino por descanso longo."),
            h("Estilo de Combate", 2, "Adota um estilo de combate escolhido."),
            h("Punição Divina", 2, "Despende um espaço de magia para causar dano radiante extra ao acertar."),
            h("Juramento Sagrado", 3, "Faz um juramento (Devoção, Lealdade ou Vingança)."),
            h("Ataque Extra", 5, "Pode atacar duas vezes com a ação de Atacar."),
            h("Aura de Proteção", 6, "Aliados a 10 pés somam seu modificador de Carisma nas resistências."),
            h("Aura de Coragem", 10, "Aliados dentro de 10 pés ficam imunes ao medo.")
        ),
        Classe.CLERIGO to listOf(
            h("Domínio Divino", 1, "Concede proficiências e benefícios do domínio escolhido (Vida, Luz, etc.)."),
            h("Feitiços de Domínio", 1, "Prepara sempre os feitiços de domínio no nível de círculo correspondente."),
            h("Canalizar Divindade", 2, "Gasta o canal para efeitos de domínio ou expulsar mortos-vivos, uma vez por descanso curto."),
            h("Destruir Mortos-Vivos", 5, "Pode destruir mortos-vivos com fracasso em resistência de Sabedoria."),
            h("Dano Divino (1d8)", 8, "Golpes de arma causam 1d8 extras de dano divino."),
            h("Intervenção Divina", 10, "Pede ajuda direta de sua divindade no nível 10.")
        ),
        Classe.BRUXO to listOf(
            h("Patrono Arcano", 1, "Faz um pacto sombrio com um patrono (Arquifada, Demônio ou Grande Antigo)."),
            h("Magia de Pacto", 1, "Ganha espaços de magia de pacto que se recuperam em descanso curto."),
            h("Invocação Mística", 2, "Escolhe duas invocações místicas do seu patrono."),
            h("Bênção do Pacto", 3, "Realiza um pacto (Lâmina, Cadeia, Espada ou Tomo)."),
            h("Arcano Místico", 11, "Ganha um espaço de magia mística de círculo 6 a 9 (um por uso).")
        ),
        Classe.MAGO to listOf(
            h("Recuperação Arcanista", 1, "Recupera espaços de magia ao final de um descanso curto, num total de metade do nível."),
            h("Tradição Arcana", 2, "Escolhe uma tradição arcana (Abrir as Portas do Impossível, Ilusionista, etc.)."),
            h("Arquivo de Feitiços", 3, "Registra feitiços encontrados no seu arquivo pessoal de magias."),
            h("Foco Arcano", 6, "Pode lançar feitiços sem componentes materiais quando o foco está presente.")
        ),
        Classe.DRUIDA to listOf(
            h("Druidês", 1, "Fala a língua dos druidas."),
            h("Forma Selvagem", 2, "Transforma-se em uma fera pelo nível máximo do seu círculo."),
            h("Círculo Druídico", 2, "Escolhe um círculo druídico (Terras, Selva, Lua)."),
            h("Descargas do Prado", 6, "Explosão de cura/dano em forma selvagem (dependendo do círculo)."),
            h("Forma de Elemental", 10, "Pode se transformar em elemental."),
            h("Natureza Imortal", 18, "Sente a passagem dos séculos sem envelhecer.")
        ),
        Classe.FEITICEIRO to listOf(
            h("Origem Feérica", 1, "Escolhe uma origem sobrenatural (Linhagem Dracônica ou Magia Selvagem)."),
            h("Ponto de Feitiçaria", 2, "Ganha pontos de feitiçaria para metamágica e espaços de magia."),
            h("Metamágica", 3, "Escolhe opções de metamágica (Feitiço Gêmeo, Acelerado, Subtil, etc.)."),
            h("Flexibilidade Arcana", 5, "Converte pontos de feitiçaria em espaços de magia ou vice-versa."),
            h("Magia Selvagem", 6, "Surto mágico aleatório ao lançar feitiços quando a magia selvagem está ativa.")
        ),
        Classe.BARDO to listOf(
            h("Inspiração de Bardo", 1, "Ação bônus: concede um d6 de inspiração a um aliado."),
            h("Canção de Descanso", 2, "Aliados que descansam recuperam um dado de vida extra."),
            h("Colégio de Bardo", 3, "Escolhe um colégio (Conhecimento, Coragem ou Semiótica)."),
            h("Perícia Extra", 3, "Recebe proficiência em duas perícias adicionais."),
            h("Maestria", 14, "Ao passar em um teste de perícia proficiente, ganha inspiração extra.")
        ),
        Classe.PATRULHEIRO to listOf(
            h("Inimigo Favorito", 1, "Escolhe um tipo de inimigo favorito para vantagens em rastreamento."),
            h("Caçador Natural", 1, "Vantagem em rastreamento e sobrevivência."),
            h("Estilo de Combate", 2, "Adota um estilo de combate escolhido."),
            h("Magia de Patrulheiro", 2, "Ganha espaços de magia de patrulheiro."),
            h("Arquétipo de Patrulheiro", 3, "Escolhe um arquétipo (Caçador, Mestre das Feras)."),
            h("Ataque Extra", 5, "Pode atacar duas vezes com a ação de Atacar.")
        )
    )

    fun habilidadesDaClasse(classe: Classe): List<HabilidadeDeClasse> = habilidades[classe] ?: emptyList()

    fun habilidadesAteNivel(classe: Classe, nivel: Int): List<HabilidadeDeClasse> =
        habilidadesDaClasse(classe).filter { it.nivel <= nivel }
}