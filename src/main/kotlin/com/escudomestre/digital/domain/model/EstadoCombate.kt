package com.escudomestre.digital.domain.model

/**
 * Estados do ciclo de vida de uma entidade durante os turnos de um combate (Seção 7.4).
 */
enum class EstadoCombate {
    /** Entidade presente no combate aguardando sua vez na ordem de iniciativa. */
    AGUARDANDO_INICIATIVA,

    /** Entidade com a iniciativa, atuando no turno corrente. */
    EM_TURNO,

    /** Entidade sob efeito de status (ex.: atordoado/incapacitado). */
    ATURDIDO_INCAPACITADO,

    /** Entidade eliminada do combate (pontosDeVidaAtual == 0). */
    DERROTADO,
}
