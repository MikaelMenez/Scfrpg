# Escudo do Mestre Digital — Backlog e Rastreamento de Issues

> **Status:** cada issue traz o estado atual em relação ao código (✅ implementado, 🟡 parcial, ⏳ pendente).
> Este arquivo rastreia o progresso das funcionalidades em relação ao código-fonte atual.
> (Reconstruído após perda acidental do arquivo durante a edição; verifiqueu o histórico.)

---

## #1 — Persistência em SQLite com Exposed ORM · ✅

`SQLiteDatabaseFactory`, `Tables` (PersonagemTable, ItemTable, MagiaTable, HistoricoTable),
`PersonagemDAO`, `ItemDAO`, `MagiaDAO` e `HistoricoDAO`. CRUD completo e relacionamentos
(1:N entre personagem e itens/magias/histórico).

---

## #2 — Interface gráfica (Janelas separadas) · 🟡 Parcial

`menu_inicial.fxml` (navegação), `criar_heroi.fxml` (formulário + preview com armadura/arma),
`gerenciar_fichas.fxml` e `painel_combate.fxml` + `styles.css`. **Pendente:** abas do painel
(Ficha/Habilidades/Grimório/Inventário/Histórico) e a tela de seleção em estilo Netflix —
hoje o app usa janelas separadas.

## #3 — Observers e atualização da UI · ✅

Padrão Observer no `Personagem` notificando `PersonagemObserver` a cada alteração de dano,
cura, nível e, de forma assíncrona, a persistência via thread de background.

## #4 — Níveis (Level Up) na interface (UC06) · ✅

Botão no painel → `Personagem.subirNivel()` (rola o hit dice + mod de Constituição, ou média
no modo suportado) e persistência.

## #5 — Regras de combate 5e/2024 · ✅

`MotorDeRegras` (vantagem/desvantagem, cálculo de modificador), `testarAtaque` com crítico
(natural 20) e falha (natural 1), dano desarmado (1 + Força), `SimuladorDeCombate.atacar`
decidindo acerto por CA e aplicando dano dobrado no crítico.

## #6 — Atributos e proficiência (tabela 5e) · ✅

Regras de atributo (`RegrasDeAtributo.kt`): `modificadorDeAtributo`, `bonusDeProfic¸´encia`,
`cdDeResistencia` e `bonusAtaqueDeMagia`.

## #7 — Armadura e CA · 🟡 Parcial

`Armadura` (padrão coberto: leve `10 + modDES`, média `13/14/15 + modDES` com teto `2`,
pesada `15/16/17/18` fixa; `ESCUDO` +2) e `caEfetiva()` no personagem. **Pendente:** exibição
completa das armadura na interface além da seleção em `criar_heroi`.

## #8 — Regras D&D: atributos, raças/classes, equipamento e combate · ✅

`ConstrutorDeFicha`, `RoladorDeAtributos` (4d6, fixo, compra de pontos fiel — 27 pts, faixa
8–15, custo 0–9), enum `Atributo`, equipamento completo (`Arma` e `Armadura` SRD com regras de
CA/escudo), `testarAtaque` (crítico/falha), dano desarmado, descansos e
`SimuladorDeCombate.atacar` por CA.

## #9 — Regras de magia D&D 5.5 (grimório, slots, catálogo) · 🟡 Parcial

`EscolaDeMagia`, `Magia` (todos os campos SRD), `SlotsDeMagia` (conjuradores plenos, metade,
pacto), `CatalogoDeMagias` (24 magias) persistido. Grimório em `gerenciar_fichas` permite
**preparar/despreparar** e **conjurar (gastar slot)** via menu de contexto (~ MagiaDAO.atualizarStatus).
**Pendente:** a aba `Grimório` no painel de jogo.

## #10 — Habilidades de classe (catálogo SRD) · 🟡 Parcial

`CatalogoHabilidades` + `HabilidadeDeClasse` para as 12 classes, com filtro por nível
(`habilidadesAteNivel`). **Pendente:** exibição na UI (aba Habilidades não existe).

## #11 — Histórico da sessão na interface · ✅

`HistoricoDAO` (ordem decrescente) + `listHistorico` no `PainelCombateController`, recarregado
após cada rolagem/aplicação.

## #12 — Catálogo de armaduras/armas na criação · ✅

Seleção de `Armadura`, `Arma` e escudo no `criar_heroi` com preview de CA, bônus de ataque e
dano; persistida no banco (colunas `arma`, `armadura`, `escudo`).

## #13 — Seletor de fichas por nome (não pelo ID) · ✅

`carregarFichas` usa `cellFactory`/`ListCell<Personagem>` renderizando apenas `nome`
(não o ID/`toString`) no `comboPersonagem` do painel de combate.

## #14 — Estado de combate editável · ✅

`ComboBox<EstadoCombate>` no `painel_combate.fxml` (substituiu o `Label` fixo), valores de
`EstadoCombate`, persistido via `PersonagemDAO.atualizar` e sincronizado em `atualizarUI`.

## #15 — Seletor de magias com nível · ✅

`GerenciarFichasController.adicionarMagia` usa `ChoiceDialog` com rótulo
`"${nome} (Nível ${nivel})"` (via `associateBy`).

## #16 — Correção da aplicação de dano · ✅

`executarAplicarDano` força `atualizarUI(p)` e exibe "Dano aplicado: X (PV restante Y)".

## #17 — Correção de acúmulo de dano nas rolagens · ✅

`executarRolarDano` antes lia `txtDano` como modificador mas gravava o resultado no mesmo campo,
fazendo rolagens sucessivas somarem o valor anterior. Agora deriva quantidade/faces/bônus da
**arma equipada** via `Personagem.dadosDeDano()` (bônus constante) e preenche o resultado em
`txtDano`/`txtResultado` — as rolagens são independentes e não acumulam (documentado no `CODIGO.md`).

## #18 — Remoção institucional (UFPB / Centro de Informática) · ✅

Removidas todas as referências a "UFPB" e "Centro de Informática" bem como o rótulo de versão da
tela principal: `menu_inicial.fxml`, título da janela em `MainApp`, `build.gradle.kts` (group),
`README.md` e `CODIGO.md`.

## #19 — Teste de persistência de estado (round-trip) · ✅

`RepositorioIntegracaoTest` valida a alteração manual de `EstadoCombate` (define
`ATURDIDO_INCAPACITADO` → persiste → recupera). Suíte: **134 testes**.

---

> Ao finalizar, rodar `./gradlew clean build` (compila + testa a suíte, hoje **134 testes**).