# 🧩 Guia do Código — O Escudo do Mestre Digital

Documentação técnica para quem vai **ler ou escrever código** neste projeto. Aqui você
encontra a arquitetura, a estrutura de pastas, o papel de cada classe e as convenções
de desenvolvimento adotadas.

> Este guia reflete a **implementação corrente** do repositório (`src/main/kotlin`).
> Para o documento de requisitos/projeto, veja o `README.md`.

---

## Índice

1. [Visão geral da arquitetura](#1-visão-geral-da-arquitetura)
2. [Stack e ferramentas](#2-stack-e-ferramentas)
3. [Estrutura de pastas](#3-estrutura-de-pastas)
4. [Camada de domínio — model](#4-camada-de-domínio--model)
5. [Camada de domínio — service](#5-camada-de-domínio--service)
6. [Camada de infraestrutura — persistência](#6-camada-de-infraestrutura--persistência)
7. [Camada de apresentação — interface](#7-camada-de-apresentação--interface)
8. [Testes](#8-testes)
9. [Fluxos principais](#9-fluxos-principais)
10. [Convenções e boas práticas](#10-convenções-e-boas-práticas)
11. [Armadilhas comuns](#11-armadilhas-comuns)
12. [Como adicionar código novo](#12-como-adicionar-código-novo)

---

## 1. Visão geral da arquitetura

O projeto segue uma arquitetura em camadas inspirada em **DDD (Domain-Driven Design)**,
com **MVC** na interface e **Observer** para sincronizar o domínio com a UI:

```
presentation (JavaFX: MainApp, controllers, FXML, CSS)
    │
    ▼ (usa)
domain (model + service — regras de negócio, Kotlin puro)
    ▲
    │
infrastructure (persistência Exposed/SQLite, DAO)
```

- **domain** é o núcleo: entidades, enums, serviços de regra e o padrão Observer.
  Não conhece JavaFX nem banco.
- **infrastructure** implementa os DAOs com Exposed ORM sobre SQLite.
- **presentation** é a interface JavaFX (FXML + controllers + CSS).
- Não existe camada `application` no código principal: o `SimuladorDeCombate` e o
  `MotorDeEscalonamento` vivem em `domain/service`.

---

## 2. Stack e ferramentas

| Item            | Versão / Detalhe                                   |
|-----------------|----------------------------------------------------|
| Linguagem       | Kotlin (plugin JVM) 1.9.23                         |
| JVM             | Java 21 (toolchain, source/target 21)              |
| UI              | JavaFX 21 (módulos `controls` e `fxml`, plugin openjfx 0.1.0) |
| ORM             | Exposed 0.48.0 (`core`, `jdbc`, `dao`)              |
| Banco           | SQLite (`org.xerial:sqlite-jdbc:3.44.1.0`)          |
| Build           | Gradle Wrapper 8.10 (`./gradlew`)                   |
| Testes          | JUnit 5 (Jupiter 5.10.0) + `kotlin-test` + `kotlinx-coroutines-test` |

Configurações: `build.gradle.kts`, `gradle.properties`, `settings.gradle.kts`.
`group = "com.escudo.mestre"`, `version = "1.6.0"`.
Classe de entrada: `presentation.MainAppKt` (o plugin `application` define
`mainClass.set("presentation.MainAppKt")`, e `jvmToolchain(21)`.

---

## 3. Estrutura de pastas

```
src/
├── main/
│   ├── kotlin/
│   │   ├── domain/model/          # Entidades, enums, Observer
│   │   ├── domain/service/        # Regras (rolagens, construção, magia, escalonamento)
│   │   ├── infrastructure/persistence/  # Exposed: tabelas, DAO, fábrica de conexão
│   │   └── presentation/          # MainApp + controllers JavaFX
│   └── resources/
│       ├── css/styles.css         # Folha de estilo (tema dark D&D)
│       └── fxml/                  # Telas: menu_inicial, criar_heroi, gerenciar_fichas, painel_combate
└── test/kotlin/
    ├── application/               # Testes do SimuladorDeCombate
    ├── domain/model/              # Personagem, Historico, Item/Magia, Magia 5e
    ├── domain/service/            # DadoVirtual, MotorDeRegras, Slots, Catálogos, point buy…
    └── infrastructure/            # Persistence (integração CRUD no SQLite)
```

---

## 4. Camada de domínio — `model`

Arquivos em `domain/model/`. Regras e entidades puras, sem dependência de UI ou banco.

### `Personagem.kt`
Entidade central, segue o padrão **Observable**:
- Identidade: `id`, `nome`, `raca`, `classe`, `nivel`.
- Vida: `pontosDeVidaAtual` / `pontosDeVidaMaximo` (var).
- `classeArmadura`, `estadoCombate: EstadoCombate`.
- Coleções em memória: `inventario: MutableList<Item>`, `grimorio: MutableList<Magia>`,
  `historico: MutableList<Historico>`.

Comportamentos:
- `aplicarDano(valor)` — reduz PV (mínimo 0); a zero, `estadoCombate = DERROTADO`, notifica observers.
- `curar(valor)` — aumenta PV sem passar do máximo; volta de `DERROTADO` para `AGUARDANDO_INICIATIVA`.
- `subirNivel()` — `nivel++`, rola o dado de vida da classe (`DadoVirtual`) e soma aos PV máximos/atuais.
- `adicionarObserver` / `removerObserver` / `notificarObservers` — contrato `PersonagemObservable`.

### `PersonagemObserver.kt`
```kotlin
interface PersonagemObserver { fun onPersonagemAlterado(personagem: Personagem) }
interface PersonagemObservable { ...adicionarObserver() ...notificarObservers() }
```

### `EstadoCombate.kt`
`AGUARDANDO_INICIATIVA`, `EM_TURNO`, `ATURDIDO_INCAPACITADO`, `DERROTADO`.

### `Raca.kt`
Enum das 9 raças do SRD com o mapa `bonus` (aumentos de habilidade):
- `ANAO` → CON+2; `ELFO`/`HALFLING` → DES+2; `HUMANO` → +1 em todos; `DRACONATO` → FOR+2, CAR+1;
  `GNOMO` → INT+2; `MEIO_ELFO` → CAR+2; `MEIO_ORC` → FOR+2, CON+1; `TIEFLING` → INT+1, CAR+2.

### `Classe.kt`
Enum das 12 classes com `hitDice` (dado de vida) e `nomeExibicao`:
`BARBARO`(d12), `GUERREIRO`/`PALADINO`/`PATRULHEIRO`(d10), demais d8, `FEITICEIRO`/`MAGO`(d6).

### `Item.kt` e `Magia.kt`
- `Item(id, nome, peso: Double, quantidade)`.
- `Magia` — modelo **fiel ao SRD 5e (2024)**:
  `id, nome, nivel, escola: EscolaDeMagia, tempoConjuracao, alcance, componentes, duracao, requerConcentracao, descricao, preparada, slotGasto`.
  - `ehTruque` (`nivel == 0`).
  - `copiaParaGrimorio(novoId)` — cria cópia própria para o grimório (id novo é necessário porque o PK é `id`).

### `EscolaDeMagia.kt`
Enum das 8 escolas (Abjuração, Conjuração, Adivinhação, Encantamento, Evocação, Ilusão, Necromancia, Transmutação) + `deTexto()`.

### `HabilidadeDeClasse.kt`
`data class HabilidadeDeClasse(nome, nivel, descricao)`.

### `DadoVirtual.kt`
`object DadoVirtual.rolar(faces, modificador = 0)` — PRNG sobre `kotlin.random.Random`.
Lança `IllegalArgumentException` para `faces <= 0`.

### `RolagemEvento.kt` e `Historico.kt`
- `RolagemEvento(personagemId, tipoRolagem, resultado, timestamp = now)`.
- `Historico(id, personagemId, tipoRolagem, ultimo resultado, timestamp)` com
  `companion fun registrar(evento): Historico`.

---

## 5. Camada de serviço — `service`

Regras de negócio, Kotlin puro.

### `MotorDeRegras.kt`
- `calcularModificador(atributo)` — `(atributo - 10) / 2` **arredondado para baixo** (16→+3, 9→−1).
- `rolarComVantagem(faces, mod)` / `rolarComDesvantagem(...)` — maior/menor de duas rolagens.
- `rolarAtaque(mod, vantagem, desvantagem)` — d20 + mod; combina vantagem/desvantagem.
- `rolarDano(faces, quantidade, mod)`.

### `RoladorDeAtributos.kt`
Geração de atributos **fiel ao SRD 5e**:
- `ORCAMENTO_PONTOS = 27`, `VALOR_MINIMO = 8`, `VALOR_MAXIMO = 15`.
- `rolar4d6DropLowest()` — 6 conjuntos de 4d6 descartando o menor; intervalo 3–18.
- `arrayFixo()` → `[15, 14, 13, 12, 10, 8]`.
- `custo(valor)` — tabela progressiva do SRD (8→0, 9→1, 10→2, 11→3, 12→4, 13→5, 14→7, 15→9);
  lança exceção fora de 8–15.
- `custoTotal`, `pontosRestantes` (27 − Σ custos).
- `compraDePontos(distribuicao)` — exige 6 valores em 8–15 com custo ≤ 27.
- `podeAdicionar(valor, pontos)` / `podeDiminuir(valor)` — controle dos steppers da UI.

### `ConstrutorDeFicha.kt`
- `construir(nome, raca, classe, atributosBase)` — aplica o bônus racial, calcula
  `PV max = hitDice + modCON`, `CA = 10 + modDES` e retorna o `Personagem`.
- `calcularBonusInicialAtaque(atributo)` — mod. do atributo principal.

### `SimuladorDeCombate.kt`
Coordena as rolagens e o histórico:
- `processarDano(p, valor)` — aplica dano no personagem.
- `rolarAtaqueParaPersonagem(p, mod, vantagem, desvantagem)` — rola e registra `ATAQUE` no histórico.
- `rolarDanoParaPersonagem(p, faces, quantidade, mod)` — registra `DANO`.
- `aplicarDanoERegistrar(p, valor)` — aplica dano e registra `APLICAR_DANO`.

### `MotorDeEscalonamento.kt`
Ordem de iniciativa e turnos:
- `ordenarIniciativa(personagens)` — rola iniciativa com `MotorDeRegras.rolarAtaque(0)`
  e marca o primeiro como `EM_TURNO`, os demais `AGUARDANDO_INICIATIVA`.
- `avancarTurno(ordenados, indice)` — passa ao próximo (wrap-around) e reseta o atual,
  preservando `DERROTADO`/`ATURDIDO_INCAPACITADO`.

### `SlotsDeMagia.kt`
Tabelas oficiais de **D&D 5e**:
- conjuradores **plenos** (`BARDO, CLERIGO, DRUIDA, FEITICEIRO, MAGO`), **metade**
  (`PALADINO`, `PATRULHEIRO`) e **pacto** (`BRUXO`).
- `nivelDeConjuracao(classe, nivel)` — plenos: nível; metade: `(nível+1)/2`; Bruxo: nível do pacto.
- `slots(classe, nivelDeClasse)` → `Map<círculo, quantidade>`.
- `circulos(classe, nivel)` — maior círculo alcançado.
- `atributoDeConjuracao(classe)` — `INT`/`SAB`/`CAR` conforme a classe (ou `-`).

### `CatalogoDeMagias.kt`
20 magias SRD 5e completas. Métodos:
- `magias` (lista), `porNome`, `porId`, `magiasDaClasse(classe)`, `todasAsClasses()`.

### `CatalogoHabilidades.kt`
Habilidades de classe por nível (nome, nível, descrição) para as 12 classes:
- `habilidadesDaClasse(classe)`, `habilidadesAteNivel(classe, nivel)`.

---

## 6. Camada de infraestrutura — `persistence`

### `SQLiteDatabaseFactory.kt`
`object SQLiteDatabaseFactory.conectar(caminho = "escudo.db")` — conecta ao SQLite
(JDBC driver `org.sqlite.JDBC`), cria as tabelas via `SchemaUtils.create(...)`, e
`reset()` zera a instância (usado nos testes).

### `Tables.kt`
Definições Exposed com PK na coluna `id`:
- `PersonagemTable("personagens")` — `id, nome, raca, classe, nivel, pontos_de_vida_atual,
  pontos_de_vida_maximo, classe_armadura, estado_combate`.
- `ItemTable("itens")` — `id, personagem_id (FK), nome, peso, quantidade`.
- `MagiaTable("magias")` — `id, personagem_id (FK), nome, nivel, escola, tempo_conjuracao,
  alcance, componentes, duracao, requer_concentracao, descricao, preparada, slot_gasto`.
- `HistoricoTable("historico")` — `id, personagem_id, tipo_rolagem, resultado, timestamp`.

> ⚠️ Não há migrações: se o esquema mudar, apague o `escudo.db` antigo para o app recriar.

### DAOs
- `PersonagemDAO` — `inserir`, `atualizar`, `buscarPorId`, `listarTodos`, `deletar`.
  Correntes com FK e estado via `EstadoCombate.valueOf`.
- `ItemDAO` — `inserir(item, personagemId)`, `listarPorPersonagem`, `deletar`.
- `MagiaDAO` — `inserir(magia, personagemId)`, `listarPorPersonagem`, `buscarPorId`, `deletar`;
  mapeia todos os campos da magia (escola, tempo, alcance, etc.).
- `HistoricoDAO` — `inserir`, `listarPorPersonagem`, `listarTodos` (ordem cronológica decrescente).

---

## 7. Camada de apresentação — `presentation`

### `MainApp.kt`
JavaFX `Application`:
- `start()` — conecta ao banco (`SQLiteDatabaseFactory.conectar("escudo.db")`) com fallback,
  carrega `menu_inicial.fxml`, aplica `styles.css`, janela 1366×768 (mínima igual).
- `main()` no fim do arquivo lança a aplicação (`presentation.MainAppKt`).

### Controllers
| Controller | Tela | Responsabilidades |
|---|---|---|
| `MenuInicialController` | menu_inicial | Navega para Criar Herói / Gerenciar Fichas / Painel de Combate |
| `CriarHeroiController` | criar_heroi | Formulário; geração de atributos; **steppers `−/+` de compra de pontos**; preview; salvar |
| `GerenciarFichasController` | gerenciar_fichas | Lista de fichas, edição, inventário e grimório (associar itens/magias do catálogo) |
| `PainelCombateController` | painel_combate | Seleção de personagem, rolar ataque/dano, aplicar dano/curar, nível, histórico; atalhos F5/F6/F7 |

Destaques do **CriarHeroiController**:
- Modos de geração: "Rolagem de Dados", "Matriz Fixa", "Compra de Pontos" (`comboMetodo`).
- **Compra de Pontos:** cada atributo tem botões `−`/`+` e um `TextField` de 50 px.
  O selo `lblPontos` mostra `pontosRestantes`; `+` é desabilitado sem saldo (custo do valor
  seguinte excede o orçamento) e em 15, `−` é em 8; o selo fica vermelho se estourar o orçamento.
  Os campos ficam `editable=false` (apenas leitura) nesse modo; nos demais são editáveis.
- `atualizarPreview()` reconstrói um `Personagem` de preview via `ConstrutorDeFicha` e mostra
  PV máx, CA e atributo de conjuração (via `SlotsDeMagia`).

Destaques do **`PainelCombateController`**:
- Atalhos (RNF05) via `scene.addEventFilter` — `F5` rolar ataque, `F6` rolar dano, `F7` aplicar dano.
- Implementa `PersonagemObserver`: `onPersonagemAlterado` atualiza a UI na FX Thread (`Platform.runLater`).
- `salvarHistorico` persiste os eventos do personagem no `HistoricoDAO`.
- `executarRolarDano` deriva o dado de dano da **arma equipada** (`Personagem.dadosDeDano` →
  quantidade/faces/bônus) e preenche o resultado em `txtDano`/`txtResultado`; como o bônus vem da
  arma (não do valor digitado), rolagens sucessivas são independentes e **não acumulam**.

### Recursos FXML
`src/main/resources/fxml/`: `menu_inicial.fxml`, `criar_heroi.fxml`,
`gerenciar_fichas.fxml`, `painel_combate.fxml`; e `css/styles.css` (tema).

> O README descreve o Painel com "abas (Ficha/Habilidades/Grimório/Inventário/Histórico)". Hoje a
> implementação usa janelas separadas (`menu_inicial`, `gerenciar_fichas`, `painel_combate`),
> sem `TabPane`; esse fluxo de abas é evolução pretendida, não presente no código atual.

---

## 8. Testes

Local: `src/test/kotlin/`. Suíte atual: **134 testes** (executar com `./gradlew test`).

| Arquivo | Qtd | O que testa |
|---|---|---|
| `domain/model/PersonagemTest.kt` | 6 | `aplicarDano`/`curar` (estado), `subirNivel`, Observer |
| `domain/model/HistoricoTest.kt` | 2 | Registro e ordenação cronológica |
| `domain/model/ItemMagiaTest.kt` | 3 | Inventário e grimório |
| `domain/model/Magia5eTest.kt` | 8 | Modelo completo da magia, truque, cópia, escola |
| `domain/model/EquipamentoTest.kt` | 7 | `Arma`/`Armadura` SRD, CA, escudo, proficiência, duas mãos |
| `domain/model/RegrasDeAtributoTest.kt` | 5 | Enum `Atributo`, chaves de atributo, regras de atributo |
| `domain/model/CombatePersonagemTest.kt` | 11 | `CD`/`bonusAtaqueDeMagia`, descanso curto, `subirNivel(mediaAoNivelar)` |
| `domain/service/DadoVirtualTest.kt` | 4 | PRNG, faixas, exceções |
| `domain/service/MotorDeRegrasTest.kt` | 6 | Vantagem/desvantagem, modificador (aresta baixa), dano, ataque |
| `domain/service/TestarAtaqueTest.kt` | 6 | Crítico, falha, bônus de proficiência, dano desarmado |
| `domain/service/RoladorDeAtributosTest.kt` | 12 | 4d6 drop menor, array fixo, custos, orçamento, `podeAdicionar`/`podeDiminuir` |
| `domain/service/ConstrutorDeFichaTest.kt` | 4 | bônus racial, CA, bônus de ataque, construção |
| `domain/service/SlotsDeMagiaTest.kt` | 16 | Espaços de magia por classe/nível, pacto, atributo de conjuração |
| `domain/service/CatalogoDeMagiasTest.kt` | 7 | Catálogo por classe, busca por nome/id, validade |
| `domain/service/CatalogoHabilidadesTest.kt` | 8 | Habilidades por classe e por nível |
| `domain/service/MotorDeEscalonamentoTest.kt` | 7 | Ordem de iniciativa e turnos (inclui pular `DERROTADO`/`ATURDIDO`) |
| `application/SimuladorDeCombateTest.kt` | 9 | Rolagens de ataque/dano, histórico, dano fatal, vantagem |
| `infrastructure/RepositorioIntegracaoTest.kt` | 13 | CRUD real no SQLite (personagem, item, magia completa, histórico, equipamento/escudo, alteração manual de estado, delete) |

O banco usado nos testes de integração é um arquivo SQLite temporário
(`test_escudo.db`) recriado a cada teste via `SQLiteDatabaseFactory.reset()`.

---

## 9. Fluxos principais

### Criação de ficha (com compra de pontos)
```
CriarHeroiController (comboMetodo = "Compra de Pontos")
    → campos começam em 8, lblPontos = 27
    → clique em '+'/−( ) → RoladorDeAtributos.podeAdicionar/pontosRestantes → valida → atualiza
    → salvarHeroi() → ConstrutorDeFicha.construir(...) → PersonagemDAO.inserir()
```

### Combate (aplicar dano)
```
PainelCombateController (btnAplicarDano / F7)
    → SimuladorDeCombate.aplicarDanoERegistrar(p, valor)
        → Personagem.aplicarDano (estado DERROTADO se for 0)
        → p.historico.adicionar(RolagemEvento)
    → PersonagemDAO.atualizar(p)
    → HistoricoDAO.inserir(...)  → PersonagemObserver.notificarObservers  → UI (Platform.runLater)
```

### Subir de nível (UC06)
```
PainelCombateController → btnLevelUp
    → Personagem.subirNivel() (rola hitDice, soma PV) → PersonagemDAO.atualizar → Alert
```

---

## 10. Convenções e boas práticas

- **Idioma:** domínio e mensagens em português; identificadores em inglês (`camelCase`),
  colunas em `snake_case`.
- **Camadas:** a UI não importa Exposed/SQLite; o domínio não conhece JavaFX. Os DAOs são a
  única ponte para o banco.
- **Observer:** mudanças de PV são propagadas via `PersonagemObserver` e refletidas na FX
  thread; nunca bloqueie a UI com DB.
- **TDD:** cada comportamento novo tem teste em `src/test/kotlin`; rode `./gradlew test` antes
  de concluir. Build completo: `./gradlew clean build`.
- **Enums persistem por `.name`** e são lidos com `valueOf`. Renomear quebra o banco = recriar `escudo.db`.
- **FXML:** mudanças de layout validadas apenas rodando o app (não há testes de UI).
- **`./gradlew run`** para iniciar a aplicação localmente.

---

## 11. Armadilhas comuns

- **`&` em FXML:** use `&amp;` em textos de atributo; um `&` cru estourou `XMLStreamException`.
- **`padding="24"` em FXML:** use `padding="24 24 24 24"` (era um único valor quebrado).
- **`loader.root` em Kotlin:** use `loader.getRoot()` (o campo `root` é privado).
- **Exposed `select { }` deprecado:** a DSL `select(builder)` gera warning; ao atualizar o Exposed,
  use `selectAll().where(...)`. Hoje o código usa `SqlExpressionBuilder.eq` nos DAOs.
- **Slots de magia:** conjuradores de pacto (Bruxo) não usam a tabela plena — use `SlotsDeMagia`.
- **Banco antigo:** mudanças de schema não geram migração. Apague `escudo.db` e o app recria.
- **Env de display (Linux):** sem interface gráfica, execute com `DISPLAY=:1`.

---

## 12. Como adicionar código novo

1. **Defina onde o código pertence** pelo regra de camada (model → service → persistence → presentation).
2. **Crie/altere o enum ou entidade em `domain/model`** se for regra de negócio pura.
3. **Escreva o teste** em `src/test/kotlin` (TDD) e rode `./gradlew test`.
4. **Implemente** o comportamento; se for regra de conjuração/atributos, use
   `SlotsDeMagia`, `RoladorDeAtributos` e `ConstrutorDeFicha`.
5. **Para persistir algo novo**, atualize `Tables.kt` + o DAO correspondente.
6. **Para UI,**** altere o FXML + CSS e valide com `./gradlew run`.
7. **Atualize este `CODIGO.md` e o README** (contagem de testes, features e stack).
8. Rode `./gradlew clean build` antes de commitar.