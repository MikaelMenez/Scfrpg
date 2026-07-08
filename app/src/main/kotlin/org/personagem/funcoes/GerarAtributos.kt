package personagem.funcoes
import personagem.ficha.info.Atributos

/**
 * Gera atributos aleatorios no formato 4d6 descartando o menor dado.
 */
class GeradorAtributos{
    fun rolarAtributo():Int{
        val dados = MutableList(4){
            (1..6).random()
        }
        return dados.sortedDescending()
            .take(3)
            .sum()
    }

    fun gerar():Atributos{
        return Atributos(
            rolarAtributo(),
            rolarAtributo(),
            rolarAtributo(),
            rolarAtributo(),
            rolarAtributo(),
            rolarAtributo()
        )
    }
}

