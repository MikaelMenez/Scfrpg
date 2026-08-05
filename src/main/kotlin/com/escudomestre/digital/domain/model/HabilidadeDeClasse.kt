package com.escudomestre.digital.domain.model

/**
 * Habilidade/feature de classe do SRD 5e. Obtida no [nivel] correspondente.
 */
data class HabilidadeDeClasse(
    val nome: String,
    val nivel: Int,
    val descricao: String,
)