package com.escudomestre.digital.infrastructure.persistence.repositories

import com.escudomestre.digital.domain.model.Magia
import com.escudomestre.digital.domain.repository.MagiaRepository
import com.escudomestre.digital.infrastructure.persistence.MagiasTable
import com.escudomestre.digital.infrastructure.persistence.toMagia
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

/**
 * Implementação de [MagiaRepository] sobre o Exposed ORM (RF01).
 */
class ExposedMagiaRepository : MagiaRepository {

    override fun criar(personagemId: String, magia: Magia): Magia {
        transaction {
            MagiasTable.insert {
                it[id] = magia.id
                it[MagiasTable.personagemId] = personagemId
                it[nome] = magia.nome
                it[nivel] = magia.nivel
                it[preparada] = magia.preparada
                it[slotGasto] = magia.slotGasto
            }
        }
        return magia
    }

    override fun buscarPorId(id: String): Magia? = transaction {
        MagiasTable.selectAll().where { MagiasTable.id eq id }
            .singleOrNull()
            ?.toMagia()
    }

    override fun listarPorPersonagem(personagemId: String): List<Magia> = transaction {
        MagiasTable.selectAll().where { MagiasTable.personagemId eq personagemId }
            .orderBy(MagiasTable.nome)
            .map { it.toMagia() }
    }

    override fun atualizar(magia: Magia): Magia {
        transaction {
            MagiasTable.update({ MagiasTable.id eq magia.id }, null) {
                it[nome] = magia.nome
                it[nivel] = magia.nivel
                it[preparada] = magia.preparada
                it[slotGasto] = magia.slotGasto
            }
        }
        return magia
    }

    override fun excluir(id: String) {
        transaction {
            MagiasTable.deleteWhere { MagiasTable.id eq id }
        }
    }
}
