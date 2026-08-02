# ⚔ O Escudo do Mestre Digital

Aplicação desktop para auxiliar o **Mestre de uma mesa de RPG** na condução de sessões de
**Dungeons & Dragons 5ª edição (D&D 5e)**. A ferramenta digitaliza as fichas de personagem,
automatiza as rolagens de dados (seguindo as regras do SRD 5e) e mantém um painel de combate
com histórico de rolagens, tudo persistido localmente.

Desenvolvida em **Kotlin** com interface **JavaFX** e banco de dados **SQLite**.

---

## O que o programa faz

- **Criação de fichas D&D 5e** — define nome, raça, classe, nível, atributos e equipamento.
- **Três métodos de geração de atributos** (regras oficiais):
  - **Rolagem** — 4d6 descartando o menor, um conjunto para cada atributo;
  - **Array fixo** — 15, 14, 13, 12, 10, 8;
  - **Compra de pontos** — 27 pontos, cada atributo entre 8 e 15.
- **Aplicação automática das regras**:
  - bônus raciais de habilidade (ex.: Anão +2 Constituição);
  - pontos de vida a partir do dado de vida da classe + modificador de Constituição;
  - Classe de Armadura (armadura + Destreza + escudo);
  - bônus de ataque (proficiência + atributo).
- **Painel de Jogo e Simulador** — ações de mesa:
  - **Rolar Ataque (F5)** — d20 + bônus contra a CA digitada, com crítico (20) e falha crítica (1);
  - **Rolar Dano (F6)** — dado da arma + modificador;
  - **Aplicar Dano (F7)** — reduz os PV do personagem selecionado.
- **Seleção de fichas** — cards dos personagens salvos para abrir na mesa.
- **Persistência local** — as fichas ficam salvas em um arquivo SQLite (`escudo.db`).

## Catálogo disponível (SRD 5e)

- **9 raças**: Anão, Elfo, Halfling, Humano, Draconato, Gnomo, Meio-Elfo, Meio-Orc, Tiefling.
- **12 classes**: Bárbaro, Bardo, Clérigo, Druida, Guerreiro, Monge, Paladino, Patrulheiro, Ladino, Feiticeiro, Bruxo, Mago.
- **Armas** e **armaduras** do SRD com seus dados de dano e CAs.

---

## Como executar

### Pré-requisitos

- **Java 21** (JDK) — projeto usa a toolchain 21.
- Sistema com interface gráfica (o app é JavaFX; em Linux é preciso de um display, por exemplo X11).

### Passos

```bash
# 1. Compilar e executar
./gradlew run
```

A janela "O Escudo do Mestre Digital" abre na tela de **seleção de fichas**.

Outros comandos úteis:

```bash
# Compilar apenas (gera build/libs/escudo-mestre-digital-1.0.0.jar)
./gradlew build

# Executar os testes
./gradlew test
```

> **Dica (Linux com display secundário):** se estiver em um ambiente sem interface, use
> `DISPLAY=:1 ./gradlew run` apontando para um servidor X disponível.

### Onde ficam os dados

As fichas e o histórico de rolagens são persistidos em `escudo.db` (SQLite), criado na raiz
do projeto na primeira execução. O banco é recriado automaticamente se for removido.

---

## Guia rápido de uso

1. Na tela inicial, clique em **"+ Nova Ficha"**.
2. Preencha a identidade (nome, raça, classe, nível), escolha o **método de atributos**
   e clique em **"Gerar / Rolar"** para distribuir os valores.
3. Escolha **arma**, **armadura** e se deseja **escudo** — o resumo da ficha (PV, CA, ataque,
   proficiência, bônus racial) atualiza ao vivo.
4. Clique em **"Salvar Ficha"** e depois **"Abrir ficha"** no card criado.
5. No **Painel de Jogo**, use os atalhos **F5 / F6 / F7** (ou os botões) para rolar ataques
   contra a CA do alvo, rolar dano e aplicar dano ao personagem selecionado.

---

## Tecnologias

| Camada           | Tecnologia                              |
|------------------|-----------------------------------------|
| Linguagem        | Kotlin 2.2                              |
| Interface        | JavaFX 21 (FXML + CSS)                  |
| Persistência     | Exposed ORM 0.56 + SQLite               |
| Build            | Gradle (wrapper 8.14)                   |
| Testes           | JUnit 5 + kotlin-test                   |

---

## Documentação

- **Para usuários:** este arquivo (README).
- **Para desenvolvedores:** veja [`CODIGO.md`](CODIGO.md) — arquitetura, estrutura de pastas,
  explicação das classes e como contribuir com código.
- **Especificação do projeto:** `Escudo_do_Mestre_Digital_v1_2.md`.
- **Backlog de issues:** `ISSUES.md`.
