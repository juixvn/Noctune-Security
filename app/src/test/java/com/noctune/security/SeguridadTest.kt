package com.noctune.security

import com.noctune.security.util.Seguridad
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SeguridadTest {
    @Test
    fun hashVerificaClaveCorrecta() {
        val sal = Seguridad.generarSal()
        val hash = Seguridad.calcularHash("Segura123", sal)
        assertTrue(Seguridad.verificar("Segura123", sal, hash))
    }

    @Test
    fun hashRechazaClaveIncorrecta() {
        val sal = Seguridad.generarSal()
        val hash = Seguridad.calcularHash("Segura123", sal)
        assertFalse(Seguridad.verificar("segura123", sal, hash))
    }

    @Test
    fun hashNoEsLaClaveYCambiaConLaSal() {
        val a = Seguridad.generarSal()
        val b = Seguridad.generarSal()
        assertNotEquals(a, b)
        assertNotEquals("Segura123", Seguridad.calcularHash("Segura123", a))
        assertNotEquals(Seguridad.calcularHash("Segura123", a), Seguridad.calcularHash("Segura123", b))
    }

    @Test
    fun codigoTieneSeisDigitos() {
        repeat(200) { assertEquals(6, Seguridad.generarCodigo().length) }
    }
}
