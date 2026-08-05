# Apresentação do Projeto — Engenharia de Software

Guia simples para apresentar o **Escudo do Mestre Digital** ao professor.
Combine o roteiro abaixo com a documentação (`README.md`, `CODIGO.md`, `ISSUES.md`) e uma
demonstração ao vivo da aplicação.

---

## 1. Pitch de 30 segundos (o que é)

> Um escudo de mestre digital: ferramenta de mesa **offline** que centraliza a criação de
> personagens, magias, equipamento, rolagem de dados e o painel de combate de uma mesa de RPG
> (D&D 5e/2024). Está **local**, rápido e persistente, substituindo planilhas, fichas de papel e
> calculadoras manuais do mestre.

Destaque o problema resolvido (sobrecarga cognitiva do mestre: dezenas de fichas + regras
complexas + cálculos) — seção 2.1 do `README.md`.

---

## Roteiro de apresentação (3 partes)

### 1. Engenharia (processo e requisitos)
- **Requisitos** documentados: user stories em formato **BDD** com critérios de aceite (`README §4.1`),
  requisitos funcionais e não funcionais (`README §5`), incluindo o RNF de performance (**< 200 ms**).
- **Casos de uso** (UC01–UC06) especificados em tabelas (`README §7.1.2`).
- **Projeto arquitetural**: visões (lógica, processo, desenvolvimento, física), padrões adotados e
  diagramas UML — classes, sequência, estados, componentes (`README §6–7`).

> Apontar: "documentamos antes de programar, seguindo a disciplina de engenharia de software."

### 2. O código (arquitetura e boas práticas)
- **DDD (Domain-Driven Design)**: camadas bem separadas:
  - `domain/model` — entidades (`Personagem`, `Magia`, `Arma`, `Armadura`, `Historico`).
  - `domain/service` — regras (`MotorDeRegras`, `RoladorDeAtributos`, `SimuladorDeCombate`, `SlotsDeMagia`).
  - `infrastructure/persistence` → repositórios com **Exposed ORM + SQLite** (`PersonagemDAO`, etc.).
  - `presentation` → controladores **JavaFX/FXML**.
- **Padrões**: Observer (UI reage a mudanças de PV/estado), Repository/DAO, Service, separação de
  camadas — detalhados em `CODIGO.md`.
- **Fonte da regra fiel ao livro**: compra de pontos (27 pts, faixa 8–15), CD de magia, bônus de
  ataque, crítico/falha, regras de CA e escudo, hit dice por classe.

### 3. Qualidade (testes e versionamento)
- **TDD**: suíte de **134 testes** automatizados (JUnit 5) — listados por arquivo em `CODIGO.md §8`.
- Rodar: `./gradlew clean build`.
- **Matriz de rastreabilidade** requisito ↔ teste (`README §8.5`).
- **Gerência de configuração e evolução**: histórico de versões de v1.2 → v2.3
  (`README §9`), e `ISSUES.md` rastreado como backlog (issues #1–#19).

---

## Demonstração ao vivo (script sugerido)

1. **Menu inicial** → "Criar Novo Herói": visualizar a geração de atributos (rolagens de 4d6/array fixo/
   compra de pontos) e seleção de raça/classe/armadura/arma com preview de CA e dano.
2. **Gerenciar Fichas** → associar uma **magia** (repare no rótulo "(Nível X)") e um **item** ao
   grimório/inventário; preparar e conjurar gastando slot.
3. **Painel de Combate**: selecionar ficha (por **nome**), **rolar ataque** (F5), **rolar dano**
   (F6 — repare que o dano vem da **arma equipada**, sem acumular), **aplicar dano** (F7) e ver PV,
   CA, barra de vida e o **estado de combate editável**.
4. **Histórico** — mostrar o log automático de cada rolagem.
5. **Subir nível** — hit dice sobe PV máx, slots de magia e habilidades.

## Perguntas prováveis (e respostas prontas)

- **Por que JavaFX e não Web?** → Ferramenta local e offline, sem dependência de servidor, com
  persistência SQLite embarcada; foco em funcionar offline para a mesa sem internet.
- **Como garante as regras?** → Regras extraídas do SRD oficial de D&D, implementadas no domínio,
  cobertas por testes unitários por regra (compra de pontos, crítico, CD, CA, slots).
- **E se faltar cobrança para a regra X?** → A matriz de rastreabilidade (`README §8.5`) mostra
  requisito ↔ teste; o fluxo de integração é `./gradlew clean build` antes de cada release.
- **Escalabilidade/persistência?** → SQLite com transações via Exposed; CRUD relacional completo
  (1:N fichas → itens/magias/histórico).

---

## Checklist da apresentação

- [ ] Rodar `./gradlew clean build` antes (mostrar **BUILD SUCCESSFUL**, 134 testes).
- [ ] Ter fichas de exemplo já criadas para a demo.
- [ ] Abrir `README.md` nos diagramas e matriz de rastreabilidade.
- [ ] Abrir `CODIGO.md` para a arquitetura/camadas e `ISSUES.md` para o backlog.
- [ ] Ensaia as 3 fases no roteiro (10–12 min), reservando uns minutos para perguntas.