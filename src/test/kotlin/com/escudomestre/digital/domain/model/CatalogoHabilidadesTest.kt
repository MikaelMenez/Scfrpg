package com.escudomestre.digital.domain.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CatalogoHabilidadesTest {

    @Test
    fun `barbaro obtem fúria no nivel 1`() {
        val nivel1 = CatalogoHabilidades.habilidadesAteNivel(ClasseDePersonagem.BARBARO, 1)
        assertTrue(nivel1.any { it.nome == "Fúria" })
    }

    @Test
    fun `magia de classe listada somente quando dentro do nivel atual`() {
        val magoNivel1 = CatalogoHabilidades.habilidadesAteNivel(ClasseDePersonagem.MAGO, 1)
        assertTrue(magoNivel1.any { it.nome == "Conjuração" })
        assertTrue(magoNivel1.none { it.nome == "Tradição Arcana" })
    }

    @Test
    fun `todas as doze classes possuem habilidades catalogadas`() {
        ClasseDePersonagem.entries.forEach { classe ->
            assertTrue(
                CatalogoHabilidades.habilidadesDaClasse(classe).isNotEmpty(),
                "classe ${classe.name} sem habilidades",
            )
        }
    }
}