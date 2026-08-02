# Backlog — O Escudo do Mestre Digital

Issues no formato GitHub, derivadas diretamente da documentação (`Escudo_do_Mestre_Digital_v1_2.md`).
Cada issue referencia os requisitos e artefatos do documento que deve implementar.

## #1 — Setup do projeto Gradle/Kotlin

**Tipo:** enhancement · **Prioridade:** alta

**Corpo:** Inicializar o projeto Kotlin com Gradle conforme RNF01 ("desenvolvido utilizando a linguagem Kotlin e o ecossistema Gradle"). Configurar wrapper, dependências e a estrutura de pacotes da Visão de Desenvolvimento (§6.3): camada de interface (JavaFX), motor de regras (Kotlin puro) e repositório de dados (Exposed ORM).

**Dependências:** Exposed (core/jdbc/dao), sqlite-jdbc, JavaFX (controls/fxml), JUnit 5 + kotlin-test, coroutines.

**Critérios de aceite:**
- `./gradlew build` compila o projeto com todos os módulos base criados.
- Estrutura de pacotes `com.escudomestre.digital.{domain,application,infrastructure,presentation}` criada.
- `.gitignore` presente, ignorando `.gradle/`, `build/` e `.idea/`.

**Requisitos:** RNF01 · §6.3

---

## #2 — DadoVirtual e Motor de Regras (rolagens com vantagem/desvantagem)

**Tipo:** enhancement · **Prioridade:** alta

**Corpo:** Implementar o serviço de domínio `DadoVirtual` (PRNG) e o `MotorDeRegras`, responsáveis pelo cálculo automático de rolagens de ataque e dano, incluindo a lógica de vantagem/desvantagem (RU02/RF02). Em TDD: testes unitários de distribuição do PRNG e casos de borda (AC 2.1–2.3, §8.2).

**Critérios de aceite:**
- `DadoVirtual.rolar(faces, modificador)` retorna um inteiro no intervalo `[1 + modificador, faces + modificador]` (§7.2).
- Rolagem com vantagem executa duas rolagens e retorna o maior valor; desvantagem retorna o menor (AC 2.2).
- Motor de regras aplica modificadores de atributos sobre o resultado do PRNG (RF02).

**Requisitos:** RU02 · RF02 · AC 2.1–2.3 · §8.2

---

## #3 — Entidades do domínio (Personagem, Item, Magia, Historico)

**Tipo:** enhancement · **Prioridade:** alta

**Corpo:** Implementar as entidades de domínio conforme o diagrama de classes (§7.2): `Personagem` (id, nome, raca, classe, nivel, pontosDeVidaAtual, pontosDeVidaMaximo, classeArmadura; comportamentos aplicarDano, curar, subirNivel), `Item`, `Magia` e `Historico` (registrar RolagemEvento com personagemId, tipoRolagem, resultado, timestamp). Modelar o ciclo de vida de combate com o diagrama de estados (§7.4). Em TDD, um teste por critério de aceite (RU01/RU03).

**Critérios de aceite:**
- Aplicar dano reduz `pontosDeVidaAtual`; curar aumenta, sem ultrapassar o máximo (AC 2.3 relacionado).
- `subirNivel()` incrementa `nivel`.
- `Historico.registrar()` grava personagemId, tipoRolagem, resultado e timestamp sem ação manual (AC 3.1).
- Estado da entidade transita conforme §7.4 (AguardandoIniciativa → EmTurno → ... → Derrotado quando PV ≤ 0).

**Requisitos:** RU01 · RU03 · RF03 · §7.2 · §7.4 · AC 3.1

---

## #4 — Camada de persistência Exposed/SQLite

**Tipo:** enhancement · **Prioridade:** alta

**Corpo:** Implementar a camada de repositório com Exposed ORM sobre SQLite conforme RF01/RNF03 e o diagrama de componentes (§6.6). Criar as tabelas `Personagens`, `Itens`, `Magias` e `Historico`, o `PersonagemDAO` e as interfaces de repositório (DDD). Em TDD: testes de integração de CRUD e integridade referencial (§8.3).

**Critérios de aceite:**
- CRUD completo de Ficha, Item e Magia via Exposed (RF01).
- Persistência local em arquivo SQLite (RNF03).
- Integridade referencial entre Personagem, Item, Magia e Historico (§8.3).

**Requisitos:** RU01 · RF01 · RNF03 · §8.3

---

## #5 — Simulador de Combate com notificação Observer (PV ≤ 200 ms)

**Tipo:** enhancement · **Prioridade:** alta

**Corpo:** Implementar o `SimuladorDeCombate` (camada de aplicação) que processa dano e coordena a persistência, notificando a interface via padrão Observer conforme §6.2, §6.5, §7.3 (fluxo "Aplicar Dano"). A atualização de PV deve refletir na interface em no máximo 200 ms (RNF02). Teste de desempenho e teste de sistema do histórico da sessão (RU03/RF03, AC 3.2).

**Critérios de aceite:**
- Fluxo de §7.3: processarDano(id, valor) → DAO atualiza → Observer notifica a interface.
- Atualização de PV refletida em ≤ 200 ms do evento de UI até a notificação (RNF02).
- Histórico lista rolagens em ordem cronológica decrescente (AC 3.2).

**Requisitos:** RNF02 · §6.2 · §6.5 · §7.3 · AC 3.2

---

## #6 — Interface JavaFX (Painel de Jogo, atalhos, resolução mínima)

**Tipo:** enhancement · **Prioridade:** média

**Corpo:** Implementar o esqueleto da interface JavaFX conforme §6.6 e RNF04/RNF05: aplicação de entrada, tela FXML do Painel de Jogo e Simulador redimensionável a partir de 1366×768 px, e atalhos de teclado configuráveis para as ações de mesa (rolar ataque, rolar dano, aplicar dano ao selecionado).

**Critérios de aceite:**
- Aplicação JavaFX inicia e carrega o FXML do Painel de Jogo.
- Elementos redimensionáveis sem sobreposição em resolução ≥ 1366×768 (RNF04).
- Atalhos de teclado configuráveis para rolar ataque, rolar dano e aplicar dano (RNF05).

**Requisitos:** RU01 · RNF04 · RNF05 · §6.6

---

## #7 — Telas de seleção e criação de fichas + folha de estilo (visual Tailwind)

**Tipo:** enhancement · **Prioridade:** alta

**Corpo:** Completar o fluxo de RU01 na interface: adicionar a tela de **seleção de fichas** (cards dos personagens persistidos, com estado vazio e atalho para criar) e a tela de **criação de ficha** (formulário com validação dos campos obrigatórios e numéricos, AC 1.3), além da navegação entre Seleção → Criação → Painel de Jogo. Aplicar folha de estilo com visual inspirado no design system Tailwind CSS (paleta slate/indigo/emerald/rose, cards arredondados, sombras e estados de hover) em todas as telas e adicionar `cellFactory` na lista de fichas do painel.

**Critérios de aceite:**
- Tela de seleção lista fichas persistidas como cards; com estado vazio, apresenta orientação para criar.
- Tela de criação valida obrigatórios e numéricos (AC 1.3) e persiste a ficha ao salvar.
- Navegação fluida: Seleção → Criação → Painel de Jogo (com opção de voltar à seleção).
- Visual da interface consistente com o design system Tailwind.
- Lista do painel exibe ficha estruturada (nome, raça/classe, nível, PV, estado) via `cellFactory`.

**Requisitos:** RU01 · AC 1.3 · §6.6

---

## #8 — Regras D&D 5e: atributos, raças/classes (dropdowns), rolagem de atributos e combate

**Tipo:** enhancement · **Prioridade:** alta

**Corpo:** Digitalizar fichas D&D 5e conforme as regras do SRD: catálogos de domínio para os seis `Atributo`, `Raca` (com bônus de habilidade), `ClasseDePersonagem` (dado de vida, proficiências), `Arma` e `Armadura`. Implementar o `RoladorDeAtributos` (rolar 4d6 descartando o menor, array fixo 15/14/13/12/10/8 e compra de pontos de 27 pts) e o `ConstrutorDeFicha` (PV = dado de vida + CON, CA = armadura + Destreza + escudo, bônus de ataque = proficiência + atributo). Atualizar o `SimuladorDeCombate` para as regras de combate 5e (d20 + bônus vs CA, natural 20 crítico, natural 1 falha, dano por arma). Na interface, raça e classe viram listas suspensas e os atributos podem ser rolados/distribuídos.

**Critérios de aceite:**
- Atributos gerados por rolagem (4d6 descartar menor), array fixo ou compra de pontos, conforme as regras 5e.
- Raça e classe selecionáveis por dropdown, com bônus raciais e dado de vida aplicados automaticamente.
- PV, CA, bônus de proficiência e bônus de ataque calculados a partir dos atributos/equipamento.
- Combate usa d20 + bônus vs. CA; natural 20 acerta (crítico) e natural 1 erra.
- Tela de criação exibe resumo da ficha (PV, CA, ataque, proficiência, bônus racial) antes de salvar.

**Requisitos:** RU01 · RU02 · §7.2 · SRD 5e

---

## #9 — Refinamento da interface gráfica (visual Dark Mode D&D)

**Tipo:** enhancement · **Prioridade:** média

**Corpo:** Refinar o visual da interface para um tema dark sofisticado de mesa de D&D sobre o design system Tailwind da issue #7. Substituir o gradiente de fundo por uma composição mais rica, adicionar cabeçalho com logotipo nas telas, padronizar todos os controles (ComboBox com popup estilizado, CheckBox, ScrollBar, campos com estados de foco), enriquecer os cards de seleção e as células da lista com avatar, badges de estado/derrota, tiles de estatísticas (FOR/DES/CON/INT/SAB/CAR) no painel e melhor hierarquia tipográfica.

**Critérios de aceite:**
- Controles nativos (ComboBox, CheckBox, ScrollBar) estilizados e consistentes com o tema.
- Cards de seleção e células da lista com avatar, badges e hierarquia visual clara.
- Bloco de atributos no painel apresentado como tiles de estatísticas.
- Todas as telas com cabeçalho unificado e espaçamento/padding adequado.
- Build e suíte de testes continuam verdes após o refinamento.

**Requisitos:** RNF04 · §6.6
