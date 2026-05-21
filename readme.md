# Especificação e Documentação de Projeto: Gerenciador de Fichas de D&D

Este documento descreve as especificações técnicas, requisitos funcionais e a caracterização do escopo para o desenvolvimento do sistema de criação e gerenciamento de fichas de personagens para o sistema Dungeons & Dragons (D&D).

---

## 1. Visão Geral do Projeto
O objetivo do projeto é desenvolver uma aplicação desktop robusta, performática e intuitiva em **Kotlin** para criar, armazenar e gerenciar fichas de personagens de D&D. A aplicação contará com uma interface gráfica rica (GUI) projetada de forma visual e um fluxo guiado passo a passo para a concepção dos personagens, além de ferramentas de simulação e suporte ao jogo em tempo real (como combate e gerenciamento de inventário/magias).

---

## 2. Stack Tecnológica

* **Linguagem Principal:** Kotlin
* **Interface Gráfica (GUI):** JavaFX (Telas projetadas via Scene Builder / FXML)
* **Banco de Dados:** Arquivo .JSON

---

## 3. Especificações das Funcionalidades (Features)

### 3.1. Autenticação e Gerenciamento de Usuários
* Mecanismo de cadastro e login de usuários para separar as fichas de diferentes jogadores ou mestres no mesmo ambiente local.

### 3.2. Fluxo Guiado de Criação de Fichas
* **Criação Estilo Assistente (Wizard):** Processo passo a passo idêntico aos livros oficiais e plataformas consolidadas de D&D (como o D&D Beyond).
* **Distribuição de Pontos (Point Buy / Standard Array):** Sistema integrado para a compra e alocação de pontos de atributos básicos.
* **Mecânica de Atributos:** Gerenciamento dos valores de atributos considerando bônus, vantagens, desvantagens e modificadores associados.
* **Sistema de Raças e Classes:** Aplicação automática de traços raciais, proficiências, pontos de vida iniciais e habilidades de classe no momento da escolha.

### 3.3. CRUD de Personagens
* **Criar:** Iniciar novas jornadas do zero pelo fluxo guiado.
* **Visualizar/Editar:** Alterar dados da ficha conforme o personagem evolui ou sofre modificações.
* **Deletar:** Remover personagens do banco de dados local.

### 3.4. Gerenciamento de Personagem Ativo (Em Jogo)
* **Status em Tempo Real:** Atualização instantânea de Pontos de Vida (PV/HP), Classe de Armadura (CA), Iniciativa e deslocamento.
* **Gerenciamento de Magias:** Controle de espaços de magia (spell slots) gastos e preparados, além do grimório do personagem.
* **Inventário Dinâmico:** Gerenciamento de itens, armas, armaduras e cálculo automático de capacidade de carga.

### 3.5. Sistemas de Dados e Progressão (Escalonamento)
* **Sistema de Dados Virtual:** Mecanismo interno para rolar dados digitais (d4, d6, d8, d10, d12, d20, d100) com soma automática de modificadores de atributos e perícias.
* **Escalonamento de Nível (Level Up):** Ferramenta automática ou manual para o aumento de nível do personagem, calculando novos pontos de vida, bônus de proficiência e liberação de novas magias/habilidades conforme a progressão da classe.

### 3.6. Utilitários de Gameplay
* **Simulação de Combate:** Painel focado em simular turnos, ordem de iniciativa, aplicação rápida de dano, cura e testes de resistência contra a morte (Death Saves).

---

## 4. Arquitetura de Dados (Conceitual - SQLite)

O banco de dados SQLite local armazenará as seguintes entidades mapeadas:
1. **Users:** ID, username, password_hash.
2. **Characters:** ID, user_id, name, race, class, level, hp_max, hp_current, xp, gold.
3. **Attributes:** Strength, Dexterity, Constitution, Intelligence, Wisdom, Charisma.
4. **Inventory:** Item_id, character_id, item_name, weight, quantity.
5. **Spells:** Spell_id, character_id, spell_name, level, is_prepared.

---

## 5. Próximos Passos para o Desenvolvimento

1. **Modelagem de Telas:** Estruturar os arquivos `.fxml` no Scene Builder separando a tela de Login, o Dashboard principal e o Assistente de Criação de Ficha.
2. **Definição do Esquema (Schema) do SQLite:** Criar os scripts de inicialização de tabelas e conexões em Kotlin.
3. **Implementação do Core Ruleset:** Codificar a lógica abstrata das regras de D&D (cálculo de modificador: `(atributo - 10) / 2`, progressão de tabelas de XP e espaços de magia).
