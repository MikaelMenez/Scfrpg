package personagem.funcoes
import personagem.ficha.info.Atributos
import personagem.ficha.info.Classe
import personagem.ficha.info.Personagem
import personagem.ficha.inventario.Arma
import personagem.ficha.inventario.Armadura
import personagem.ficha.acao.Ataque
import kotlin.math.floor
import kotlin.math.min

/**
 * Centraliza regras de combate derivadas da ficha.
 */
class CombateService {
    /**
     * Calcula o modificador padrao de atributo, arredondando para baixo.
     */
    fun modificador(valor: Int): Int{
        return floor((valor - 10) / 2.0).toInt()
    }

    /**
     * Calcula os pontos de vida maximos a partir da classe, constituicao e nivel.
     */
    fun calcularPV(classe: Classe, atributos: Atributos, nivel: Int): Int{
        val modCon = modificador(atributos.constituicao)

        if (nivel == 1) {
            return classe.dadoVida + modCon
        }
        return classe.dadoVida + modCon +
        (nivel - 1) * ((classe.dadoVida / 2) + 1 + modCon)
    }

    /**
     * Calcula classe de armadura considerando o tipo da armadura.
     *
     * Leve soma o modificador completo de destreza, media limita esse bonus a
     * +2 e pesada ignora destreza.
     */
    fun calcularCA(atributos: Atributos, armadura: Armadura? = null): Int{
        val modDes = modificador(atributos.destreza)

        if (armadura == null) {
            return 10 + modDes
        }

        return when (armadura.tipo.lowercase()) {
            "leve" -> armadura.classe + modDes
            "media", "média" -> armadura.classe + min(modDes, 2)
            "pesada" -> armadura.classe
            else -> armadura.classe + modDes
        }
    }

    /**
     * Iniciativa usa o modificador de destreza.
     */
    fun calcularIniciativa(atributos: Atributos): Double{
        return modificador(atributos.destreza).toDouble()
    }

    /**
     * Define o bonus de ataque considerando acuidade, distancia e proficiencia.
     */
    fun bonusAtaque(personagem: Personagem, arma: Arma): Int{
        val atributo = if (arma.acuidade)
            maxOf(
                modificador(personagem.atributos.forca),
                modificador(personagem.atributos.destreza)
            )
        else if (arma.distancia)
            modificador(personagem.atributos.destreza)
        else
            modificador(personagem.atributos.forca)

        return atributo + personagem.proficiencia
    }

    /**
     * Monta a expressao de dano exibida na ficha, somando o atributo adequado.
     */
    fun danoAtaque(personagem: Personagem,arma: Arma): String{
        val atributo = if (arma.acuidade)
            maxOf(
                modificador(personagem.atributos.forca),
                modificador(personagem.atributos.destreza)
            )
        else if (arma.distancia)
            modificador(personagem.atributos.destreza)
        else
            modificador(personagem.atributos.forca)

        return "${arma.dano}+${atributo}"
    }

    /**
     * Cria a lista de ataques a partir das armas atuais no inventario.
     */
    fun gerarAtaques(personagem: Personagem): MutableList<Ataque>{
        val ataques = mutableListOf<Ataque>()

        personagem.inventario.armas.filterIsInstance<Arma>().forEach{
            arma -> ataques.add(Ataque(
                        nome = arma.nome,
                        bonus = bonusAtaque(personagem, arma),
                        dano = danoAtaque(personagem, arma),
                        tipo = arma.tipo
                    )
                )
            }
        return ataques
    }
}
