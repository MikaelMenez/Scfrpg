package infrastructure.persistence

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File

object SQLiteDatabaseFactory {
    private var database: Database? = null

    fun conectar(caminho: String = "escudo.db"): Database {
        if (database == null) {
            val url = "jdbc:sqlite:$caminho"
            database = Database.connect(url, driver = "org.sqlite.JDBC")
            transaction {
                SchemaUtils.create(
                    PersonagemTable,
                    ItemTable,
                    MagiaTable,
                    HistoricoTable
                )
            }
        }
        return database!!
    }

    fun reset() {
        database = null
    }
}
