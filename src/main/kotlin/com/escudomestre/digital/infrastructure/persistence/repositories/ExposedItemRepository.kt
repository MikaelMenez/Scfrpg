package com.escudomestre.digital.infrastructure.persistence.repositories

import com.escudomestre.digital.domain.model.Item
import com.escudomestre.digital.domain.repository.ItemRepository
import com.escudomestre.digital.infrastructure.persistence.ItensTable
import com.escudomestre.digital.infrastructure.persistence.toItem
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

/**
 * Implementação de [ItemRepository] sobre o Exposed ORM (RF01).
 */
class ExposedItemRepository : ItemRepository {

    override fun criar(personagemId: String, item: Item): Item {
        transaction {
            ItensTable.insert {
                it[id] = item.id
                it[ItensTable.personagemId] = personagemId
                it[nome] = item.nome
                it[peso] = item.peso
                it[quantidade] = item.quantidade
            }
        }
        return item
    }

    override fun buscarPorId(id: String): Item? = transaction {
        ItensTable.selectAll().where { ItensTable.id eq id }
            .singleOrNull()
            ?.toItem()
    }

    override fun listarPorPersonagem(personagemId: String): List<Item> = transaction {
        ItensTable.selectAll().where { ItensTable.personagemId eq personagemId }
            .orderBy(ItensTable.nome)
            .map { it.toItem() }
    }

    override fun atualizar(item: Item): Item {
        transaction {
            ItensTable.update({ ItensTable.id eq item.id }, null) {
                it[nome] = item.nome
                it[peso] = item.peso
                it[quantidade] = item.quantidade
            }
        }
        return item
    }

    override fun excluir(id: String) {
        transaction {
            ItensTable.deleteWhere { ItensTable.id eq id }
        }
    }
}
