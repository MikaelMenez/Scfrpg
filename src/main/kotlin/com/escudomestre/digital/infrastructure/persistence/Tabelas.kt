package com.escudomestre.digital.infrastructure.persistence

import com.escudomestre.digital.domain.model.Item
import com.escudomestre.digital.domain.model.Magia
import com.escudomestre.digital.domain.model.Personagem
import com.escudomestre.digital.domain.model.RolagemEvento
import com.escudomestre.digital.domain.model.TipoRolagem
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table

/**
 * Mapeamento das entidades do domínio (Seção 7.2) para o banco relacional via Exposed ORM (RF01).
 */
object PersonagensTable : Table("personagens") {
    val id = varchar("id", 36)
    val nome = varchar("nome", 100)
    val raca = varchar("raca", 50)
    val classe = varchar("classe", 50)
    val nivel = integer("nivel")
    val pontosVidaAtual = integer("pontos_vida_atual")
    val pontosVidaMaximo = integer("pontos_vida_maximo")
    val classeArmadura = integer("classe_armadura")
    val modificadorAtaque = integer("modificador_ataque")

    override val primaryKey = PrimaryKey(id)
}

object ItensTable : Table("itens") {
    val id = varchar("id", 36)
    val personagemId = varchar("personagem_id", 36).references(PersonagensTable.id)
    val nome = varchar("nome", 100)
    val peso = double("peso")
    val quantidade = integer("quantidade")

    override val primaryKey = PrimaryKey(id)
}

object MagiasTable : Table("magias") {
    val id = varchar("id", 36)
    val personagemId = varchar("personagem_id", 36).references(PersonagensTable.id)
    val nome = varchar("nome", 100)
    val nivel = integer("nivel")
    val preparada = bool("preparada")
    val slotGasto = bool("slot_gasto")

    override val primaryKey = PrimaryKey(id)
}

object HistoricoTable : Table("historico") {
    val id = varchar("id", 36)
    val personagemId = varchar("personagem_id", 36).references(PersonagensTable.id)
    val tipoRolagem = varchar("tipo_rolagem", 20)
    val resultado = integer("resultado")
    val timestamp = long("timestamp")

    override val primaryKey = PrimaryKey(id)
}

internal fun ResultRow.toPersonagem(): Personagem = Personagem(
    id = this[PersonagensTable.id],
    nome = this[PersonagensTable.nome],
    raca = this[PersonagensTable.raca],
    classe = this[PersonagensTable.classe],
    nivel = this[PersonagensTable.nivel],
    pontosDeVidaAtual = this[PersonagensTable.pontosVidaAtual],
    pontosDeVidaMaximo = this[PersonagensTable.pontosVidaMaximo],
    classeArmadura = this[PersonagensTable.classeArmadura],
    modificadorAtaque = this[PersonagensTable.modificadorAtaque],
)

internal fun ResultRow.toItem(): Item = Item(
    id = this[ItensTable.id],
    nome = this[ItensTable.nome],
    peso = this[ItensTable.peso],
    quantidade = this[ItensTable.quantidade],
)

internal fun ResultRow.toMagia(): Magia = Magia(
    id = this[MagiasTable.id],
    nome = this[MagiasTable.nome],
    nivel = this[MagiasTable.nivel],
    preparada = this[MagiasTable.preparada],
    slotGasto = this[MagiasTable.slotGasto],
)

internal fun ResultRow.toRolagemEvento(): RolagemEvento = RolagemEvento(
    personagemId = this[HistoricoTable.personagemId],
    tipoRolagem = TipoRolagem.valueOf(this[HistoricoTable.tipoRolagem]),
    resultado = this[HistoricoTable.resultado],
    timestamp = this[HistoricoTable.timestamp],
)
