# Documentacao do modulo `personagem`

Este modulo concentra as estruturas e regras principais para montar uma ficha de personagem de RPG. Ele separa os dados da ficha, as regras de calculo e os repositórios que carregam conteudo base em JSON.

## Estrutura

- `ficha/info`: informacoes centrais do personagem, como raca, classe, atributos, antecedente, pericias e pontos de vida.
- `ficha/inventario`: itens carregados pelo personagem, incluindo armas e armaduras.
- `ficha/acao`: acoes que aparecem na ficha, como ataques, magias e espacos de magia.
- `funcoes`: servicos com regras de negocio, como criacao de personagem, combate, magia, geracao de atributos e persistencia.
- `repositorio`: carregadores de dados base vindos de arquivos JSON.
- `data`: arquivos JSON usados como base de racas, classes, magias, antecedentes e inventario.

## Fluxo principal

1. Os repositórios carregam os dados base a partir da pasta `data`.
2. `PersonagemService` recebe raca, classe, atributos, antecedente e inventario.
3. `CombateService` calcula vida maxima, classe de armadura, iniciativa e ataques.
4. `MagiaService` calcula slots de magia e regras de conjuracao.
5. `JsonService` salva, carrega ou exclui personagens em `data/personagens`.

## Modelos principais

`Personagem` representa a ficha completa. Ela junta dados fixos, como nome, raca e classe, com dados mutaveis, como vida atual, experiencia, inventario, magias e ataques.

`Classe`, `Raca` e `Antecedente` representam escolhas feitas na criacao do personagem. Elas tambem fornecem idiomas, pericias, habilidades, equipamentos e configuracoes de conjuracao.

`Inventario` separa armas, armaduras e itens comuns para facilitar calculos e exibicao.

## Regras importantes

- O modificador de atributo segue a regra `floor((valor - 10) / 2)`.
- Armas usam `dano` como texto para permitir valores como `1d8`, `2d6` ou outras formulas.
- Armaduras usam `tipo` para calcular classe de armadura: `leve` soma Destreza completa, `media`/`média` limita Destreza a +2 e `pesada` ignora Destreza.
- Conjuradores recebem slots de magia conforme o nivel.
- Truques, ou magias de nivel `0`, nao gastam slots.
- Testes de resistencia de classe sao nomes de atributos, por exemplo `["forca", "constituicao"]`.

## Formato esperado dos JSONs

Os arquivos de listas devem conter arrays JSON. Mesmo vazios, devem usar:

```json
[]
```

Arquivos vazios com tamanho zero causam erro ao serem carregados pelos repositórios.

## Pontos pendentes

- Preencher os arquivos JSON de dados base.
- Adicionar testes automatizados para criacao de personagem, combate, magia e carregamento dos repositórios.
