package com.escudomestre.digital.infrastructure.persistence

import com.escudomestre.digital.domain.model.Atributo
import com.escudomestre.digital.domain.model.Personagem
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

/**
 * DAO de Personagem (PersonagemDAO) da camada de persistência (Seção 7.3).
 * Centraliza as consultas Exposed sobre a tabela `personagens`.
 */
class PersonagemDAO {

    fun inserir(personagem: Personagem) {
        transaction {
            PersonagensTable.insert {
                it[id] = personagem.id
                it[nome] = personagem.nome
                it[raca] = personagem.raca.name
                it[classe] = personagem.classe.name
                it[nivel] = personagem.nivel
                it[forca] = personagem.valorDe(Atributo.FORCA)
                it[destreza] = personagem.valorDe(Atributo.DESTREZA)
                it[constituicao] = personagem.valorDe(Atributo.CONSTITUICAO)
                it[inteligencia] = personagem.valorDe(Atributo.INTELIGENCIA)
                it[sabedoria] = personagem.valorDe(Atributo.SABEDORIA)
                it[carisma] = personagem.valorDe(Atributo.CARISMA)
                it[pontosVidaAtual] = personagem.pontosDeVidaAtual
                it[pontosVidaMaximo] = personagem.pontosDeVidaMaximo
                it[arma] = personagem.armaEquipada?.name
                it[armadura] = personagem.armaduraEquipada.name
                it[escudo] = personagem.escudoEquipado
            }
        }
    }

    fun buscarPorId(id: String): Personagem? = transaction {
        PersonagensTable.selectAll().where { PersonagensTable.id eq id }
            .singleOrNull()
            ?.toPersonagem()
    }

    fun atualizar(personagem: Personagem) {
        transaction {
            PersonagensTable.update({ PersonagensTable.id eq personagem.id }, null) {
                it[nome] = personagem.nome
                it[raca] = personagem.raca.name
                it[classe] = personagem.classe.name
                it[nivel] = personagem.nivel
                it[forca] = personagem.valorDe(Atributo.FORCA)
                it[destreza] = personagem.valorDe(Atributo.DESTREZA)
                it[constituicao] = personagem.valorDe(Atributo.CONSTITUICAO)
                it[inteligencia] = personagem.valorDe(Atributo.INTELIGENCIA)
                it[sabedoria] = personagem.valorDe(Atributo.SABEDORIA)
                it[carisma] = personagem.valorDe(Atributo.CARISMA)
                it[pontosVidaAtual] = personagem.pontosDeVidaAtual
                it[pontosVidaMaximo] = personagem.pontosDeVidaMaximo
                it[arma] = personagem.armaEquipada?.name
                it[armadura] = personagem.armaduraEquipada.name
                it[escudo] = personagem.escudoEquipado
            }
        }
    }

    fun atualizarPontosDeVida(id: String, valor: Int) {
        transaction {
            PersonagensTable.update({ PersonagensTable.id eq id }, null) {
                it[pontosVidaAtual] = valor
            }
        }
    }

    fun excluir(id: String) {
        transaction {
            PersonagensTable.deleteWhere { PersonagensTable.id eq id }
        }
    }

    fun listar(): List<Personagem> = transaction {
        PersonagensTable.selectAll()
            .orderBy(PersonagensTable.nome)
            .map { it.toPersonagem() }
    }
}
