package infrastructure.persistence

import domain.model.Historico
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

class HistoricoDAO {

    fun inserir(h: Historico) {
        transaction {
            HistoricoTable.insert {
                // Adicionado 'HistoricoTable.' explicitamente em cada coluna
                it[HistoricoTable.id] = h.id
                it[HistoricoTable.personagemId] = h.personagemId
                it[HistoricoTable.tipoRolagem] = h.tipoRolagem
                it[HistoricoTable.resultado] = h.resultado
                it[HistoricoTable.timestamp] = h.timestamp
            }
        }
    }

    fun listarPorPersonagem(personagemId: String): List<Historico> {
        return transaction {
            HistoricoTable.select { HistoricoTable.personagemId eq personagemId }
                .orderBy(HistoricoTable.timestamp to SortOrder.DESC)
                .map { row ->
                    Historico(
                        id = row[HistoricoTable.id],
                        personagemId = row[HistoricoTable.personagemId],
                        tipoRolagem = row[HistoricoTable.tipoRolagem],
                        resultado = row[HistoricoTable.resultado],
                        timestamp = row[HistoricoTable.timestamp]
                    )
                }
        }
    }

    fun listarTodos(): List<Historico> {
        return transaction {
            HistoricoTable.selectAll()
                .orderBy(HistoricoTable.timestamp to SortOrder.DESC)
                .map { row ->
                    Historico(
                        id = row[HistoricoTable.id],
                        personagemId = row[HistoricoTable.personagemId],
                        tipoRolagem = row[HistoricoTable.tipoRolagem],
                        resultado = row[HistoricoTable.resultado],
                        timestamp = row[HistoricoTable.timestamp]
                    )
                }
        }
    }
}