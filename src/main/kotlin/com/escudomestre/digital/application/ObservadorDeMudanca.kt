package com.escudomestre.digital.application

import com.escudomestre.digital.domain.model.Personagem

/**
 * Contrato do padrão Observer (Seção 6.5): a interface gráfica se registra no
 * simulador para ser notificada sempre que o estado de uma entidade muda.
 */
fun interface ObservadorDeMudanca {
    /** Chamado quando o estado da [personagem] é alterado pelo motor de regras. */
    fun notificarMudancaDeEstado(personagem: Personagem)
}
