package domain.model

data class RolagemEvento(
    val personagemId: String,
    val tipoRolagem: String,
    val resultado: Int,
    val timestamp: Long = System.currentTimeMillis()
)
