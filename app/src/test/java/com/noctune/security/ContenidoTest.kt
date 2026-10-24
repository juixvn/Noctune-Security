package com.noctune.security

import com.noctune.security.model.CategoriaResultado
import com.noctune.security.model.Contenido
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ContenidoTest {
    @Test
    fun nivelesSegunPuntaje() {
        assertEquals("Riesgo bajo", Contenido.nivelPara(100))
        assertEquals("Riesgo bajo", Contenido.nivelPara(75))
        assertEquals("Riesgo medio", Contenido.nivelPara(74))
        assertEquals("Riesgo medio", Contenido.nivelPara(40))
        assertEquals("Riesgo alto", Contenido.nivelPara(39))
        assertEquals("Riesgo alto", Contenido.nivelPara(0))
    }

    @Test
    fun detalleSeCodificaYDecodifica() {
        val original = listOf(CategoriaResultado("Respaldos", 1, 2), CategoriaResultado("Recuperación de cuentas", 2, 2))
        assertEquals(original, Contenido.decodificarDetalle(Contenido.codificarDetalle(original)))
    }

    @Test
    fun cadaPreguntaTieneCategoriaValidaYPrioridad() {
        val nombres = Contenido.categorias.map { it.nombre }
        Contenido.preguntas.forEach {
            assertTrue(it.categoria in nombres)
            assertTrue(it.prioridad in 1..3)
        }
        assertEquals(Contenido.preguntas.size, Contenido.preguntas.map { it.id }.toSet().size)
    }
}
