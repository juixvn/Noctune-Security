package com.noctume.security.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.noctume.security.model.Preguntas
import com.noctume.security.ui.components.LogoNoctume
import com.noctume.security.ui.components.Tarjeta
import com.noctume.security.ui.components.colorPorcentaje
import com.noctume.security.ui.components.iconoCategoria
import com.noctume.security.viewmodel.SeguridadViewModel

@Composable
fun InicioScreen(
    vm: SeguridadViewModel,
    onDispositivos: () -> Unit,
    onAprendizaje: () -> Unit
) {
    val evaluaciones by vm.evaluacionesDispositivoActivo.collectAsState()
    val ultima = evaluaciones.firstOrNull()

    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Resumen de seguridad", style = MaterialTheme.typography.titleMedium)
        Tarjeta {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        "Dispositivo de evaluación",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.secondary,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        "Administra los dispositivos ficticios de tus evaluaciones.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                LogoNoctume(tamano = 84.dp)
            }
            OutlinedButton(onClick = onDispositivos, modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
                Text("GESTIONAR DISPOSITIVOS DE EVALUACIÓN", fontWeight = FontWeight.Bold)
            }
            OutlinedButton(onClick = onAprendizaje, modifier = Modifier.fillMaxWidth()) {
                Text("APRENDER SOBRE SEGURIDAD DIGITAL", fontWeight = FontWeight.Bold)
            }
        }

        Text("Estado de seguridad", Modifier.padding(top = 8.dp), style = MaterialTheme.typography.titleMedium)
        Preguntas.categorias.forEach { categoria ->
            val porcentaje = if (ultima == null) 0 else Preguntas.porcentaje(ultima.respuestas, categoria)
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                Icon(iconoCategoria(categoria), contentDescription = null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(32.dp))
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(categoria, style = MaterialTheme.typography.bodyMedium)
                        Text("$porcentaje%", style = MaterialTheme.typography.bodyMedium, color = colorPorcentaje(porcentaje))
                    }
                    LinearProgressIndicator(
                        progress = { porcentaje / 100f },
                        modifier = Modifier.fillMaxWidth(),
                        color = colorPorcentaje(porcentaje),
                        trackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                    )
                }
            }
        }
    }
}
