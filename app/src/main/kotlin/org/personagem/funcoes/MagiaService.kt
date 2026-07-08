package personagem.funcoes
import personagem.ficha.acao.Magia
import personagem.ficha.acao.NivelMagia
import personagem.ficha.acao.SlotsMagia
import personagem.ficha.info.Classe
import personagem.ficha.info.Personagem
import personagem.repositorio.MagiaRepo
import kotlin.math.floor


/**
 * Regras de conjuracao, espacos de magia e calculos magicos.
 */
class MagiaService(
    private val magiaRepo: MagiaRepo
){
    /**
     * Calcula os espacos de magia disponiveis para a classe e nivel.
     */
    fun calcularEspacos(classe: Classe, nivel: Int): SlotsMagia{
        val slots = mutableListOf<NivelMagia>()

        if (!classe.conjurador) {
            return SlotsMagia(slots)
        }

        when (nivel) {
            1 -> {
                slots.add(NivelMagia(1, 2, 0))
            }
            2 -> {
                slots.add(NivelMagia(1, 3, 0))
            }
            3 -> {
                slots.add(NivelMagia(1, 4, 0))
                slots.add(NivelMagia(2, 2, 0))
            }
            4 -> {
                slots.add(NivelMagia(1, 4, 0))
                slots.add(NivelMagia(2, 3, 0))
            }
            5 -> {
                slots.add(NivelMagia(1, 4, 0))
                slots.add(NivelMagia(2, 3, 0))
                slots.add(NivelMagia(3, 2, 0))
            }
            6 -> {
                slots.add(NivelMagia(1, 4, 0))
                slots.add(NivelMagia(2, 3, 0))
                slots.add(NivelMagia(3, 3, 0))
            }
            7 -> {
                slots.add(NivelMagia(1, 4, 0))
                slots.add(NivelMagia(2, 3, 0))
                slots.add(NivelMagia(3, 3, 0))
                slots.add(NivelMagia(4, 1, 0))
            }
            8 -> {
                slots.add(NivelMagia(1, 4, 0))
                slots.add(NivelMagia(2, 3, 0))
                slots.add(NivelMagia(3, 3, 0))
                slots.add(NivelMagia(4, 2, 0))
            }
            9 -> {
                slots.add(NivelMagia(1, 4, 0))
                slots.add(NivelMagia(2, 3, 0))
                slots.add(NivelMagia(3, 3, 0))
                slots.add(NivelMagia(4, 3, 0))
                slots.add(NivelMagia(5, 1, 0))
            }
        }

        return SlotsMagia(slots)
    }

    /**
     * Verifica se a magia pode ser conjurada com os slots atuais.
     */
    fun podeConjurar(personagem: Personagem, magia: Magia): Boolean{
        if (magia.nivel == 0) {
            return true
        }

        val slot = personagem.slotsMagia.espacos.find{it.nivel == magia.nivel}

        return slot != null && slot.usados < slot.maximo
    }

    /**
     * Conjura uma magia e consome um slot quando necessario.
     */
    fun conjurarMagia(personagem: Personagem, magia: Magia): Boolean{
        if (!podeConjurar(personagem, magia)) {
            return false
        }

        if (magia.nivel > 0) {
            personagem.slotsMagia.espacos.first { it.nivel == magia.nivel }.usados++
        }

        return true
    }

    /**
     * Recupera todos os espacos de magia gastos.
     */
    fun restaurarEspacos(personagem: Personagem) {
        personagem.slotsMagia.espacos.forEach {
            it.usados = 0
        }
    }

    /**
     * Calcula o modificador do atributo usado para conjuracao da classe.
     */
    fun modificadorConjuracao(personagem: Personagem): Int{
        val atributo = when (personagem.classe.atributoConjuracao) {
            "INT" ->
                personagem.atributos.inteligencia
            "SAB" ->
                personagem.atributos.sabedoria
            "CAR" ->
                personagem.atributos.carisma
            else -> 10
        }
        return floor((atributo - 10) / 2.0).toInt()
    }

    /**
     * Calcula a classe de dificuldade das magias.
     */
    fun cdMagia(personagem: Personagem): Int{
        return 8 + personagem.proficiencia +
        modificadorConjuracao(personagem)
    }

    /**
     * Calcula o bonus de ataques magicos.
     */
    fun bonusAtaqueMagico(personagem: Personagem): Int{
        return personagem.proficiencia +
                modificadorConjuracao(personagem)
    }

    /**
     * Ponto de extensao para selecionar magias iniciais de uma classe.
     */
    fun magiasIniciais(classe: Classe): MutableList<Magia>{
        return classe.magiasIniciais.mapNotNull{magiaRepo.buscar(it)}.toMutableList()
    }

}
