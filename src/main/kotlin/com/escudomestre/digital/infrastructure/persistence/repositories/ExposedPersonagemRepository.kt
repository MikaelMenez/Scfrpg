package com.escudomestre.digital.infrastructure.persistence.repositories

import com.escudomestre.digital.domain.model.Personagem
import com.escudomestre.digital.domain.repository.PersonagemRepository
import com.escudomestre.digital.infrastructure.persistence.PersonagemDAO

/**
 * Implementação de [PersonagemRepository] sobre o Exposed ORM (RF01),
 * delegando as consultas ao [PersonagemDAO] (Seção 7.3).
 */
class ExposedPersonagemRepository(
    private val dao: PersonagemDAO = PersonagemDAO(),
) : PersonagemRepository {

    override fun criar(personagem: Personagem): Personagem {
        dao.inserir(personagem)
        return personagem
    }

    override fun buscarPorId(id: String): Personagem? = dao.buscarPorId(id)

    override fun atualizar(personagem: Personagem): Personagem {
        dao.atualizar(personagem)
        return personagem
    }

    override fun atualizarPontosDeVida(id: String, valor: Int) {
        dao.atualizarPontosDeVida(id, valor)
    }

    override fun excluir(id: String) {
        dao.excluir(id)
    }

    override fun listar(): List<Personagem> = dao.listar()
}
