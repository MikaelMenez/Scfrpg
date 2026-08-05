package infrastructure.persistence

import domain.model.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq // Importante para o deleteWhere
import org.jetbrains.exposed.sql.transactions.transaction

class PersonagemDAO {

    fun inserir(p: Personagem) {
        transaction {
            PersonagemTable.insert {
                // Referenciando a tabela explicitamente
                it[PersonagemTable.id] = p.id
                it[PersonagemTable.nome] = p.nome
                it[PersonagemTable.raca] = p.raca
                it[PersonagemTable.classe] = p.classe
                it[PersonagemTable.nivel] = p.nivel
                it[PersonagemTable.pontosDeVidaAtual] = p.pontosDeVidaAtual
                it[PersonagemTable.pontosDeVidaMaximo] = p.pontosDeVidaMaximo
                it[PersonagemTable.classeArmadura] = p.classeArmadura
                it[PersonagemTable.estadoCombate] = p.estadoCombate.name
                it[PersonagemTable.arma] = p.armaEquipada?.name
                it[PersonagemTable.armadura] = p.armaduraEquipada?.name
                it[PersonagemTable.escudo] = p.escudoEquipado
            }
        }
    }

    fun atualizar(p: Personagem) {
        transaction {
            PersonagemTable.update({ PersonagemTable.id eq p.id }) {
                // Referenciando a tabela explicitamente no update também
                it[PersonagemTable.nome] = p.nome
                it[PersonagemTable.raca] = p.raca
                it[PersonagemTable.classe] = p.classe
                it[PersonagemTable.nivel] = p.nivel
                it[PersonagemTable.pontosDeVidaAtual] = p.pontosDeVidaAtual
                it[PersonagemTable.pontosDeVidaMaximo] = p.pontosDeVidaMaximo
                it[PersonagemTable.classeArmadura] = p.classeArmadura
                it[PersonagemTable.estadoCombate] = p.estadoCombate.name
                it[PersonagemTable.arma] = p.armaEquipada?.name
                it[PersonagemTable.armadura] = p.armaduraEquipada?.name
                it[PersonagemTable.escudo] = p.escudoEquipado
            }
        }
    }

    fun buscarPorId(id: String): Personagem? {
        return transaction {
            PersonagemTable.select { PersonagemTable.id eq id }
                .map { row ->
                    Personagem(
                        id = row[PersonagemTable.id],
                        nome = row[PersonagemTable.nome],
                        raca = row[PersonagemTable.raca],
                        classe = row[PersonagemTable.classe],
                        nivel = row[PersonagemTable.nivel],
                        pontosDeVidaAtual = row[PersonagemTable.pontosDeVidaAtual],
                        pontosDeVidaMaximo = row[PersonagemTable.pontosDeVidaMaximo],
                        classeArmadura = row[PersonagemTable.classeArmadura],
                        estadoCombate = EstadoCombate.valueOf(row[PersonagemTable.estadoCombate]),
                        armaEquipada = row[PersonagemTable.arma]?.let { runCatching { Arma.valueOf(it) }.getOrNull() },
                        armaduraEquipada = row[PersonagemTable.armadura]?.let { runCatching { Armadura.valueOf(it) }.getOrNull() },
                        escudoEquipado = row[PersonagemTable.escudo]
                    )
                }
                .singleOrNull()
        }
    }

    fun listarTodos(): List<Personagem> {
        return transaction {
            PersonagemTable.selectAll()
                .map { row ->
                    Personagem(
                        id = row[PersonagemTable.id],
                        nome = row[PersonagemTable.nome],
                        raca = row[PersonagemTable.raca],
                        classe = row[PersonagemTable.classe],
                        nivel = row[PersonagemTable.nivel],
                        pontosDeVidaAtual = row[PersonagemTable.pontosDeVidaAtual],
                        pontosDeVidaMaximo = row[PersonagemTable.pontosDeVidaMaximo],
                        classeArmadura = row[PersonagemTable.classeArmadura],
                        estadoCombate = EstadoCombate.valueOf(row[PersonagemTable.estadoCombate]),
                        armaEquipada = row[PersonagemTable.arma]?.let { runCatching { Arma.valueOf(it) }.getOrNull() },
                        armaduraEquipada = row[PersonagemTable.armadura]?.let { runCatching { Armadura.valueOf(it) }.getOrNull() },
                        escudoEquipado = row[PersonagemTable.escudo]
                    )
                }
        }
    }

    fun deletar(id: String) {
        transaction {
            PersonagemTable.deleteWhere { PersonagemTable.id eq id }
        }
    }
}