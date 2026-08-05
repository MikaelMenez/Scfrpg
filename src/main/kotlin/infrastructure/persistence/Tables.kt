package infrastructure.persistence

import org.jetbrains.exposed.sql.Table

object PersonagemTable : Table("personagens") {
    val id = varchar("id", 36)
    val nome = varchar("nome", 255)
    val raca = varchar("raca", 50)
    val classe = varchar("classe", 50)
    val nivel = integer("nivel")
    val pontosDeVidaAtual = integer("pontos_de_vida_atual")
    val pontosDeVidaMaximo = integer("pontos_de_vida_maximo")
    val classeArmadura = integer("classe_armadura")
    val estadoCombate = varchar("estado_combate", 50)
    val arma = varchar("arma", 50).nullable()
    val armadura = varchar("armadura", 50).nullable()
    val escudo = bool("escudo").default(false)

    // Nova forma de definir a chave primária
    override val primaryKey = PrimaryKey(id)
}

object ItemTable : Table("itens") {
    val id = varchar("id", 36)
    val personagemId = varchar("personagem_id", 36).references(PersonagemTable.id)
    val nome = varchar("nome", 255)
    val peso = double("peso")
    val quantidade = integer("quantidade")

    override val primaryKey = PrimaryKey(id)
}

object MagiaTable : Table("magias") {
    val id = varchar("id", 36)
    val personagemId = varchar("personagem_id", 36).references(PersonagemTable.id)
    val nome = varchar("nome", 255)
    val nivel = integer("nivel")
    val escola = varchar("escola", 50)
    val tempoConjuracao = varchar("tempo_conjuracao", 50)
    val alcance = varchar("alcance", 100)
    val componentes = varchar("componentes", 100)
    val duracao = varchar("duracao", 100)
    val requerConcentracao = bool("requer_concentracao")
    val descricao = varchar("descricao", 500)
    val preparada = bool("preparada")
    val slotGasto = bool("slot_gasto")

    override val primaryKey = PrimaryKey(id)
}

object HistoricoTable : Table("historico") {
    val id = varchar("id", 36)
    val personagemId = varchar("personagem_id", 36).references(PersonagemTable.id)
    val tipoRolagem = varchar("tipo_rolagem", 50)
    val resultado = integer("resultado")
    val timestamp = long("timestamp")

    override val primaryKey = PrimaryKey(id)
}