# O Escudo do Mestre Digital

### Documento de Requisitos e Projeto de Software

**Membros da Equipe de Desenvolvimento:**
Mikael Menezes • Kaio Renato • João Filipe Rosa • Gustavo Afonso • Álvaro Neto • Saulo Ramos

---

## Sumário do Documento

1. Prefácio e Histórico de Versões
2. Introdução e Objetivos do Projeto
   - 2.1 Objetivos do Projeto
   - 2.2 Escopo da v1.0
3. Glossário Técnico Ampliado
4. Requisitos de Usuário e BDD Scenarios
   - 4.1 Histórias de Usuário e Critérios de Aceite
5. Requisitos de Sistema
   - 5.1 Requisitos Funcionais (RF)
   - 5.2 Requisitos Não Funcionais (RNF)
6. Arquitetura do Sistema
   - 6.1 Visão Lógica
   - 6.2 Visão de Processo (Detalhada v1.6)
   - 6.3 Visão de Desenvolvimento
   - 6.4 Visão Física
   - 6.5 Padrões Adotados (MVC, Observer, DDD)
   - 6.6 Diagrama de Componentes
7. Modelos do Sistema — Casos de Uso
   - 7.1.1 Diagrama de Casos de Uso
   - 7.1.2 Especificações de Casos de Uso (UC01-UC06)
   - 7.2 Diagrama de Classes (Modelo Estrutural)
   - 7.3 Diagrama de Sequência (Modelo de Interação)
   - 7.4 Diagrama de Estados do Turno
8. Validação e Planejamento de Testes
   - 8.1 Estratégia de TDD (14 Arquivos)
   - 8.5 Matriz de Rastreabilidade
9. Gerenciamento de Configuração e Evolução
   - 9.1 Histórico de Derivação Git
10. Manuais do Usuário e de Operação
    - 10.2 Atalhos Rápidos de Teclado
11. Conclusão

---

## 1. Prefácio

Este documento serve como a especificação de software consolidado para o projeto **Escudo do Mestre Digital**. Ele visa unificar em um único artefato os diagramas de engenharia de software (UML), a arquitetura do sistema, as especificações completas de casos de uso e o planejamento detalhado de verificação e validação (V&V). O documento foi desenhado para orientar rigorosamente a equipe técnica no desenvolvimento e homologação do software.

### 1.1 Histórico de Versões

| Versão | Data | Descrição da Revisão / Marcos de Entrega |
|---|---|---|
| v1.0 | 22/06/2026 | Definição inicial do escopo, dos requisitos de usuário e de sistema. |
| v1.1 | 05/07/2026 | Revisão arquitetural; migração para Exposed ORM; adoção de modelagem DDD; inclusão de diagramas UML. |
| v1.2 | 25/07/2026 | Inclusão de diagramas de estados; correção de nomenclaturas; quantificação do RNF02 (200 ms); inclusão da classe Historico; histórias de usuário em formato BDD (4.1). |
| v1.4 | 03/08/2026 | Correção de fluxos de casos de uso interrompidos e acréscimos técnicos na Visão de Processo. |
| v1.5 | 03/08/2026 | Integração de todos os diagramas UML gerados em alta definição . |
| v1.6 | 03/08/2026 | Reformatação de todas as especificações de casos de uso (UC01-UC06) em tabelas estruturadas e refinamento geral de leiaute e tipografia. |
| v2.1 | 05/08/2026 | Implementação do sistema de magia 5.5 completo (`EscolaDeMagia`, `SlotsDeMagia`, `CatalogoDeMagias`, `CatalogoHabilidades`, `HabilidadeDeClasse` e `Magia` com todos os campos SRD), compra de pontos fiel ao livro (27 pts, faixa 8–15, tabela de custo 0–9) com botões `−`/`+` na criação, persistência relacional completa de itens/magias e suíte ampliada para 102 testes. Cálculo do modificador de atributo corrigido (arredondamento para baixo). |
| v2.2 | 05/08/2026 | Sistema de combate 5e/2024: enum `Atributo`, bônus de proficiência por nível, CD e bônus de ataque de magia, equipamento completo (Armas `Arma` e `Armadura` SRD com regras de CA e escudo), `MotorDeRegras.testarAtaque` com crítico/falha, dano desarmado, `SimuladorDeCombate.atacar` por CA, descansos, persistência de equipamento/escudo e gasto/preparo de slot no grimório. Catálogo ampliado para 24 magias. Suíte ampliada para 130 testes. |
| v2.3 | 05/08/2026 | Refinamentos de usabilidade na aba de combate e grimório: estado de combate editável (`ComboBox<EstadoCombate>` persistível), seletor de fichas exibindo apenas o nome (não o ID), correção da aplicação de dano (`atualizarUI` forçado + mensagem de PV restante), e seletor de magias com rótulo de nível na `ChoiceDialog`. Removidas todas as referências institucionais (UFPB / Centro de Informática) e o rótulo de versão da tela principal. Suíte: 134 testes verdes. |

---

## 2. Introdução

Mestres de RPG de mesa rotineiramente precisam lidar com uma sobrecarga cognitiva severa, tendo que conciliar dezenas de fichas de personagens, livros de regras complexos, planilhas manuais e cálculos de combate simultâneos. O **Escudo do Mestre Digital** centraliza todas essas responsabilidades em um software local veloz e autônomo, reduzindo drasticamente o tempo necessário para cálculos de dano, testes de atributos e administração de iniciativa.

### 2.1 Objetivos do Projeto

- Disponibilizar um ambiente único para criação, edição e consulta de fichas de personagens, monstros e itens.
- Automatizar o cálculo de rolagens de ataque, dano e demais testes de resolução de ações com regras baseadas no SRD 5e.
- Registrar o histórico de rolagens de maneira auditável durante toda a sessão de jogo.
- Garantir a persistência local e offline dos dados de forma autônoma utilizando SQLite embarcado.
- Utilizar uma arquitetura de pacotes altamente modularizada e desacoplada baseada no modelo MVC e DDD.

### 2.2 Escopo

A v1.0 foca primariamente na experiência local single-player para o Mestre. Estão fora de escopo funcionalidades multiplayer em rede, sincronização em nuvem e servidores remotos. Toda a estrutura de banco de dados e motor lógico foi desenhada para viabilizar futuras expansões em rede de maneira simples, sem a necessidade de refatorar o domínio.

---

## 3. Glossário Técnico Ampliado

A tabela abaixo define os termos técnicos, frameworks, tecnologias e padrões adotados na engenharia deste projeto para assegurar o completo alinhamento de terminologias entre desenvolvedores e parceiros.

| Termo | Definição Técnica do Projeto |
|---|---|
| PRNG | Pseudo-Random Number Generator (Gerador de Números Pseudoaleatórios). Algoritmo matemático para simular de forma confiável rolagens de dados virtuais na aplicação local. |
| CA | Classe de Armadura: Atributo estático que define a dificuldade numérica necessária para que um ataque atinja fisicamente um personagem ou entidade. |
| PV | Pontos de Vida: Atributo dinâmico representativo da vitalidade física atual e máxima de uma entidade (personagens ou monstros). |
| Exposed ORM | Framework oficial de Mapeamento Objeto-Relacional escrito em Kotlin, responsável por traduzir operações no modelo de classes em comandos SQL de forma segura. |
| SQLite | Mecanismo de banco de dados relacional embarcado autônomo, rápido e local. Armazena todas as tabelas em um único arquivo (.db) na pasta raiz do projeto. |
| DDD | Domain-Driven Design (Design Orientado ao Domínio): Abordagem arquitetural que modela o software em torno das regras do negócio RPG, isolando-o de dependências técnicas. |
| MVC | Model-View-Controller: Padrão arquitetural que isola a visualização JavaFX (View), as regras do jogo (Model) e a coordenação de ações e botões (Controller). |
| BDD | Behavior-Driven Design (Design Orientado ao Comportamento): Metodologia de escrita de cenários em linguagem natural (Dado/Quando/Então) usada para mapear as histórias de usuário. |
| TDD | Test-Driven Development (Desenvolvimento Orientado a Testes): Metodologia que preconiza a escrita de testes unitários automatizados antes do código de produção. |
| JavaFX / FXML | Framework gráfico utilizado para a construção e renderização da interface desktop enriquecida, com arquivos FXML separando a estrutura visual da lógica de comportamento. |
| Gradle | Mecanismo de build e gerenciamento automatizado de dependências para o ecossistema Java/Kotlin, utilizado via build.gradle.kts. |
| Observer | Padrão de projeto comportamental de notificação reativa. Permite que as mudanças de Pontos de Vida no domínio reflitam na interface sem acoplamento rígido. |
| Thread de UI | Linha de processamento dedicada exclusivamente à renderização gráfica da interface JavaFX, que nunca deve sofrer bloqueios por transações de banco ou processamentos longos. |

---

## 4. Requisitos de Usuário

Os requisitos de usuário são abstratos, focados nas funcionalidades do ponto de vista prático do Mestre e de seus jogadores, evitando termos técnicos de implementação complexos.

| Código | Descrição Amigável do Requisito de Usuário |
|---|---|
| RU01 | O sistema deve possibilitar a criação, visualização e edição detalhada de fichas de personagens, monstros e itens de campanha. |
| RU02 | O sistema deve calcular de maneira automática as rolagens matemáticas de ataque, dano e jogadas de proteção durante os combates. |
| RU03 | O sistema deve expor um registro histórico detalhado de todas as rolagens realizadas durante a sessão para consulta e auditoria. |

### 4.1 Histórias de Usuário e Critérios de Aceite (BDD)

#### História de Usuário 1 — Gerenciar Fichas (RU01)

*Como Mestre de RPG, eu quero criar, editar e visualizar fichas de personagens, monstros e itens, para que eu possa preparar e conduzir minhas sessões sem depender de planilhas externas.*

**Critério de Aceite 1.1 — Criação de ficha válida:**
Dado que o Mestre está na tela "Gerenciar Fichas" e preenche nome, raça, classe e atributos obrigatórios;
Quando o Mestre confirma a criação;
Então o sistema persiste a ficha via Exposed ORM e a exibe imediatamente na lista de personagens, com ID gerado automaticamente.

**Critério de Aceite 1.2 — Edição de ficha existente:**
Dado que existe uma ficha de personagem previamente salva;
Quando o Mestre altera um atributo (ex.: pontosDeVidaMaximo) e salva;
Então o sistema atualiza o registro correspondente no banco local SQLite e reflete a alteração em qualquer painel aberto.

**Critério de Aceite 1.3 — Validação de dados obrigatórios:**
Dado que o Mestre tenta criar uma ficha sem preencher o campo nome obrigatório;
Quando o Mestre confirma a criação;
Então o sistema rejeita a operação, exibe mensagem de campo obrigatório ausente e não grava nenhum registro incompleto.

**Critério de Aceite 1.4 — Associação de Itens e Magias (v1.6):**
Dado que o Mestre está na tela de gerenciamento de ficha com um personagem válido carregado;
Quando o Mestre associa um item ao inventário ou uma magia ao grimório do herói;
Então o sistema grava a associação relacional no banco de dados SQLite local por meio de uma chave estrangeira de relacionamento no Exposed ORM e atualiza os painéis correspondentes na interface JavaFX de forma sincronizada.

#### História de Usuário 2 — Rolagens de Ataque e Dano (RU02)

*Como Mestre de RPG, eu quero que o sistema calcule automaticamente rolagens de ataque e dano, para que eu possa manter o ritmo do combate sem interromper a narrativa com cálculos manuais.*

**Critério de Aceite 2.1 — Rolagem simples:**
Dado que um personagem possui um modificador de ataque definido;
Quando o Mestre aciona "Rolar Ataque" para esse personagem;
Então o sistema invoca DadoVirtual.rolar(), soma o modificador cadastrado e exibe o resultado final na interface.

**Critério de Aceite 2.2 — Vantagem e desvantagem:**
Dado que uma rolagem é marcada com vantagem;
Quando o Mestre executa a rolagem;
Então o motor de regras executa duas rolagens do PRNG e retorna o maior valor entre elas (e o menor valor se estiver marcada como desvantagem).

**Critério de Aceite 2.3 — Aplicação de dano em tempo real:**
Dado que um personagem está com pontosDeVidaAtual acima de zero;
Quando o Mestre aplica um valor de dano resultante da rolagem;
Então o sistema atualiza o pontosDeVidaAtual, persiste a alteração no banco e notifica a interface via padrão Observer de forma assíncrona dentro de 200 ms.

#### História de Usuário 3 — Histórico de Rolagens (RU03)

*Como Mestre de RPG, eu quero consultar um histórico das últimas rolagens da sessão, para que eu possa auditar decisões de mesa e resolver disputas sobre resultados.*

**Critério de Aceite 3.1 — Registro automático de rolagem:**
Dado que uma rolagem de ataque, dano ou magia é executada no simulador;
Quando o resultado é calculado pelo motor de regras;
Então o sistema cria automaticamente um registro em Historico contendo personagemId, tipoRolagem, resultado e timestamp, de forma automática e silenciosa.

**Critério de Aceite 3.2 — Consulta do histórico da sessão:**
Dado que existem registros de Historico gravados na sessão corrente;
Quando o Mestre abre o painel lateral de histórico;
Então o sistema lista as rolagens em ordem cronológica decrescente, exibindo identificador do personagem, tipo de rolagem e resultado.

---

## 5. Requisitos de Sistema

### 5.1 Requisitos Funcionais (RF)

| Código | Requisito Funcional | Detalhamento do Comportamento |
|---|---|---|
| RF01 | Interface de Repositório CRUD | Fornecer repositórios para realizar operações CRUD nas tabelas de Ficha, Item e Magia utilizando o Exposed ORM. |
| RF02 | Motor de Rolagens e Regras | Processar modificadores de atributos e aplicar lógicas de vantagem/desvantagem sobre o PRNG (maior ou menor entre duas rolagens). |
| RF03 | Registro Automático de Histórico | Registrar de forma automática na entidade Historico cada evento de rolagem executado (ataque, dano ou magia), gravando personagemId, tipoRolagem, resultado e timestamp. |

### 5.2 Requisitos Não Funcionais (RNF)

| Código | Categoria | Descrição da Restrição / Métrica |
|---|---|---|
| RNF01 | Processo | Desenvolvido em Kotlin utilizando Gradle (build.gradle.kts) para compilação e empacotamento. |
| RNF02 | Desempenho | Atualização de Pontos de Vida (PV) refletida na tela em no máximo 200 ms, medidos do evento de UI até a notificação via padrão Observer no hardware de referência (SSD, 8 GB RAM). |
| RNF03 | Físico | Os dados devem ser armazenados de maneira 100% local utilizando banco de dados embarcado SQLite. |
| RNF04 | Usabilidade | A interface JavaFX deve rodar em resoluções mínimas a partir de 1366x768 px com painéis redimensionáveis e sem sobreposição. |
| RNF05 | Interface | Atalhos de teclado (F5 para ataque, F6 para dano e F7 para aplicar dano) configuráveis para reduzir dependência do mouse. |

---

## 6. Arquitetura do Sistema

A especificação arquitetural é baseada no consagrado modelo de Visões 4+1, assegurando um software extremamente robusto, desacoplado e modular.

### 6.1 Visão Lógica

O domínio do jogo é estruturado seguindo práticas de Domain-Driven Design (DDD). As entidades básicas do RPG, como Personagem, Item e Magia, residem de maneira independente, enquanto o DadoVirtual atua como serviço de domínio gerando valores pseudoaleatórios. A nova entidade Historico complementa a visão gravando os logs de mesa.

### 6.2 Visão de Processo (Detalhada v1.6)

Para atender estritamente ao requisito RNF02 (tempo máximo de atualização visual de 200 ms), a concorrência do software foi minuciosamente isolada. A interface gráfica opera em uma *UI Thread* exclusiva do JavaFX, que nunca é bloqueada por atividades do banco de dados. Quando uma ação ocorre (por exemplo, reduzir HP via F7), o Motor de Regras processa as regras de combate em tempo real na CPU e agenda a persistência da tabela via Exposed ORM em uma thread de background. Assim que a persistência é concluída no SQLite, a interface é notificada instantaneamente através do padrão Observer, concluindo todo o processamento de exibição gráfica em menos de 50 ms no hardware de testes.

### 6.3 Visão de Desenvolvimento

A estrutura física de pacotes no Gradle divide-se claramente em três módulos separados de responsabilidade:

- **Presentation:** Responsável pelas janelas, controladores JavaFX, arquivos declarativos FXML e estilos visuais em CSS.
- **Domain:** Kotlin puro contendo as entidades, agregados e regras de combate fundamentais de RPG.
- **Infrastructure:** Responsável pela conexão física ao arquivo escudo.db do SQLite e mapeamentos do ORM.

### 6.4 Visão Física

O software é empacotado como um executável offline único (fat-JAR), dispensando totalmente conexões de rede ou infraestruturas externas de computação em nuvem, minimizando latências de conexões corporativas.

### 6.5 Padrões Adotados

O padrão **Model-View-Controller (MVC)** governa a distribuição básica de código. O padrão **Observer** rege as notificações de alteração de dados de forma desacoplada, enquanto os padrões táticos do **DDD** blindam o domínio principal do sistema contra detalhes de frameworks externos.

### 6.6 Diagrama de Componentes

> **Figura 1: Diagrama de Componentes em Camadas Isoladas.**
>
> - **A Interface (JavaFX)**: Telas FXML e Scene Builder [Componente UI] → *Eventos* → Painel de Jogo e Simulador [Controller]
> - → *Envia Comandos* →
> - **O Motor (Kotlin)**: Regras de Negócio [Domain Services] → *Usa matemática* → Dado Virtual PRNG [Value Object]; → *Aciona gatilhos* → Motor de Escalonamento [Domain Services]
> - → *Persiste e Consulta* →
> - **O Repositório (Dados)**: Exposed ORM [Data Mapper] → *Linguagem SQL* → Banco de Dados Relacional [SQLite File]

---

## 7. Modelos do Sistema

A modelagem técnica é ilustrada por diagramas UML estruturais, comportamentais e de interação.

### 7.1.1 Diagrama de Casos de Uso

> **Figura 2: Diagrama de Casos de Uso do Escudo do Mestre.**
> Ator: Mestre / Jogador, interagindo com:
> - UC01 - Autenticar no Ambiente Local
> - UC02 - Criar Novo Herói
> - UC03 - Gerenciar Fichas
> - UC04 - Atualizar PV, CA e Inventário (Painel)
> - UC05 - Simular Combate e Iniciativa
> - UC06 - Acionar Gatilho de Nível

### 7.1.2 Especificações Detalhadas de Casos de Uso (Tabelas)

#### UC01 – Autenticar no Ambiente Local

| Campo | Descrição |
|---|---|
| **Ator Principal** | Mestre / Jogador |
| **Resumo** | Permite o acesso inicial ao sistema garantindo que o usuário se conecte ao banco de dados SQLite local adequado. |
| **Pré-condições** | Arquivo de banco de dados SQLite acessível no diretório local do sistema. |
| **Fluxo Principal** | 1. O usuário abre o aplicativo. 2. O sistema solicita a confirmação do ambiente/perfil do usuário local. 3. O usuário confirma o acesso ao seu perfil. 4. O sistema estabelece conexão com o SQLite e renderiza o menu inicial. |
| **Fluxos Alternativos** | 3a. Falha de conexão: Se o arquivo do banco de dados local não for localizado, o sistema exibe um alerta gráfico de erro e oferece ao usuário a opção de iniciar um banco de dados totalmente novo do zero. |
| **Pós-condições** | O usuário ganha acesso irrestrito às ferramentas e painéis de jogo. |

#### UC02 – Criar Novo Herói

| Campo | Descrição |
|---|---|
| **Ator Principal** | Mestre / Jogador |
| **Resumo** | Cadastra uma nova ficha de personagem de RPG no banco de dados local para uso imediato em campanhas. |
| **Pré-condições** | Usuário autenticado no ambiente inicial do sistema (UC01 concluído). |
| **Fluxo Principal** | 1. O usuário seleciona a opção 'Criar Novo Herói' no menu inicial. 2. O sistema carrega o painel com o formulário de cadastro em branco. 3. O usuário insere as informações obrigatórias (Nome, Raça, Classe, Atributos de Jogo). 4. O usuário confirma a gravação clicando em 'Salvar'. 5. O sistema valida os campos e grava a nova ficha via Exposed ORM. 6. O sistema atualiza a tela exibindo a nova ficha cadastrada com seu ID sequencial. |
| **Fluxos Alternativos** | 3a. Validação de dados obrigatórios: Se o campo de Nome for submetido em branco, o sistema aborta a operação de persistência, exibe um alerta gráfico em vermelho de validação de campo e mantém o formulário aberto. |
| **Pós-condições** | A ficha do novo herói está salva no banco de dados local SQLite e disponível para uso imediato. |

#### UC03 – Gerenciar Fichas

| Campo | Descrição |
|---|---|
| **Ator Principal** | Mestre / Jogador |
| **Resumo** | Permite a listagem visual, edição e consulta rápida do acervo de fichas de personagens de RPG. |
| **Pré-condições** | Existência de pelo menos uma ficha previamente cadastrada no banco de dados local. |
| **Fluxo Principal** | 1. O usuário acessa a opção 'Gerenciar Fichas' no menu. 2. O sistema exibe em cards visuais todas as fichas ativas carregadas do banco local. 3. O usuário seleciona uma ficha e altera atributos específicos (ex: pontosDeVidaMaximo). 4. O usuário aciona a opção de salvar. 5. O sistema atualiza os dados na tabela correspondente via Exposed ORM. 6. O sistema notifica a UI para recarregar quaisquer cards visuais ou visualizadores abertos. |
| **Fluxos Alternativos** | Não se aplica nesta especificação local (erros de banco são tratados de forma genérica pelo driver JDBC). |
| **Pós-condições** | As modificações da ficha estão salvas e consistentes em todos os painéis do sistema. |

#### UC04 – Atualizar PV, CA e Inventário

| Campo | Descrição |
|---|---|
| **Ator Principal** | Mestre / Jogador |
| **Resumo** | Permite a rápida alteração de atributos dinâmicos e voláteis, como Pontos de Vida (PV), Classe de Armadura (CA) e uso de itens do inventário no fluxo do combate. |
| **Pré-condições** | O usuário deve estar com o 'Painel de Jogo e Simulador' aberto com uma entidade carregada. |
| **Fluxo Principal** | 1. O usuário clica para alterar o valor de HP do personagem (ex: aplicar dano ou curar). 2. O Simulador de Combate calcula o valor restante e aciona o repositório PersonagemDAO. 3. O repositório executa a transação SQL no banco e confirma a persistência dos novos pontos de vida. 4. O padrão Observer detecta a mudança de estado e propaga a notificação para os controladores de interface gráfica. 5. O painel redesenha a barra de vida exibindo os novos valores de forma suave em no máximo 200 ms. 6. O sistema reflete a alteração em qualquer painel aberto que exiba o personagem. |
| **Fluxos Alternativos** | PV <= 0: Caso a redução de PV resulte em valor menor ou igual a zero, o Motor de Regras força a transição do estado do combatente para 'Derrotado', alterando sua cor no painel visual. |
| **Pós-condições** | A interface gráfica reflete com absoluta precisão o novo estado e HP do personagem, com os dados persistidos de forma segura no SQLite. |

#### UC05 – Simular Combate e Iniciativa (Fluxo Corrigido v1.6)

| Campo | Descrição |
|---|---|
| **Ator Principal** | Mestre / Jogador |
| **Resumo** | Automatiza as rolagens matemáticas e a organização cronológica dos turnos de combate, reduzindo drasticamente a carga cognitiva do Mestre. |
| **Pré-condições** | Os personagens e monstros ativos no simulador de combate devem estar no estado inicial 'Aguardando Iniciativa'. |
| **Fluxo Principal** | 1. O usuário clica nos botões 'Rolar Ataque', 'Rolar Dano' ou aciona a rolagem de iniciativa. 2. O Motor de Combate invoca o serviço de domínio DadoVirtual.rolar(), somando os modificadores de atributos do atacante. 3. O sistema exibe o resultado numérico final de forma destacada no painel de combate. 4. [PASSO RESTAURADO] O sistema cria de maneira automática um novo registro de auditoria na entidade Historico contendo o ID do personagem correspondente, o tipo de rolagem efetuada, o resultado numérico final obtido e o carimbo de data/hora (timestamp) preciso do evento. 5. O Motor de Escalonamento de Combate organiza os combatentes em ordem cronológica de turnos com base no valor de iniciativa rolado, marcando a entidade ativa com o estado 'Em Turno'. |
| **Fluxos Alternativos** | Rolagem com Vantagem/Desvantagem: Se a rolagem estiver marcada com o modificador de vantagem, o sistema executa duas rolagens completas no gerador pseudoaleatório (PRNG) e adota o maior valor (ou adota o menor valor no caso de desvantagem). |
| **Pós-condições** | O resultado do combate é processado, as ações são devidamente registradas no histórico e a ordem dos turnos é estabelecida. |

#### UC06 – Acionar Gatilho de Nível (Fluxo Corrigido v1.6)

| Campo | Descrição |
|---|---|
| **Ator Principal** | Mestre / Jogador |
| **Resumo** | Executa de forma automática e segura a transição de nível do personagem quando acumulada experiência suficiente. |
| **Pré-condições** | Personagem selecionado na interface com pontos de experiência acumulados suficientes para avançar de nível. |
| **Fluxo Principal** | 1. O usuário aciona a opção de subir nível ('Level Up') na interface gráfica do formulário da ficha. 2. [PASSO RESTAURADO] O Motor de Regras calcula os novos atributos básicos e bônus do personagem de acordo com as regras estruturadas para sua classe e raça específicas. 3. [PASSO RESTAURADO] O sistema recalcula os atributos dependentes do nível, atualizando os pontos de vida máximos (HP máximo com base no dado de vida da classe - Hit Dice) e liberando novos espaços de magia (slots de magia) no grimório. 4. As alterações do novo nível do herói são gravadas e persistidas definitivamente no banco de dados local via Exposed ORM. 5. O sistema atualiza o painel principal notificando o usuário que a transição de nível ocorreu com absoluto sucesso. |
| **Fluxos Alternativos** | Não se aplica nesta especificação local. |
| **Pós-condições** | O personagem está devidamente atualizado para o novo nível com seus respectivos atributos e bônus calculados e salvos no SQLite. |

### 7.2 Diagrama de Classes (Modelo Estrutural)

O diagrama abaixo ilustra o modelo estrutural de dados e suas respectivas chaves. A classe principal Personagem serve como raiz de agregação, contendo relacionamento direto de um-para-muitos com Item (inventário) e Magia (grimório). A nova entidade Historico registra todas as rolagens efetuadas pelo DadoVirtual de maneira persistente e linear.

> **Figura 3: Diagrama de Classes do Domínio de RPG.**
>
> **Personagem**: id: String, nome: String, raca: String, classe: String, nivel: Int, pontosDeVidaAtual: Int, pontosDeVidaMaximo: Int, classeArmadura: Int
> + aplicarDano(valor: Int), + curar(valor: Int), + subirNivel()
>
> - possui no Inventário (1 → *) → **Item**: id, nome, peso, quantidade
> - possui no Grimório (1 → *) → **Magia**: id, nome, nivel, escola: EscolaDeMagia, tempoConjuracao, alcance, componentes, duracao, requerConcentracao, descricao, preparada, slotGasto — conjurada via → **SlotsDeMagia**: + slots(classe, nivel): Map<Int, Int>, + circulos(classe, nivel), + atributoDeConjuracao(classe)
> - consulta → **CatalogoDeMagias**: + magiasDaClasse(classe) e **CatalogoHabilidades**: + habilidadesDaClasse(classe), + habilidadesAteNivel(classe, nivel) → **HabilidadeDeClasse**: nome, nivel, descricao
> - gera (1 → *) → **Historico (novo)**: id, personagemId, tipoRolagem, resultado, timestamp — + registrar(evento: RolagemEvento)
> - usa → **DadoVirtual**: + rolar(faces: Int, modificador: Int): Int — Historico "registra resultado de" DadoVirtual

### 7.3 Diagrama de Sequência (Modelo de Interação)

O diagrama exibe as trocas de mensagens síncronas e assíncronas do caso de uso de aplicação de dano (UC04), detalhando como o padrão Observer reage e atualiza a interface gráfica em menos de 200 ms.

> **Figura 4: Diagrama de Sequência para Aplicação de Dano.**
>
> Usuário → Interface Gráfica: clica para aplicar dano → Simulador de Combate: processarDano(id, valor) → PersonagemDAO (ORM): atualizarPontosDeVida(id, valor) → Banco de Dados: UPDATE personagens SET PV = ... → (transação concluída) → PersonagemDAO notifica Simulador ("Estado Atualizado") → Interface Gráfica: notificarMudancaEstado() [Observer] → lerNovosDados() → retorno dos dados → Usuário: "Tela atualizada em < 200ms"

### 7.4 Diagrama de Estados

O diagrama ilustra o ciclo de vida e a transição de estados dos combatentes geridos pelo motor lógico, mostrando como o fluxo avança conforme o HP de uma entidade é modificado.

> **Figura 5: Diagrama de Transição de Estados de Combate.**
>
> (início) → Aguardando Iniciativa → [Ordem de iniciativa atingida] → Em Turno → [Ação encerrada / passar turno] → Aguardando Iniciativa
> Em Turno → [Efeito de status aplicado] → Aturdido / Incapacitado → [Efeito expira] → Aguardando Iniciativa
> Aguardando Iniciativa / Em Turno / Aturdido-Incapacitado → [PV <= 0] → Derrotado (fim)

---

## 8. Validação e Planejamento de Testes

Para assegurar o correto funcionamento lógico e o absoluto cumprimento de todos os requisitos funcionais e não funcionais especificados, o projeto adota uma rigorosa metodologia de cobertura e validação baseada em testes automatizados.

### 8.1 Desenvolvimento Dirigido a Testes (TDD)

Seguindo as práticas de TDD, os testes automatizados foram construídos de forma concomitante com o código-fonte principal, tendo os cenários estruturados de BDD (Seção 4.1) como base de asserções diretas.

| Arquivo de Teste | Módulo Avaliado | Qtd | Escopo e Validação Principal |
|---|---|---|---|
| DadoVirtualTest.kt | domain/service | 4 | Verifica a distribuição probabilística do PRNG de dados comuns, valida intervalo [1..faces] e lança exceções para faces <= 0. |
| MotorDeRegrasTest.kt | domain/service | 6 | Valida a lógica matemática de Vantagem (maior entre duas jogadas d20) e Desvantagem (menor), aplicando modificadores e o arredondamento correto para atributos baixos. |
| RoladorDeAtributosTest.kt | domain/service | 12 | Valida o algoritmo '4d6 drop lowest', compra de pontos (27 pontos, faixa 8–15, tabela de custo 0–9), pontos restantes e limites dos steppers +/−. |
| ConstrutorDeFichaTest.kt | domain/service | 4 | Aplica modificadores de raça (incluindo meio-elfo +2 CAR), hit dice, bônus de ataque, Classe de Armadura e construção de ficha. |
| PersonagemTest.kt | domain/model | 6 | Cobre aplicarDano(), curar() e subirNivel(), garantindo as transições de estado de combate e a notificação Observer. |
| HistoricoTest.kt | domain/model | 2 | Assegura que as rolagens sejam criadas automaticamente e ordenadas de forma cronológica decrescente na sessão. |
| ItemMagiaTest.kt | domain/model | 3 | Garante adições/remoções consistentes de itens no inventário e de magias do catálogo no grimório. |
| Magia5eTest.kt | domain/model | 8 | Valida o modelo completo de magia 5.5 (escola, tempo, alcance, componentes, duração, concentração, descrição), truques e cópia para o grimório. |
| SlotsDeMagiaTest.kt | domain/service | 16 | Verifica os espaços de magia por círculo de conjuradores plenos, de metade (Paladino/Patrulheiro) e de pacto (Bruxo), além do atributo de conjuração por classe. |
| CatalogoDeMagiasTest.kt | domain/service | 7 | Garante que o catálogo SRD retorne magias por classe, busque por nome/id e mantenha todos os campos válidos. |
| CatalogoHabilidadesTest.kt | domain/service | 8 | Garante que as habilidades de classe sejam recuperadas, ordenadas e filtradas pelo nível de desbloqueio. |
| MotorDeEscalonamentoTest.kt | domain/service | 7 | Valida a ordenação de iniciativa e o avanço de turnos, preservando estados Derrotado/Atordoado e o retorno ao início da rodada. |
| SimuladorDeCombateTest.kt | application | 9 | Simula rolagens de ataque/dano com vantagem e desvantagem, aplicação de dano (incluindo PV <= 0), registros do histórico e timestamps sequenciais. |

| RegrasDeAtributoTest.kt | domain/model | 5 | Valida o modificador de atributo (arredondamento), o bônus de proficiência por nível, a CD de resistência e o bônus de ataque de magia. |
| EquipamentoTest.kt | domain/model | 7 | Armas SRD (atributo de combate Força/Destreza, dano) e CA de armadura (leve/média/grossa) e escudo (+2). |
| CombatePersonagemTest.kt | domain/model | 11 | Equipamento no personagem: bônus de proficiência, modificador de ataque, dados de dano, CA efetiva, descanso e aplicação do ataque/crítico. |
| TestarAtaqueTest.kt | domain/service | 6 | Crítico (natural 20), falha (natural 1), d20+bônus contra CA, dano dobrado no crítico e dano desarmado (1 + Força). |

| RepositorioIntegracaoTest.kt | infrastructure | 13 | Testes de persistência em SQLite: CRUD de personagem, item, magia completa (escola/tempo/duração), histórico decrescente e exclusões relacionadas. |

**Total: 134 testes automatizados, todos verdes no `./gradlew clean build`.**

### 8.5 Matriz de Rastreabilidade

A matriz de rastreabilidade a seguir valida como cada requisito especificado (de usuário ou de sistema) é efetivamente validado pelas rotinas e testes automatizados.

| Requisito Mapeado | Estratégia de Verificação e Cobertura de Testes |
|---|---|
| RU01 / RF01 (Gerenciar Fichas) | Validação de operações de escrita, leitura e atualização de tabelas de fichas no teste RepositorioIntegracaoTest e ConstrutorDeFichaTest. |
| RU02 / RF02 (Motor de Combate) | Cenários de rolagens, vantagens, desvantagens e cálculos matemáticos validados em DadoVirtualTest e MotorDeRegrasTest. |
| RU03 / RF03 (Histórico) | Testes de inserção decrescente de auditoria cronológica cobertos em HistoricoTest e SimuladorDeCombateTest. |
| RNF02 (Desempenho PV) | Teste de medição temporal em milissegundos no SimuladorDeCombateTest, atestando tempos inferiores a 200 ms (média local de 45 ms). |
| RNF03 (Armazenamento SQLite) | Validação do banco físico em RepositorioIntegracaoTest, executando transações e salvando dados locais no arquivo escudo.db. |
| RNF04 / RNF05 (Atalhos e Resolução) | Inspeção visual manual da responsividade JavaFX e validação dos Accelerators dos atalhos de teclado F5/F6/F7. |

---

## 9. Gerenciamento de Configuração e Evolução

As solicitações de mudança e evoluções de escopo do projeto de software foram devidamente auditadas e autorizadas pelo comitê técnico por meio de formulários formais de Solicitação de Mudança (Change Request Form - CRF).

| Cod. CRF | Título de Solicitação | Estimativa de Impacto | Decisão do Comitê |
|---|---|---|---|
| CRF-001 | Inclusão de histórico de rolagens em sessão (RU03/RF03) | Modelagem da tabela Historico e DAO associados (8 horas) | Aprovado (v1.1) |
| CRF-002 | Migração da camada de banco para o Exposed ORM | Refatoração de toda a camada de repositórios (12 horas) | Aprovado (v1.1) |
| CRF-003 | Adição de atalhos rápidos de teclado (F5/F6/F7) | Configuração de Accelerators nos menus do JavaFX (4 horas) | Aprovado (v1.2) |
| CRF-004 | Ajuste na regra racial fixa do Meio-Elfo (+2 CAR) | Alteração do enum estático de raça Raca.kt (2 horas) | Aprovado (v1.2) |

### 9.1 Histórico de Derivação

A rastreabilidade de código-fonte foi mantida sob estrito controle de versionamento no repositório Git local do projeto.

| Tag / Release | Módulos de Código Modificados | Resumo das Alterações Efetuadas |
|---|---|---|
| v1.0 | build.gradle.kts, domain/* | Setup inicial do Gradle, estrutura do diretório de pacotes e classes de domínio Personagem, Item e Magia. |
| v1.1 | infrastructure/persistence/*, db/* | Mapeamento Exposed ORM, criação de tabelas relacionais, SQLite DatabaseFactory e PersonagemDAO. |
| v1.2 | presentation/*, test/* | Telas XML, folhas de estilo CSS, mapeamento de atalhos rápidos F5/F6/F7 e 74 testes passando. |
| v1.6 | reports/*, documentation/* | Atualização completa da documentação de requisitos, com especificações de casos de uso estruturadas em tabelas. |
| v2.0 | domain/model, infrastructure/*, presentation/*, test/* | Sistema de magia 5.5 (EscolaDeMagia, CatálogoDeMagias, SlotsDeMagia), habilidades de classe (CatálogoHabilidades), botão Subir Nível (UC06) e correção de rolagem da ficha. Nota: as abas do Painel de Jogo (Ficha/Habilidades/Grimório/Inventário/Histórico) e a tela de seleção em estilo Netflix permanecem como evolução pretendida, não presentes no código atual. |
| v2.1 | domain/service, domain/model, presentation/controller, infrastructure/persistence, test/* | Detalhamento completo da Magia 5.5 (todos os campos SRD) com persistência relacional, compra de pontos fiel ao livro com steppers `−`/`+`, catálogo de habilidades para as 12 classes e suíte ampliada para 102 testes. |

### 9.2 Documentação de Release

O pacote de lançamento oficial da aplicação compreende:

1. **Arquivo Executável JAR:** `build/libs/escudo-mestre-digital-1.0.0.jar`
2. **Banco de Dados Embarcado:** Arquivo `escudo.db` gerado de maneira automática na raiz na primeira execução.
3. **Instruções de Inicialização:** Comando via terminal: `./gradlew run` ou `java -jar escudo-mestre-digital-1.0.0.jar`.

---

## 10. Manuais do Usuário e de Operação

### 10.1 Telas e Interfaces do Usuário

> **Nota de implementação (v2.1):** a interface atual é composta por janelas separadas e mais simples do que
> o fluxo descrito abaixo. O `menu_inicial` oferece três ações — **Criar Novo Herói**, **Gerenciar Fichas** e
> **Painel de Combate e Simulador**. A criação usa um formulário com geração de atributos (rolagem, matriz fixa
> e **compra de pontos** com steppers `−`/`+`), e o Painel de Combate é um painel único (sem abas) com ações
> de mesa, histórico e atalhos F5/F6/F7. O visual em estilo **Netflix**, a tela de seleção por cards e o Painel
> com abas (Ficha/Habilidades/Grimório/Inventário/Histórico) são descritos abaixo como **evolução pretendida**.

A tela inicial da aplicação, em estilo Netflix, apresenta um mural de cards "poster" visuais para cada ficha cadastrada, exibindo nome, raça, classe, nível atual e barra dinâmica de pontos de vida. Ao clicar em '+ Nova Ficha', o Mestre abre o formulário de criação dividido em dois cards (Identidade/Atributos e Equipamento/Resumo), todos padronizados em grids de 2 colunas com labels de largura fixa e controles responsivos, além de um resumo ao vivo do HP máximo, CA, bônus de ataque e proficiência antes de confirmar a gravação.

Depois de selecionar uma ficha, o **Painel de Jogo** abre em um conjunto de abas:

| Aba | Funcionalidade |
|---|---|
| Ficha | Dados de identidade, PV/CA, grade de atributos, ações de mesa (Atacar, Rolar Dano, Aplicar Dano) e botão **Subir Nível** (UC06) que aumenta nível, PV máximo/atual e atualiza habilidades/slots. |
| Habilidades | Lista as habilidades de classe a partir do nível atual e o resumo de conjuracão da classe. |
| Grimório | Slots de magia por círculo, combo adicionar magia e lista de magias conhecidas (preparar/gastar slot/remover). |
| Inventário | Cadastro de itens (nome/peso/quantidade) com peso total carregado. |
| Histórico | Registros de rolagens de ataque, dano e magia da sessão (RU03), ordenados cronologicamente e atualizados automaticamente. |

**Métodos de Geração de Atributos**

| Método | Descrição de Regra de Geração |
|---|---|
| Rolagem de Dados (Default) | Rola 4d6 de forma automática descartando o menor valor individual obtido para a composição de cada um dos 6 atributos básicos. |
| Matriz de Atributos (Array Fixo) | Fornece a distribuição padrão de valores oficiais de RPG para alocação direta: 15, 14, 13, 12, 10, 8. |
| Compra de Pontos (Points Buy) | Distribui 27 pontos pelo custo progressivo do SRD (atributo parte de 8 = 0 pts até 15 = 9 pts). No formulário, cada atributo ganha botões `−`/`+` que ajustam o valor respeitando a faixa 8–15 e o saldo restante, exibido em tempo real no selo "Pontos restantes" (torna-se vermelho caso o orçamento seja excedido). |

### 10.2 Atalhos Rápidos de Teclado (Mapeamento RNF05)

Durante a condução das sessões de jogo, o Mestre de RPG pode utilizar atalhos rápidos pré-configurados do sistema para maximizar a agilidade e evitar o uso excessivo do mouse no painel de combate.

| Tecla | Ação de Combate | Descrição Técnica do Comportamento |
|---|---|---|
| F5 | Rolar Ataque | Executa a rolagem do d20 somada ao bônus de ataque cadastrado da arma equipada contra a CA declarada do alvo, sinalizando acertos e falhas críticas. |
| F6 | Rolar Dano | Dispara as rolagens do dado de dano específico da arma do herói ativo, aplicando o modificador de atributo e exibindo o resultado. |
| F7 | Aplicar Dano | Aplica o valor de dano resultante na ficha selecionada, reduzindo os pontos de vida no banco SQLite e redesenhando a barra de vida em menos de 200 ms. |

---

## 11. Conclusão

O presente documento consolidou, em um único artefato rigoroso e unificado, o projeto completo de engenharia de software para o **Escudo do Mestre Digital**. A adoção do padrão MVC sob modelagem de DDD, a persistência ágil com SQLite embarcado e a reatividade promovida pelo padrão Observer fornecem uma base de software extremamente sólida, modularizada e performática. Com todas as especificações de casos de uso (UC01-UC06) estruturadas em tabelas formais na v1.6, a equipe de engenharia dispõe de um guia de implementação definitivo e livre de ambiguidades.
