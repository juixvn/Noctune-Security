package com.noctune.security

import com.noctune.security.util.Validaciones
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ValidacionesTest {
    @Test
    fun correosValidos() {
        assertTrue(Validaciones.correoValido("andy@noctune.com"))
        assertTrue(Validaciones.correoValido("  a.b+c@mail.cl  "))
    }

    @Test
    fun correosInvalidos() {
        assertFalse(Validaciones.correoValido(""))
        assertFalse(Validaciones.correoValido("sin-arroba.com"))
        assertFalse(Validaciones.correoValido("a@b"))
        assertFalse(Validaciones.correoValido("a b@mail.com"))
    }

    @Test
    fun usuarios() {
        assertNull(Validaciones.errorUsuario("andy_99"))
        assertNotNull(Validaciones.errorUsuario("ab"))
        assertNotNull(Validaciones.errorUsuario("con espacio"))
        assertNotNull(Validaciones.errorUsuario("a".repeat(25)))
    }

    @Test
    fun claves() {
        assertNull(Validaciones.errorClave("Segura123"))
        assertEquals("Debe tener al menos 8 caracteres", Validaciones.errorClave("abc123"))
        assertEquals("Debe incluir letras y números", Validaciones.errorClave("sololetras"))
        assertEquals("Debe incluir letras y números", Validaciones.errorClave("12345678"))
    }
}
