package infrastructure.persistence

import domain.model.Item
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq // Importante para o deleteWhere
import org.jetbrains.exposed.sql.transactions.transaction

class ItemDAO {

    fun inserir(item: Item, personagemId: String) {
        transaction {
            ItemTable.insert {
                // Adicionado 'ItemTable.' explicitamente nas colunas que faltavam
                it[ItemTable.id] = item.id
                it[ItemTable.personagemId] = personagemId
                it[ItemTable.nome] = item.nome
                it[ItemTable.peso] = item.peso
                it[ItemTable.quantidade] = item.quantidade
            }
        }
    }

    fun listarPorPersonagem(personagemId: String): List<Item> {
        return transaction {
            ItemTable.select { ItemTable.personagemId eq personagemId }
                .map { row ->
                    Item(
                        id = row[ItemTable.id],
                        nome = row[ItemTable.nome],
                        peso = row[ItemTable.peso],
                        quantidade = row[ItemTable.quantidade]
                    )
                }
        }
    }

    fun deletar(id: String) {
        transaction {
            ItemTable.deleteWhere { ItemTable.id eq id }
        }
    }
}