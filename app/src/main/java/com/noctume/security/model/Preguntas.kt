package com.noctume.security.model

data class Pregunta(
    val categoria: String,
    val texto: String,
    val titulo: String,
    val detalle: String
)

object Preguntas {
    val categorias = listOf(
        "Sistema y actualizaciones",
        "Copia de seguridad",
        "Autenticación",
        "Recuperación de cuenta",
        "Protección de datos",
        "Red y conexiones"
    )

    val lista = listOf(
        Pregunta("Sistema y actualizaciones", "¿Mantienes tu sistema operativo y aplicaciones actualizados?",
            "Actualizar sistema", "Tu sistema no está actualizado."),
        Pregunta("Sistema y actualizaciones", "¿Tienes activadas las actualizaciones automáticas?",
            "Activar actualizaciones automáticas", "Tus aplicaciones pueden quedar con fallos de seguridad."),
        Pregunta("Copia de seguridad", "¿Tienes copias de seguridad activas de tus archivos?",
            "Copia de seguridad", "No tienes copias de seguridad activas."),
        Pregunta("Autenticación", "¿Usas verificación en dos pasos en tu correo?",
            "Autenticación en 2 pasos", "No tienes activada la verificación en dos pasos."),
        Pregunta("Autenticación", "¿Usas contraseñas distintas en tus cuentas importantes?",
            "Contraseñas únicas", "Repetir contraseñas pone en riesgo todas tus cuentas."),
        Pregunta("Recuperación de cuenta", "¿Tienes actualizados tu correo y teléfono de recuperación?",
            "Recuperación de cuenta", "No has configurado opciones de recuperación."),
        Pregunta("Protección de datos", "¿Revisas los permisos que tienen tus aplicaciones?",
            "Protección de datos", "Revisa los permisos y datos sensibles."),
        Pregunta("Red y conexiones", "¿Evitas conectarte a redes Wi-Fi públicas sin protección?",
            "Red y conexiones", "Evita redes públicas o usa una conexión segura.")
    )

    fun porcentaje(respuestas: String, categoria: String): Int {
        val indices = lista.indices.filter { lista[it].categoria == categoria }
        if (respuestas.length < lista.size || indices.isEmpty()) return 0
        return indices.count { respuestas[it] == '1' } * 100 / indices.size
    }

    fun nivel(puntaje: Int): String = when {
        puntaje >= 75 -> "Alto"
        puntaje >= 40 -> "Medio"
        else -> "Bajo"
    }

    fun mensaje(puntaje: Int): String = when {
        puntaje >= 75 -> "Tu dispositivo tiene una buena protección."
        puntaje >= 40 -> "Tu dispositivo necesita algunas mejoras."
        else -> "Tu dispositivo necesita mejoras importantes."
    }
}
