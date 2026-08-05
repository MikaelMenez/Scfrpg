package domain.model

data class Historico(
    val id: String,
    val personagemId: String,
    val tipoRolagem: String,
    val resultado: Int,
    val timestamp: Long
) {
    companion object {
        fun registrar(evento: RolagemEvento): Historico {
            return Historico(
                id = java.util.UUID.randomUUID().toString(),
                personagemId = evento.personagemId,
                tipoRolagem = evento.tipoRolagem,
                resultado = evento.resultado,
                timestamp = evento.timestamp
            )
        }
    }
}
