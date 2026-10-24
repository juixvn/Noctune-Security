package com.noctume.security.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import com.noctume.security.model.Preguntas
import com.noctume.security.ui.components.AnilloProgreso
import com.noctume.security.ui.components.Tarjeta
import com.noctume.security.ui.components.colorPorcentaje
import com.noctume.security.ui.components.formatoFecha
import com.noctume.security.viewmodel.SeguridadViewModel

@Composable
fun HistorialScreen(vm: SeguridadViewModel, onReevaluar: () -> Unit) {
    val context = LocalContext.current
    val evaluaciones by vm.evaluacionesDispositivoActivo.collectAsState()
    val ultima = evaluaciones.firstOrNull()
    var preguntarReevaluar by remember { mutableStateOf(false) }
    val crearRespaldo = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/json")) { uri ->
        val evaluacion = ultima
        if (uri != null && evaluacion != null) {
            val json = """{"fecha":${evaluacion.fecha},"puntaje":${evaluacion.puntaje},"respuestas":"${evaluacion.respuestas}"}"""
            val guardado = runCatching {
                context.contentResolver.openOutputStream(uri)?.use { it.write(json.toByteArray()) } ?: error("No se pudo abrir el archivo")
            }.isSuccess
            if (guardado) {
                if (evaluacion.hechas.getOrNull(2) != '1') vm.alternarHecha(evaluacion, 2)
                Toast.makeText(context, "Respaldo creado", Toast.LENGTH_SHORT).show()
            } else Toast.makeText(context, "No se pudo crear el respaldo", Toast.LENGTH_SHORT).show()
        }
    }

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item { Text("Resultados", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold) }

        if (ultima == null) {
            item {
                Text(
                    "Este dispositivo todavía no tiene análisis. Realiza una evaluación desde la pestaña Análisis para ver sus resultados y recomendaciones.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            item {
                Button(onClick = onReevaluar, modifier = Modifier.fillMaxWidth()) {
                    Text("Realizar evaluación")
                }
            }
        } else {
            item {
                Tarjeta {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(18.dp)) {
                        AnilloProgreso(ultima.puntaje, 110.dp, colorPorcentaje(ultima.puntaje))
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("Nivel de seguridad", style = MaterialTheme.typography.titleMedium)
                            Text(
                                Preguntas.nivel(ultima.puntaje),
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = colorPorcentaje(ultima.puntaje)
                            )
                            Text(
                                Preguntas.mensaje(ultima.puntaje),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            item { Text("Recomendaciones", Modifier.padding(top = 8.dp), style = MaterialTheme.typography.titleMedium) }
            val pendientes = Preguntas.lista.indices.filter { ultima.respuestas.getOrNull(it) == '0' }
            val completadas = pendientes.count { ultima.hechas.getOrNull(it) == '1' }
            val avance = if (pendientes.isEmpty()) 1f else completadas.toFloat() / pendientes.size
            item {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Progreso: $completadas de ${pendientes.size} acciones (${(avance * 100).toInt()}%)")
                    LinearProgressIndicator(progress = { avance }, modifier = Modifier.fillMaxWidth())
                }
            }
            if (pendientes.isEmpty()) {
                item { Text("¡Excelente! No tienes recomendaciones pendientes.", color = MaterialTheme.colorScheme.onSurfaceVariant) }
            }
            pendientes.forEach { i ->
                val pregunta = Preguntas.lista[i]
                val hecha = ultima.hechas.getOrNull(i) == '1'
                item {
                    Tarjeta {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                Text(
                                    pregunta.titulo,
                                    fontWeight = FontWeight.SemiBold,
                                    textDecoration = if (hecha) TextDecoration.LineThrough else null
                                )
                            Text(
                                pregunta.detalle,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Checkbox(
                                checked = hecha,
                                onCheckedChange = {
                                    vm.alternarHecha(ultima, i)
                                    if (!hecha) preguntarReevaluar = true
                                }
                            )
                        }
                        OutlinedButton(
                            onClick = {
                                if (i == 2) crearRespaldo.launch("noctune_respaldo_${System.currentTimeMillis()}.json")
                                else abrirAccionRecomendada(context, i)
                            },
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                        ) { Text(etiquetaAccion(i)) }
                    }
                }
            }

            item { Text("Historial de análisis", Modifier.padding(top = 8.dp), style = MaterialTheme.typography.titleMedium) }
            evaluaciones.forEach { evaluacion ->
                item {
                    Tarjeta {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Icon(Icons.Filled.History, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                            Text(formatoFecha(evaluacion.fecha), Modifier.weight(1f))
                            Text(
                                "${evaluacion.puntaje}%",
                                fontWeight = FontWeight.Bold,
                                color = colorPorcentaje(evaluacion.puntaje)
                            )
                        }
                    }
                }
            }
        }
    }

    if (preguntarReevaluar) {
        AlertDialog(
            onDismissRequest = { preguntarReevaluar = false },
            title = { Text("¡Buen avance!") },
            text = { Text("¿Quieres volver a evaluar tu seguridad para ver tu nuevo nivel?") },
            confirmButton = {
                Button(onClick = {
                    preguntarReevaluar = false
                    onReevaluar()
                }) { Text("Evaluar ahora") }
            },
            dismissButton = { TextButton(onClick = { preguntarReevaluar = false }) { Text("Más tarde") } }
        )
    }
}

private fun etiquetaAccion(indice: Int): String = when (indice) {
    0 -> "Ir a actualizar el dispositivo"
    1 -> "Abrir ajustes de actualización"
    2 -> "Configurar copia de seguridad"
    3, 4, 5 -> "Abrir seguridad de la cuenta"
    6 -> "Revisar permisos de aplicaciones"
    else -> "Abrir ajustes de Wi-Fi"
}

private fun abrirAccionRecomendada(context: Context, indice: Int) {
    val intent = when (indice) {
    // No existe una acción pública y compatible para actualizar el sistema en todos los Android.
    // Abrimos Ajustes como destino seguro; el usuario puede localizar allí la actualización.
    0 -> Intent(Settings.ACTION_SETTINGS)
        1 -> Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store"))
        2 -> Intent(Settings.ACTION_PRIVACY_SETTINGS)
        3, 4, 5 -> Intent(Intent.ACTION_VIEW, Uri.parse("https://myaccount.google.com/security"))
        6 -> Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", context.packageName, null)
        }
        else -> Intent(Settings.ACTION_WIFI_SETTINGS)
    }
    val abierto = runCatching { context.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)) }.isSuccess
    if (!abierto) {
        Toast.makeText(context, "No se pudo abrir esta opción en el dispositivo", Toast.LENGTH_SHORT).show()
    }
}
