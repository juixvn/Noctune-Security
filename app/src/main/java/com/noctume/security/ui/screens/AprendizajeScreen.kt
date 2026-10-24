package com.noctume.security.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.noctume.security.ui.components.Tarjeta

private data class TemaEducativo(val titulo: String, val contenido: String)

private val temasEducativos = listOf(
    TemaEducativo(
        "La regla de respaldo 3-2-1",
        "Conserva 3 copias de tus archivos importantes: el original y 2 respaldos. Guarda esas copias en 2 tipos de medios distintos, por ejemplo, un disco externo y un servicio en la nube. Mantén 1 copia fuera de tu casa o lugar de trabajo. Así reduces el riesgo de perderlo todo por una falla, robo o incidente. Prueba de vez en cuando que puedas recuperar los archivos."
    ),
    TemaEducativo(
        "Contraseñas seguras y únicas",
        "Usa una contraseña diferente para cada cuenta. Prefiere frases largas, difíciles de adivinar y fáciles de recordar para ti. Un administrador de contraseñas puede ayudarte a guardarlas; nunca las compartas por mensajes ni las reutilices en varios servicios."
    ),
    TemaEducativo(
        "Verificación en dos pasos",
        "Actívala en el correo, redes sociales y cuentas importantes. Añade una segunda prueba de identidad —como una aplicación autenticadora o una llave de seguridad— para que una contraseña robada no sea suficiente para entrar. Guarda los códigos de recuperación en un lugar seguro."
    ),
    TemaEducativo(
        "Detecta mensajes y enlaces sospechosos",
        "Desconfía de mensajes urgentes que pidan claves, códigos o pagos. Revisa con cuidado la dirección del remitente y el dominio del enlace. Si tienes dudas, no abras el enlace: entra al servicio escribiendo su dirección oficial o contacta a la organización por un canal conocido."
    ),
    TemaEducativo(
        "Actualiza tus dispositivos",
        "Instala las actualizaciones del sistema y de las aplicaciones apenas estén disponibles. Muchas corrigen fallos que pueden ser aprovechados para atacar el dispositivo. Descarga aplicaciones desde tiendas oficiales y elimina las que ya no uses."
    ),
    TemaEducativo(
        "Cuida tus conexiones Wi-Fi",
        "Protege tu red doméstica con una contraseña robusta y un método de seguridad moderno. En redes públicas evita operaciones sensibles; si debes acceder a una cuenta, confirma que el sitio use HTTPS y no compartas información confidencial con redes desconocidas."
    ),
    TemaEducativo(
        "Revisa privacidad y permisos",
        "Concede a cada aplicación solo los permisos que necesita. Revisa periódicamente el acceso a ubicación, cámara, micrófono y contactos. Limita la información personal visible públicamente y elimina permisos de aplicaciones que ya no utilizas."
    ),
    TemaEducativo(
        "Prepara la recuperación de tus cuentas",
        "Mantén actualizado el correo y teléfono de recuperación. Usa opciones de recuperación que controles y protege también esas cuentas con una contraseña única y verificación en dos pasos. Nunca compartas códigos de acceso o recuperación con otra persona."
    )
)

@Composable
fun AprendizajeScreen(onBack: () -> Unit) {
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        androidx.compose.foundation.layout.Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver") }
            Text("Aprende sobre seguridad", style = MaterialTheme.typography.headlineSmall)
        }
        LazyColumn(contentPadding = PaddingValues(vertical = 8.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            item {
                Text(
                    "Consejos prácticos para proteger tus dispositivos, cuentas y archivos.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            items(temasEducativos) { tema ->
                Tarjeta {
                    Text(tema.titulo, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                    Text(tema.contenido, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}
