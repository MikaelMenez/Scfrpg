package infrastructure.persistence

import domain.model.EscolaDeMagia
import domain.model.Magia
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

class MagiaDAO {

    fun inserir(magia: Magia, personagemId: String) {
        transaction {
            MagiaTable.insert {
                it[MagiaTable.id] = magia.id
                it[MagiaTable.personagemId] = personagemId
                it[MagiaTable.nome] = magia.nome
                it[MagiaTable.nivel] = magia.nivel
                it[MagiaTable.escola] = magia.escola.name
                it[MagiaTable.tempoConjuracao] = magia.tempoConjuracao
                it[MagiaTable.alcance] = magia.alcance
                it[MagiaTable.componentes] = magia.componentes
                it[MagiaTable.duracao] = magia.duracao
                it[MagiaTable.requerConcentracao] = magia.requerConcentracao
                it[MagiaTable.descricao] = magia.descricao
                it[MagiaTable.preparada] = magia.preparada
                it[MagiaTable.slotGasto] = magia.slotGasto
            }
        }
    }

    fun listarPorPersonagem(personagemId: String): List<Magia> {
        return transaction {
            MagiaTable.select { MagiaTable.personagemId eq personagemId }
                .orderBy(MagiaTable.nivel to SortOrder.ASC)
                .map { row -> toMagia(row) }
        }
    }

    fun buscarPorId(id: String): Magia? {
        return transaction {
            MagiaTable.select { MagiaTable.id eq id }
                .map { row -> toMagia(row) }
                .singleOrNull()
        }
    }

    fun deletar(id: String) {
        transaction {
            MagiaTable.deleteWhere { MagiaTable.id eq id }
        }
    }

    /** Atualiza apenas o estado de preparo/gasto de slot (5e: conjurar gasta o slot). */
    fun atualizarStatus(id: String, preparada: Boolean, slotGasto: Boolean) {
        transaction {
            MagiaTable.update({ MagiaTable.id eq id }) {
                it[MagiaTable.preparada] = preparada
                it[MagiaTable.slotGasto] = slotGasto
            }
        }
    }

    private fun toMagia(row: ResultRow): Magia {
        return Magia(
            id = row[MagiaTable.id],
            nome = row[MagiaTable.nome],
            nivel = row[MagiaTable.nivel],
            escola = EscolaDeMagia.valueOf(row[MagiaTable.escola]),
            tempoConjuracao = row[MagiaTable.tempoConjuracao],
            alcance = row[MagiaTable.alcance],
            componentes = row[MagiaTable.componentes],
            duracao = row[MagiaTable.duracao],
            requerConcentracao = row[MagiaTable.requerConcentracao],
            descricao = row[MagiaTable.descricao],
            preparada = row[MagiaTable.preparada],
            slotGasto = row[MagiaTable.slotGasto]
        )
    }
}