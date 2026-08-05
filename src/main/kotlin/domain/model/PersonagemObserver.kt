package domain.model

interface PersonagemObserver {
    fun onPersonagemAlterado(personagem: Personagem)
}

interface PersonagemObservable {
    fun adicionarObserver(observer: PersonagemObserver)
    fun removerObserver(observer: PersonagemObserver)
    fun notificarObservers()
}
