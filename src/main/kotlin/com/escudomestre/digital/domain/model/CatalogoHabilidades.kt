package com.escudomestre.digital.domain.model

/**
 * Catálogo (SRD 5e) das habilidades de classe: features com o nível em que são
 * obtidas. Inclui os principais recursos de cada uma das 12 classes até o 3º nível.
 */
object CatalogoHabilidades {

    private val habilidades: Map<ClasseDePersonagem, List<HabilidadeDeClasse>> = mapOf(
        ClasseDePersonagem.BARBARO to listOf(
            HabilidadeDeClasse("Fúria", 1, "Ação bônus: entra em fúria por 1 minuto, ganhando vantagem em Força, resistência a dano de concussão/perfurante/cortante e +2 no dano com armas corpo a corpo."),
            HabilidadeDeClasse("Defesa sem Armadura", 1, "Enquanto sem armadura, a CA é 10 + modificador de Destreza + modificador de Constituição."),
            HabilidadeDeClasse("Ataque Imprudente", 2, "Ao atacar, pode ter vantagem nas jogadas de ataque; ataques contra você também ganham vantagem até seu próximo turno."),
            HabilidadeDeClasse("Caminho Primitivo", 3, "Escolhe um Caminho (ex.: Berserker) que concede novas habilidades ao subir de nível."),
        ),
        ClasseDePersonagem.BARDO to listOf(
            HabilidadeDeClasse("Conjuração", 1, "Conjura magias com Carisma. Usa um instrumento musical ou foco arcano como foco de conjuração."),
            HabilidadeDeClasse("Inspiração de Bardo", 1, "Ação bônus: concede a um aliado um d6 para somar em uma rolagem de habilidade, ataque ou salvaguarda (d10 no 5º nível, d12 no 10º)."),
            HabilidadeDeClasse("Perícias de Bardo", 1, "Recebe proficiência em 3 perícias à sua escolha."),
            HabilidadeDeClasse("Canção de Descanso", 2, "Durante um descanso curto, os aliados rolam dados de vida extras."),
            HabilidadeDeClasse("Colégio de Bardo", 3, "Escolhe um Colégio (ex.: do Conhecimento) que concede habilidades especializadas."),
        ),
        ClasseDePersonagem.CLERIGO to listOf(
            HabilidadeDeClasse("Conjuração", 1, "Conjura magias com Sabedoria. Domínio: ex.: Vida, Luz. Prepara magias da lista de clérigo."),
            HabilidadeDeClasse("Domínio Divino", 1, "Concede magias de domínio e poderes, como Canalizar Divindade no 2º nível."),
            HabilidadeDeClasse("Canalizar Divindade", 2, "Pode canalizar energia divina, renovável em um descanso curto, para poderes como Expulsar Mortos-Vivos."),
        ),
        ClasseDePersonagem.DRUIDA to listOf(
            HabilidadeDeClasse("Conjuração", 1, "Conjura magias com Sabedoria usando um foco druídico. Prepara magias da lista de druida."),
            HabilidadeDeClasse("Idioma Druídico", 1, "Entende o idioma secreto dos druidas, usado para deixar mensagens ocultas."),
            HabilidadeDeClasse("Forma Selvagem", 2, "Ação bônus: assume a forma de um animal que já viu, usando as estatísticas dele por 1 hora."),
            HabilidadeDeClasse("Círculo Druídico", 2, "Escolhe um Círculo (ex.: da Terra) que concede habilidades e magias extra."),
        ),
        ClasseDePersonagem.GUERREIRO to listOf(
            HabilidadeDeClasse("Estilo de Luta", 1, "Escolhe um estilo (ex.: Combate com Armas Grandes, Defesa, Arqueiro) que concede um bônus de combate."),
            HabilidadeDeClasse("Retomar Fôlego", 1, "Ação bônus: recupera 1d10 + nível em pontos de vida, renovado em um descanso curto."),
            HabilidadeDeClasse("Surto de Ação", 2, "Ação: ganha uma ação extra neste turno, renovado em um descanso curto."),
            HabilidadeDeClasse("Arquétipo Marcial", 3, "Escolhe um Arquétipo (ex.: Campeão, Mestre de Batalha, Cavaleiro Arcano) que concede habilidades."),
        ),
        ClasseDePersonagem.MONGE to listOf(
            HabilidadeDeClasse("Defesa sem Armadura", 1, "Enquanto sem armadura, a CA é 10 + modificador de Destreza + modificador de Sabedoria."),
            HabilidadeDeClasse("Artes Marciais", 1, "Usa destreza em vez de força nas armas de monge; dá de 1d4 a 1d10 de dano desarmado conforme o nível."),
            HabilidadeDeClasse("Pontos de Ki", 2, "Ganha pontos de ki (1 por nível de monge) recuperados em um descanso curto para usar técnicas."),
            HabilidadeDeClasse("Tradição Monástica", 3, "Escolhe uma Tradição (ex.: Caminho da Mão Aberta, das Sombras) que concede técnicas especiais."),
        ),
        ClasseDePersonagem.PALADINO to listOf(
            HabilidadeDeClasse("Sentido Divino", 1, "Ação: detecta a presença e localização de celestiais, diabos e mortos-vivos num raio de 60 pés."),
            HabilidadeDeClasse("Cura pelas Mãos", 1, "Golpe toque com 5 pontos de vida de cura multiplicado pelo nível, renovado em um descanso longo."),
            HabilidadeDeClasse("Conjuração", 2, "Conjura magias com Carisma (meio conjurador), preparadas da lista de paladino."),
            HabilidadeDeClasse("Golpe Divino", 2, "Ao acertar, gasta um slot para causar dano radiante extra de 2d8."),
            HabilidadeDeClasse("Juramento Sagrado", 3, "Escolhe um Juramento (ex.: Devoção, Vingança) que concede magias e Canalizar Divindade."),
        ),
        ClasseDePersonagem.PATRULHEIRO to listOf(
            HabilidadeDeClasse("Inimigo Favorito", 1, "Escolhe um tipo de inimigo: vantagem em rastreamento e rolagens de memória sobre ele, além de aprender o idioma dele."),
            HabilidadeDeClasse("Conjuração", 2, "Conjura magias com Sabedoria (meio conjurador), preparadas da lista de patrulheiro."),
            HabilidadeDeClasse("Estilo de Luta", 2, "Escolhe um estilo de luta (ex.: Arqueiro, Combate com Duas Armas) que concede bônus de combate."),
            HabilidadeDeClasse("Arquétipo de Patrulheiro", 3, "Escolhe um Arquétipo (ex.: Caçador, Mestre das Feras) que concede habilidades."),
        ),
        ClasseDePersonagem.LADINO to listOf(
            HabilidadeDeClasse("Perícia", 1, "Escolhe 4 perícias para ter proficiência, além de Duas Ferramentas de Ladrão."),
            HabilidadeDeClasse("Ataque Furtivo", 1, "Uma vez por turno, causa 1d6 extra (cresce com o nível) ao acertar com vantagem ou quando um aliado está a 1,5 m do alvo."),
            HabilidadeDeClasse("Esquiva Sobrenatural", 2, "Quando um atacante visível acerta, reage para reduzir o dano à metade."),
            HabilidadeDeClasse("Ação Ardilosa", 2, "Ação bônus: usar Desengajar, Esconder-se ou Correr."),
            HabilidadeDeClasse("Arquétipo de Ladino", 3, "Escolhe um Arquétipo (ex.: Assassino, Trapaceiro Arcano) que concede habilidades."),
        ),
        ClasseDePersonagem.FEITICEIRO to listOf(
            HabilidadeDeClasse("Conjuração", 1, "Conjura magias com Carisma, escolhidas no avanço de nível. Pode usar um foco arcano."),
            HabilidadeDeClasse("Origens Mágicas", 1, "Escolhe uma origem (ex.: Linhagem Dracônica, Magia Selvagem) que concede habilidades inatas."),
            HabilidadeDeClasse("Pontos de Feitiçaria", 2, "Ganha pontos de feitiçaria (nível × 2) para criar slots ou flexibilizar magias."),
            HabilidadeDeClasse("Metamagia", 3, "Escolhe 2 opções (ex.: Feitiço Acelerado, Feitiço Estendido) que modificam a conjuração gastando pontos."),
        ),
        ClasseDePersonagem.BRUXO to listOf(
            HabilidadeDeClasse("Patrono Sobrenatural", 1, "Firma um pacto com um patrono (ex.: Arcano, Infernal, A Fenda) que concede habilidades e a expansão do grimório."),
            HabilidadeDeClasse("Magia de Pacto", 1, "Conjura magias com Carisma; os slots são recuperados em um descanso curto."),
            HabilidadeDeClasse("Invocação Sombria", 2, "Escolhe invocações que concedem magias ou habilidades passivas (ex.: Visão das Trevas, Fúria Ardente)."),
            HabilidadeDeClasse("Bênção do Pacto", 3, "Escolhe um pacto (ex.: Lâmina do Pacto, Corrente do Pacto, Livro Antigo) que concede habilidades."),
        ),
        ClasseDePersonagem.MAGO to listOf(
            HabilidadeDeClasse("Conjuração", 1, "Conjura magias com Inteligência, anotadas no grimório a partir de pergaminhos e níveis."),
            HabilidadeDeClasse("Recuperação Arcana", 1, "Uma vez por dia, recupera slots de magia gastos cujo nível total seja no máximo metade do nível de mago (arredondado para cima)."),
            HabilidadeDeClasse("Tradição Arcana", 2, "Escolhe uma escola (ex.: Evocação, Abjuração) que concede habilidades da especialização."),
            HabilidadeDeClasse("Metamágica", 3, "(Na 5.5) escolhe opções de metamágica para flexibilizar a conjuração."),
        ),
    )

    /** Retorna as habilidades da [classe] ordenadas por nível de obtenção. */
    fun habilidadesDaClasse(classe: ClasseDePersonagem): List<HabilidadeDeClasse> =
        habilidades[classe].orEmpty()

    /** Retorna apenas as habilidades já obtidas no [nivel] indicado (inclusive). */
    fun habilidadesAteNivel(classe: ClasseDePersonagem, nivel: Int): List<HabilidadeDeClasse> =
        habilidadesDaClasse(classe).filter { it.nivel <= nivel }
}
