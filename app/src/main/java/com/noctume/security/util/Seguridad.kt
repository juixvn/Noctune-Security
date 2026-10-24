package com.noctume.security.util

import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Base64
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

object Seguridad {
    private const val ITERACIONES = 100_000

    fun generarSal(): String {
        val bytes = ByteArray(16)
        SecureRandom().nextBytes(bytes)
        return Base64.getEncoder().encodeToString(bytes)
    }

    fun calcularHash(clave: String, sal: String): String {
        val datos = PBEKeySpec(clave.toCharArray(), Base64.getDecoder().decode(sal), ITERACIONES, 256)
        val bytes = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(datos).encoded
        return Base64.getEncoder().encodeToString(bytes)
    }

    fun verificar(clave: String, sal: String, hashGuardado: String): Boolean =
        MessageDigest.isEqual(calcularHash(clave, sal).toByteArray(), hashGuardado.toByteArray())
}

object Validar {
    private val patronNombre = Regex("^[A-Za-z0-9._]{3,24}$")
    private val patronCorreo = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")

    fun nombre(valor: String): String? =
        if (patronNombre.matches(valor.trim())) null else "Usa de 3 a 24 caracteres: letras, números, punto o guion bajo"

    fun correo(valor: String): String? =
        if (patronCorreo.matches(valor.trim())) null else "Ingresa un correo válido"

    fun clave(valor: String): String? =
        if (valor.length >= 8 && valor.any { it.isLetter() } && valor.any { it.isDigit() }) null
        else "Mínimo 8 caracteres, con letras y números"
}
