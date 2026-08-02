package com.escudomestre.digital.infrastructure.persistence.repositories

import com.escudomestre.digital.domain.model.RolagemEvento
import com.escudomestre.digital.domain.repository.HistoricoRepository
import com.escudomestre.digital.infrastructure.persistence.HistoricoTable
import com.escudomestre.digital.infrastructure.persistence.toRolagemEvento
import java.util.UUID
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

/**
 * Implementação de [HistoricoRepository] sobre o Exposed ORM (RU03/RF03).
 */
class ExposedHistoricoRepository : HistoricoRepository {

    override fun registrar(evento: RolagemEvento): RolagemEvento {
        transaction {
            HistoricoTable.insert {
                it[id] = UUID.randomUUID().toString()
                it[personagemId] = evento.personagemId
                it[tipoRolagem] = evento.tipoRolagem.name
                it[resultado] = evento.resultado
                it[timestamp] = evento.timestamp
            }
        }
        return evento
    }

    override fun listarDaSessao(limite: Int): List<RolagemEvento> = transaction {
        HistoricoTable.selectAll()
            .orderBy(HistoricoTable.timestamp to SortOrder.DESC)
            .limit(limite)
            .map { it.toRolagemEvento() }
    }
}
