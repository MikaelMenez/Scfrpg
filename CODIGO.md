# 🧩 Guia do Código — O Escudo do Mestre Digital

Documentação técnica para quem vai **ler ou escrever código** neste projeto. Aqui você
encontra a arquitetura, a estrutura de pastas, o papel de cada classe e as convenções
de desenvolvimento adotadas.

---

## Índice

1. [Visão geral da arquitetura](#1-visão-geral-da-arquitetura)
2. [Stack e ferramentas](#2-stack-e-ferramentas)
3. [Estrutura de pastas](#3-estrutura-de-pastas)
4. [Camada de domínio — model](#4-camada-de-domínio--model)
5. [Camada de domínio — service](#5-camada-de-domínio--service)
6. [Camada de domínio — repository](#6-camada-de-domínio--repository)
7. [Camada de aplicação](#7-camada-de-aplicação)
8. [Camada de infraestrutura — persistência](#8-camada-de-infraestrutura--persistência)
9. [Camada de apresentação — interface](#9-camada-de-apresentação--interface)
10. [Testes](#10-testes)
11. [Fluxos principais](#11-fluxos-principais)
12. [Convenções e boas práticas](#12-convenções-e-boas-práticas)
13. [Armadilhas comuns](#13-armadilhas-comuns)
14. [Como adicionar código novo](#14-como-adicionar-código-novo)

---

## 1. Visão geral da arquitetura

O projeto segue uma arquitetura em camadas inspirada em **DDD (Domain-Driven Design)**,
onde cada camada depende apenas das camadas mais internas:

```
presentation (JavaFX: telas, controllers, FXML, CSS)
    │
    ▼
application (SimuladorDeCombate, Observer)
    │
    ▼
domain (model, service, repository — regras de negócio, Kotlin puro)
    ▲
    │
infrastructure (persistência Exposed/SQLite, repositórios concretos)
```

- **domain** é o núcleo: entidades, enums, serviços de regras e contratos de repositório.
  Não conhece JavaFX nem banco.
- **application** coordena casos de uso (combate, rolagens) e liga o domínio à UI.
- **infrastructure** implementa os repositórios com Exposed ORM sobre SQLite.
- **presentation** é a interface JavaFX (FXML + controllers + CSS).

As referências de requisitos (RU/RF/RNF/AC) nos comentários apontam para a especificação
em `Escudo_do_Mestre_Digital_v1_2.md`.

---

## 2. Stack e ferramentas

| Item            | Versão / Detalhe                                   |
|-----------------|----------------------------------------------------|
| Linguagem       | Kotlin 2.2 (JVM)                                   |
| JVM             | Java 21 (toolchain configurada no Gradle)          |
| UI              | JavaFX 21 (módulos `controls` e `fxml`)            |
| ORM             | Exposed 0.56 (`core`, `jdbc`, `dao`)               |
| Banco           | SQLite (`org.xerial:sqlite-jdbc`)                  |
| Build           | Gradle Wrapper 8.14 (`./gradlew`)                  |
| Testes          | JUnit 5 + kotlin-test                              |
| Corrotinas      | `kotlinx-coroutines-core` (declarada, usada nos testes de desempenho) |

Configurações: `build.gradle.kts`, `gradle.properties`, `settings.gradle.kts`.
A classe de entrada do `application` plugin é `com.escudomestre.digital.presentation.MainAppKt`.

---

## 3. Estrutura de pastas

```
src/
├── main/
│   ├── kotlin/com/escudomestre/digital/
│   │   ├── application/            # Camada de aplicação
│   │   ├── domain/
│   │   │   ├── model/              # Entidades, value objects, enums de regra
│   │   │   ├── repository/         # Contratos de repositório (interfaces)
│   │   │   └── service/            # Serviços de regra de negócio (Kotlin puro)
│   │   ├── infrastructure/
│   │   │   └── persistence/        # Exposed: tabelas, DAO, fábrica de conexão
│   │   │       └── repositories/   # Implementações concretas dos repositórios
│   │   └── presentation/           # MainApp, controllers, recursos FXML
│   └── resources/com/escudomestre/digital/presentation/
│       ├── *.fxml                  # Telas (Seleção, Criação, Painel)
│       └── estilo.css              # Folha de estilo (tema dark D&D)
└── test/kotlin/com/escudomestre/digital/
    ├── application/                # Testes do SimuladorDeCombate
    ├── domain/
    │   ├── model/                  # Personagem, Historico, Item/Magia
    │   └── service/                # DadoVirtual, MotorDeRegras, RoladorDeAtributos, ConstrutorDeFicha
    ├── infrastructure/persistence/ # Testes de integração (CRUD no SQLite)
    └── FichasDeTeste.kt            # Fábrica de fichas para os testes
```

---

## 4. Camada de domínio — `model`

Arquivos em `domain/model/`. Contêm entidades, enums e funções puras de regra.

### `Atributo.kt`
```kotlin
enum class Atributo(val rotulo: String) { FORCA, DESTREZA, CONSTITUICAO, INTELIGENCIA, SABEDORIA, CARISMA }

fun modificadorDeAtributo(valor: Int): Int = Math.floorDiv(valor - 10, 2)
fun bonusDeProficiencia(nivel: Int): Int = 2 + (nivel - 1) / 4
```
- `modificadorDeAtributo`: `(valor - 10) / 2` arredondado para baixo (16 → +3, 8 → -1).
- `bonusDeProficiencia`: +2 até o nível 4, +3 no 5–8, e assim por diante.

### `Raca.kt`
Enum das 9 raças do SRD com o mapa `bonus` (aumentos de habilidade):
- `ANÃO` → CON +2; `ELFO`/`HALFLING` → DEX +2; `HUMANO` → +1 em todos; `DRACONATO` → STR +2, CHA +1;
  `GNOMO` → INT +2; `MEIO_ELFO` → CHA +2; `MEIO_ORC` → STR +2, CON +1; `TIEFLING` → INT +1, CHA +2.
- ⚠️ O **Meio-Elfo** no SRD também ganha +1 em dois atributos à escolha do jogador; hoje o
  programa aplica apenas o +2 em Carisma (os +1 livres ficam por conta do jogador).

### `ClasseDePersonagem.kt`
Enum das 12 classes do SRD com:
- `dadoDeVida` (Hit Die): Bárbaro d12; Guerreiro/Paladino/Patrulheiro d10; demais d8;
  Feiticeiro/Mago d6.
- `atributoPrincipal`, `armaduras` (proficiências) e `usaArmasSimples`/`usaArmasMarciais`.

Métodos:
- `ehProficienteEmArmadura(tipo)` — testa proficiência;
- `ehProficienteEmArma(arma)` — simples ou marcial.

### `Arma.kt`
Enum das armas do SRD com `marcial`, `distancia`, `acuidade`, `facesDano` e
`quantidadeDadosDano`. Ex.: `ESPADA_LONGA` (marcial, d8), `ARCO_LONGO` (distância, d8),
`MACHADO_GRANDE` (d12), `ADAGA` (acuidade, d4).

### `Armadura.kt`
Dois enums:
- `TipoArmadura` — LEVE, MEDIA, PESADA, ESCUDO.
- `Armadura` — cada opção com `caBase` e `maxModDestreza`:
  - leves → `null` (usa o mod. de Destreza completo);
  - médias → `2` (limite);
  - pesadas → `0` (ignora Destreza);
  - `ESCUDO` → +2 sobre o total.

### `EstadoCombate.kt`
Estados do ciclo de vida: `AGUARDANDO_INICIATIVA`, `EM_TURNO`, `ATURDIDO_INCAPACITADO`, `DERROTADO`.

### `Personagem.kt`
Entidade central. Campos:
- identidade: `nome`, `raca`, `classe`, `nivel`;
- `atributos: MutableMap<Atributo, Int>` (valores finais, já com bônus racial);
- vida: `pontosDeVidaAtual` / `pontosDeVidaMaximo`;
- equipamento: `armaEquipada`, `armaduraEquipada`, `escudoEquipado`;
- combate: `estado: EstadoCombate`.

Propriedades derivadas (calculadas, não persistidas):
- `bonusProficiencia` — via `bonusDeProficiencia(nivel)`;
- `atributoDeCombate` — DEX se a arma for à distância/acuidade, senão STR;
- `modificadorAtaque` — proficiência (se a classe é proficiente na arma) + mod. do atributo;
- `classeArmadura` — `armadura.caBase + min(modDEX, limite) + (escudo ? 2 : 0)`.

Comportamentos:
- `aplicarDano(valor)` — reduz PV (mínimo 0); a zero, transita para `DERROTADO`;
- `curar(valor)` — aumenta PV sem passar do máximo;
- `subirNivel()` — `nivel++`;
- transições de estado `iniciarTurno()`, `passarTurno()`, `aplicarStatusAturdido()`,
  `expirarStatusAturdido()` — validadas pelo mapa `TRANSICOES` (entidade derrotada não muda).

### `Item.kt` e `Magia.kt`
Entidades simples do inventário (`adicionarItem`) e grimório (`adicionarMagia`).

### `Historico.kt`
- `TipoRolagem` — ATACAR, DANO, MAGIA.
- `RolagemEvento` (value object) — `personagemId`, `tipoRolagem`, `resultado`, `timestamp`.
- `Historico` — registra eventos em memória e lista em ordem cronológica decrescente
  (`registrosDaSessao`).

---

## 5. Camada de domínio — `service`

Serviços puros de regra de negócio, sem dependência de UI ou banco.

### `DadoVirtual.kt`
```kotlin
open class DadoVirtual(private val random: Random = Random.Default) {
    open fun rolar(faces: Int, modificador: Int = 0): Int
}
```
PRNG que simula rolagem de dado. `open` de propósito: testes criam subclasses com valores
determinísticos (ex.: `DadoVirtualSequenciado` em `SimuladorDeCombateTest`).

### `MotorDeRegras.kt`
- `Vantagem` — NENHUMA, VANTAGEM (maior de dois), DESVANTAGEM (menor de dois).
- `ResultadoRolagem` — `resultado` (final), `dados` (individuais), `dadoBruto` (valor natural,
  usado para detectar críticos/falhas).
- Métodos `rolarAtaque(modificador, vantagem)` e `rolarDano(faces, modificador, vantagem)`.

### `RoladorDeAtributos.kt`
Geração de pontuações de habilidade:
- `ARRAY_FIXO` = 15, 14, 13, 12, 10, 8; `PONTOS_DISPONIVEIS` = 27; faixa de compra 8–15.
- `rolar4d6DescartandoMenor()` — 6 conjuntos de 4d6 descartando o menor.
- `rolarUmConjunto()` — soma dos 3 maiores de 4d6.
- `custoDe(valor)` — tabela progressiva do SRD (8→0, 9→1, ... 14→7, 15→9).
- `comprarPontos(valores, pontos)` — valida distribuição dentro do orçamento.
- `pontosRestantes(valores)` — pontos que sobraram.

### `ConstrutorDeFicha.kt`
Monta o `Personagem` final:
- aplica `raca.bonus` sobre os `valoresBase`;
- `pontosDeVidaMaximo(classe, nivel, modCON)` — dado de vida + mod no 1º nível; média
  (dado/2 + 1) + mod nos níveis seguintes;
- `construir(...)` — retorna a ficha com PV no máximo e o equipamento definido.

---

## 6. Camada de domínio — `repository`

Interfaces (contratos) que a infraestrutura implementa:
- `PersonagemRepository` — CRUD + `atualizarPontosDeVida` + `listar`.
- `ItemRepository` — CRUD por personagem.
- `MagiaRepository` — CRUD por personagem.
- `HistoricoRepository` — `registrar` + `listarDaSessao(limite)`.

---

## 7. Camada de aplicação — `application`

### `ObservadorDeMudanca.kt`
```kotlin
fun interface ObservadorDeMudanca {
    fun notificarMudancaDeEstado(personagem: Personagem)
}
```
Contrato do padrão Observer: a UI se registra no simulador e é notificada a cada mudança.

### `SimuladorDeCombate.kt`
Coordena o combate (Seção 7.3). Construtor recebe o `MotorDeRegras`, os repositórios e um
`relogio: () -> Long` (injetável para testes).

- `processarDano(personagem, valor)` — aplica dano, persiste PV e notifica observadores.
- `atacar(atacante, alvo, vantagem)` — ataque completo: d20 + bônus vs. CA do alvo; **natural 20
  acerta sempre (crítico)**, **natural 1 erra sempre**; acerto rola dano da arma e aplica no alvo.
- `testarAtaque(atacante, caDoAlvo, vantagem)` — simula contra uma CA digitada **sem aplicar
  dano real** (usado pelo painel).
- `rolarAtaque` / `rolarDano` / `rolarMagia` — delegam ao motor e registram no histórico.
- `registrarRolagem(...)` — registra o evento no `Historico` do personagem **e** no
  `HistoricoRepository` (persistência) automaticamente.
- `ResultadoDeAtaque(acertou, critico, rolagem, dano)` — data class de retorno.

Regras de dano (SRD 5e): soma dos dados da arma (dobrado no crítico) + modificador do
`atributoDeCombate`. Sem arma → `1` de dano (desarmado).

---

## 8. Camada de infraestrutura — `persistence`

### `DatabaseFactory.kt`
`object DatabaseFactory.init(url)` — conecta ao SQLite (default `jdbc:sqlite:escudo.db`),
habilita `PRAGMA foreign_keys = ON` e cria as tabelas via `SchemaUtils.create(...)`.
⚠️ Não há migrações: se o esquema mudar, remova o `escudo.db` antigo para recriar.

### `Tabelas.kt`
Definições Exposed:
- `PersonagensTable` — id, nome, raca, classe, nivel, os 6 atributos (colunas `forca`...
  `carisma`), PV, `arma` (nullable), `armadura`, `escudo`.
- `ItensTable`, `MagiasTable` — com FK `personagem_id` → `personagens.id`.
- `HistoricoTable` — `personagem_id`, `tipo_rolagem`, `resultado`, `timestamp`.

Funções de mapeamento `ResultRow.toPersonagem()` (faz `valueOf` dos enums), `toItem()`,
`toMagia()`, `toRolagemEvento()`.

### `PersonagemDAO.kt`
DAO central das consultas: `inserir`, `buscarPorId`, `atualizar`, `atualizarPontosDeVida`,
`excluir`, `listar` (ordenado por nome).

### `repositories/` (implementações concretas)
- `ExposedPersonagemRepository` — implementa `PersonagemRepository` delegando ao DAO.
- `ExposedHistoricoRepository` — registra eventos e lista em ordem decrescente por timestamp.
- `ExposedItemRepository` / `ExposedMagiaRepository` — CRUD por personagem.

---

## 9. Camada de apresentação — `presentation`

### `MainApp.kt`
Classe de entrada (JavaFX `Application`):
- `start()` — inicializa o banco, os repositórios e o `SimuladorDeCombate`.
- Navegação: `mostrarSelecao()`, `mostrarCriacao()`, `mostrarPainel(personagem)`.
- `aplicarCena(root)` — troca a raiz da cena (cena única reutilizada) e carrega o
  `estilo.css` uma única vez.
- `main()` no fim do arquivo gera `MainAppKt` (referenciada no Gradle).

### Controllers
| Controller | Tela | Responsabilidades |
|---|---|---|
| `SelecaoPersonagemController` | Seleção | Lista fichas como cards, estado vazio, navegação p/ criação/painel |
| `CriacaoPersonagemController` | Criação | Formulário completo, validação (AC 1.3), geração de atributos, resumo ao vivo, salvar |
| `PainelDeJogoController` | Painel | Lista de fichas, ações de mesa (F5/F6/F7), observador do simulador |

Destaques do **CriacaoPersonagemController**:
- `MetodoDeAtributos` enum — ROLAR / ARRAY_FIXO / COMPRA_DE_PONTOS.
- Mapas `camposDeAtributo` e `modulosDeAtributo` ligando cada `Atributo` aos campos da UI.
- `atualizarResumo()` reconstrói um `Personagem` de preview via `ConstrutorDeFicha` a cada
  mudança para exibir PV/CA/ataque/proficiência/bônus racial.
- `atualizarPontosEValidar()` habilita os campos só no modo compra e valida o orçamento.

Destaques do **PainelDeJogoController**:
- Atalhos configuráveis (RNF05) via `KeyCodeCombination` aplicados nos aceleradores da cena.
- `notificarMudancaDeEstado` (Observer) recarrega a lista e atualiza o painel.
- `ListCellFicha` (cellFactory) — renderiza cada ficha com avatar, nome, raça/classe, PV/CA e
  badge de estado (azul; vermelho se `DERROTADO`).

### Recursos FXML
- `SelecaoPersonagem.fxml` — header + FlowPane de cards.
- `CriacaoPersonagem.fxml` — dois cards (Identidade/Atributos | Equipamento/Resumo).
- `PainelDeJogo.fxml` — header + ListView + card de detalhes com tiles de atributos.

### `estilo.css`
Tema dark "mesa de D&D" inspirado em Tailwind: classes `.card`, `.botao-primario`,
`.campo`, `.combo`, `.badge`, `.stat-tile`, `.avatar-ficha`, `.progresso-pv`, etc.

---

## 10. Testes

Local: `src/test/kotlin/`. Suíte atual: **74 testes** (executar com `./gradlew test`).

| Arquivo | O que testa |
|---|---|
| `domain/service/DadoVirtualTest.kt` | PRNG, faixas válidas, validação de `faces` |
| `domain/service/MotorDeRegrasTest.kt` | Vantagem/desvantagem, modificadores |
| `domain/service/RoladorDeAtributosTest.kt` | 4d6 drop menor, array fixo, custos, compra de pontos |
| `domain/service/ConstrutorDeFichaTest.kt` | Mods de atributo, bônus de proficiência, bônus raciais, PV, CA, bônus de ataque |
| `domain/model/PersonagemTest.kt` | Dano, cura, nível, transições de estado |
| `domain/model/HistoricoTest.kt` | Registro e ordenação cronológica |
| `domain/model/ItemMagiaTest.kt` | Inventário e grimório |
| `application/SimuladorDeCombateTest.kt` | Ataque 5e, crítico, falha crítica, dano, vantagem, observer, histórico |
| `infrastructure/persistence/RepositorioIntegracaoTest.kt` | CRUD real no SQLite (criação, atualização, listagem, FK) |

**Helper:** `FichasDeTeste.kt` fornece fichas prontas (ex.: `guerreiroHumano`) para os testes.

---

## 11. Fluxos principais

### Criação de ficha
```
Usuário preenche o formulário
    → CriacaoPersonagemController.gerarAtributos() (RoladorDeAtributos)
    → atualizarResumo() (ConstrutorDeFicha → Personagem de preview)
    → salvar() (valida AC 1.3) → personagemRepository.criar()
    → MainApp.mostrarSelecao()
```

### Combate (aplicar dano)
```
Painel: Atacar/Aplicar Dano
    → PainelDeJogoController.atacarAlvo() / aplicarDano()
    → SimuladorDeCombate.testarAtaque() / processarDano()
        → MotorDeRegras.rolarAtaque() (DadoVirtual)
        → Personagem.aplicarDano() (máquina de estados)
        → PersonagemDAO.atualizarPontosDeVida()
        → ObservadorDeMudanca.notificarMudancaDeEstado()  → UI atualiza (≤ 200 ms, RNF02)
    → Historico registra o evento (domínio + banco)
```

---

## 12. Convenções e boas práticas

- **Idioma do código:** comentários, docstrings, nomes de domínio e mensagens em português;
  enums e variáveis em inglês nos padrões Kotlin (`CamelCase`, `snake_case` para colunas).
- **Camadas:** a UI nunca importa Exposed/SQLite; o domínio não conhece JavaFX. Repositórios
  são sempre acessados por interface.
- **TDD:** cada novo comportamento tem teste unitário antes/depois; rode `./gradlew test`
  antes de concluir qualquer mudança.
- **Build antes de commit:** `./gradlew clean build` (compila + roda os testes).
- **Enums persistem pelo `.name`** (e são lidos com `valueOf`). Renomear um enum quebra o
  banco — é preciso recriar o `escudo.db`.
- **FXML:** alterações de layout são testadas apenas executando o app (não há testes de UI).

## 13. Armadilhas comuns

- **`&` em FXML:** em atributos de texto, use `&amp;` (ex.: "Nova Ficha D&amp;D 5e"). Um `&`
  cru quebra o carregamento com `XMLStreamException`.
- **Padding em FXML:** não use `padding="24"` (erro de coerção). Use `padding="24 24 24 24"`
  ou, preferencialmente, aplique no CSS (`-fx-padding`).
- **`loader.root` no Kotlin:** o campo `root` do `FXMLLoader` é privado; use
  `loader.getRoot() as Parent`.
- **cellFactory:** precisa de `javafx.util.Callback { ListCellFicha() }` — sem isso a lista
  fica vazia.
- **Testar rolagens:** `DadoVirtual` é `open` justamente para permitir subclasses
  determinísticas nos testes (não confie em dados aleatórios em testes).
- **Banco antigo:** mudanças de schema não geram migração. Apague `escudo.db` e o app recria.
- **Env de display:** em Linux sem interface gráfica, execute com `DISPLAY=:1` (ou outro
  display X disponível).

## 14. Como adicionar código novo

1. **Defina onde o código pertence** pela regra de camadas (model → service/repository →
   application → infrastructure → presentation).
2. **Crie/altere o enum ou entidade no `model`** se for regra de negócio pura.
3. **Escreva o teste** em `src/test/kotlin` (use `FichasDeTeste` quando precisar de uma ficha).
4. **Implemente** e rode `./gradlew test`.
5. Se persistir algo novo, atualize `Tabelas.kt` + o DAO/repositório correspondente.
6. Se for UI, altere o FXML + CSS e valide visualmente com `./gradlew run`.
7. Registre o trabalho no `ISSUES.md` (seguindo o padrão das issues existentes).
8. Rode `./gradlew clean build` antes de commitar.
