package com.escudomestre.digital.domain.model

import java.util.UUID

/**
 * Entidade do domínio representando uma magia do grimório (Seção 7.2) no padrão
 * D&D 5.5. Relaciona-se em cardinalidade um-para-muitos com Personagem (Grimorio).
 *
 * Os campos do catálogo (escola, tempo de conjuração, alcance, componentes,
 * duração, concentração e descrição) seguem o formato da SRD 5e/5.5.
 */
data class Magia(
    val nome: String,
    var nivel: Int,
    val escola: EscolaDeMagia = EscolaDeMagia.EVOCACAO,
    val tempoConjuracao: String = "1 ação",
    val alcance: String = "60 pés",
    val componentes: String = "V, S",
    val duracao: String = "Instantânea",
    val requerConcentracao: Boolean = false,
    val descricao: String = "",
    var preparada: Boolean = false,
    var slotGasto: Boolean = false,
    val id: String = UUID.randomUUID().toString(),
) {

    /** Círculo de magia formatado para a interface (ex.: "Truque", "3º círculo"). */
    val rotuloNivel: String
        get() = if (nivel == 0) "Truque" else "${nivel}º círculo"
}
