package com.escudomestre.digital.infrastructure.persistence

import java.sql.Connection
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

/**
 * Fábrica da camada de persistência: conecta ao banco SQLite local (RNF03),
 * habilita a integridade referencial e cria as tabelas do modelo.
 */
object DatabaseFactory {

    /**
     * Inicializa a conexão com o banco SQLite em [url] e garante o esquema.
     * O pragma de integridade referencial é executado a cada nova conexão.
     */
    fun init(url: String = "jdbc:sqlite:escudo.db") {
        Database.connect(
            url = url,
            driver = "org.sqlite.JDBC",
            setupConnection = { connection: Connection ->
                connection.createStatement().use { statement ->
                    statement.execute("PRAGMA foreign_keys = ON")
                }
            },
        )
        transaction {
            SchemaUtils.create(PersonagensTable, ItensTable, MagiasTable, HistoricoTable)
        }
    }
}
