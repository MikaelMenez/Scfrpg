package personagem.repositorio

/**
 * Ponto unico de acesso aos repositorios de dados base.
 */
object Repo{
    val classes = ClasseRepo()
    val racas = RacaRepo()
    val magias = MagiaRepo()
    val antecedentes = AntecedenteRepo()
}
