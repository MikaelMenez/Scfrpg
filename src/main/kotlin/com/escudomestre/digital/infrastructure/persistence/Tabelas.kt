package com.escudomestre.digital.infrastructure.persistence

import com.escudomestre.digital.domain.model.Armadura
import com.escudomestre.digital.domain.model.Arma
import com.escudomestre.digital.domain.model.Atributo
import com.escudomestre.digital.domain.model.ClasseDePersonagem
import com.escudomestre.digital.domain.model.EscolaDeMagia
import com.escudomestre.digital.domain.model.Item
import com.escudomestre.digital.domain.model.Magia
import com.escudomestre.digital.domain.model.Personagem
import com.escudomestre.digital.domain.model.Raca
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
    val raca = varchar("raca", 30)
    val classe = varchar("classe", 30)
    val nivel = integer("nivel")
    val forca = integer("forca")
    val destreza = integer("destreza")
    val constituicao = integer("constituicao")
    val inteligencia = integer("inteligencia")
    val sabedoria = integer("sabedoria")
    val carisma = integer("carisma")
    val pontosVidaAtual = integer("pontos_vida_atual")
    val pontosVidaMaximo = integer("pontos_vida_maximo")
    val arma = varchar("arma", 40).nullable()
    val armadura = varchar("armadura", 40)
    val escudo = bool("escudo")

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
    val escola = varchar("escola", 30)
    val tempoConjuracao = varchar("tempo_conjuracao", 60)
    val alcance = varchar("alcance", 60)
    val componentes = varchar("componentes", 60)
    val duracao = varchar("duracao", 60)
    val requerConcentracao = bool("requer_concentracao")
    val descricao = text("descricao")
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

internal fun ResultRow.toPersonagem(): Personagem {
    val armaduraNome = this[PersonagensTable.armadura]
    return Personagem(
        id = this[PersonagensTable.id],
        nome = this[PersonagensTable.nome],
        raca = Raca.valueOf(this[PersonagensTable.raca]),
        classe = ClasseDePersonagem.valueOf(this[PersonagensTable.classe]),
        nivel = this[PersonagensTable.nivel],
        atributos = mutableMapOf(
            Atributo.FORCA to this[PersonagensTable.forca],
            Atributo.DESTREZA to this[PersonagensTable.destreza],
            Atributo.CONSTITUICAO to this[PersonagensTable.constituicao],
            Atributo.INTELIGENCIA to this[PersonagensTable.inteligencia],
            Atributo.SABEDORIA to this[PersonagensTable.sabedoria],
            Atributo.CARISMA to this[PersonagensTable.carisma],
        ),
        pontosDeVidaAtual = this[PersonagensTable.pontosVidaAtual],
        pontosDeVidaMaximo = this[PersonagensTable.pontosVidaMaximo],
        armaEquipada = this[PersonagensTable.arma]?.let { Arma.valueOf(it) },
        armaduraEquipada = Armadura.valueOf(armaduraNome),
        escudoEquipado = this[PersonagensTable.escudo],
    )
}

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
    escola = EscolaDeMagia.valueOf(this[MagiasTable.escola]),
    tempoConjuracao = this[MagiasTable.tempoConjuracao],
    alcance = this[MagiasTable.alcance],
    componentes = this[MagiasTable.componentes],
    duracao = this[MagiasTable.duracao],
    requerConcentracao = this[MagiasTable.requerConcentracao],
    descricao = this[MagiasTable.descricao],
    preparada = this[MagiasTable.preparada],
    slotGasto = this[MagiasTable.slotGasto],
)

internal fun ResultRow.toRolagemEvento(): RolagemEvento = RolagemEvento(
    personagemId = this[HistoricoTable.personagemId],
    tipoRolagem = TipoRolagem.valueOf(this[HistoricoTable.tipoRolagem]),
    resultado = this[HistoricoTable.resultado],
    timestamp = this[HistoricoTable.timestamp],
)
