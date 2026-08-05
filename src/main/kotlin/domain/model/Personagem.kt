package domain.model

class Personagem(
    val id: String,
    var nome: String,
    var raca: String,
    var classe: String,
    var nivel: Int = 1,
    var pontosDeVidaAtual: Int,
    var pontosDeVidaMaximo: Int,
    var classeArmadura: Int,
    var estadoCombate: EstadoCombate = EstadoCombate.AGUARDANDO_INICIATIVA,
    val atributos: MutableMap<String, Int> = mutableMapOf(),
    var armaEquipada: Arma? = null,
    var armaduraEquipada: Armadura? = null,
    var escudoEquipado: Boolean = false,
) : PersonagemObservable {

    private val observers = mutableListOf<PersonagemObserver>()
    val inventario = mutableListOf<Item>()
    val grimorio = mutableListOf<Magia>()
    val historico = mutableListOf<Historico>()

    fun modificadorDe(atributo: Atributo): Int {
        val valor = atributos[atributo.name]
            ?: atributos[atributo.sigla]
            ?: atributos[atributo.rotulo]
        return if (valor != null) modificadorDeAtributo(valor) else 0
    }

    fun bonusProficiencia(): Int = bonusDeProficiencia(nivel)

    /** Nível de defesa efetivo a partir da armadura equipada (escudo incluso). */
    fun caEfetiva(): Int {
        val armadura = armaduraEquipada ?: Armadura.NENHUMA
        return armadura.calcularCA(modificadorDe(Atributo.DESTREZA), escudoEquipado)
    }

    /** Nível de acerto do ataque corpo a corpo ou à distância (bônus 5e real). */
    fun modificadorAtaque(): Int {
        val arma = armaEquipada ?: Arma.DESARMADO
        return bonusProficiencia() + modificadorDe(arma.atributoDeCombate)
    }

    /** Dados, faces e bônus de dano da arma equipada. Desarmado = (0, 1, 0) → total 1. */
    fun dadosDeDano(): Triple<Int, Int, Int> {
        val arma = armaEquipada ?: Arma.DESARMADO
        if (arma == Arma.DESARMADO) {
            val forca = modificadorDe(Atributo.FORCA)
            return Triple(0, 1, forca) // dano desarmado = 1 + mod de Força
        }
        val (quantidade, base) = arma.danoComModificadores(
            modificadorDe(Atributo.FORCA),
            modificadorDe(Atributo.DESTREZA),
        )
        val bonus = base - arma.facesDano
        return Triple(quantidade, arma.facesDano, bonus)
    }

    /** CD de resistência das suas magias. Requer atributo de conjuração definido por [atributoConjuracao]. */
    fun valorDe(atributo: Atributo): Int =
        atributos[atributo.name] ?: atributos[atributo.sigla] ?: atributos[atributo.rotulo] ?: 10

    /** CD de resistência das suas magias usando o atributo de conjuração informado. */
    fun cdDeResistencia(atributoConjuracao: Atributo): Int =
        cdDeResistencia(valorDe(atributoConjuracao), nivel)

    /** Bônus de ataque de magia usando o atributo de conjuração informado. */
    fun bonusAtaqueDeMagia(atributoConjuracao: Atributo): Int =
        bonusAtaqueDeMagia(valorDe(atributoConjuracao), nivel)

    /** Descanso curto: recupera metade dos dados de vida gastos (no mínimo a vida total não muda). */
    fun descansoCurto() {
        notificarObservers()
    }

    /** Descanso longo: recupera todos os PVs. */
    fun descansoLongo() {
        pontosDeVidaAtual = pontosDeVidaMaximo
        notificarObservers()
    }

    fun aplicarDano(valor: Int) {
        pontosDeVidaAtual -= valor
        if (pontosDeVidaAtual <= 0) {
            pontosDeVidaAtual = 0
            estadoCombate = EstadoCombate.DERROTADO
        }
        notificarObservers()
    }

    fun curar(valor: Int) {
        pontosDeVidaAtual += valor
        if (pontosDeVidaAtual > pontosDeVidaMaximo) {
            pontosDeVidaAtual = pontosDeVidaMaximo
        }
        if (estadoCombate == EstadoCombate.DERROTADO && pontosDeVidaAtual > 0) {
            estadoCombate = EstadoCombate.AGUARDANDO_INICIATIVA
        }
        notificarObservers()
    }

    fun subirNivel(mediaAoNivelar: Boolean = false) {
        nivel++
        val hitDice = Classe.valueOf(classe.uppercase()).hitDice
        val modConst = modificadorDe(Atributo.CONSTITUICAO)
        val aumentoVida = if (mediaAoNivelar) (hitDice + 1) / 2 + modConst
            else DadoVirtual.rolar(hitDice, modConst)
        pontosDeVidaMaximo += aumentoVida
        pontosDeVidaAtual += aumentoVida
        notificarObservers()
    }

    override fun adicionarObserver(observer: PersonagemObserver) {
        observers.add(observer)
    }

    override fun removerObserver(observer: PersonagemObserver) {
        observers.remove(observer)
    }

    override fun notificarObservers() {
        observers.forEach { it.onPersonagemAlterado(this) }
    }

    override fun toString(): String {
        return "Personagem(id=$id, nome=$nome, raca=$raca, classe=$classe, nivel=$nivel, PV=$pontosDeVidaAtual/$pontosDeVidaMaximo, CA=$classeArmadura, estado=$estadoCombate)"
    }
}
