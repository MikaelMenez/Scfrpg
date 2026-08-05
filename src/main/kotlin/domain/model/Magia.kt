package domain.model

/**
 * Magia modelada conforme o sistema de magia do SRD 5e (2024).
 * Nível 0 representa um truque (cantrip).
 */
class Magia(
    val id: String,
    var nome: String,
    var nivel: Int,
    var escola: EscolaDeMagia,
    var tempoConjuracao: String,
    var alcance: String,
    var componentes: String,
    var duracao: String,
    var requerConcentracao: Boolean,
    var descricao: String,
    var preparada: Boolean,
    var slotGasto: Boolean
) {
    val ehTruque: Boolean get() = nivel == 0

    fun copiaParaGrimorio(novoId: String): Magia = Magia(
        id = novoId,
        nome = nome,
        nivel = nivel,
        escola = escola,
        tempoConjuracao = tempoConjuracao,
        alcance = alcance,
        componentes = componentes,
        duracao = duracao,
        requerConcentracao = requerConcentracao,
        descricao = descricao,
        preparada = true,
        slotGasto = false
    )

    override fun toString(): String =
        "$nome (nível $nivel, ${escola.nomeExibicao})"
}