package com.noctume.security.ui.screens

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.noctume.security.model.Preguntas
import com.noctume.security.viewmodel.SeguridadViewModel

@Composable
fun AnalisisScreen(vm: SeguridadViewModel, onTerminar: () -> Unit) {
    val indice by vm.indice.collectAsState()
    val total = Preguntas.lista.size
    val avance by animateFloatAsState((indice + 1f) / total, tween(400), label = "avance")
    val pulso = rememberInfiniteTransition(label = "pulso")
    val escala by pulso.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(tween(1200, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "escala"
    )

    LaunchedEffect(Unit) { vm.iniciarAnalisis() }

    Column(
        modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()).padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Análisis de seguridad", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
        LinearProgressIndicator(
            progress = { avance },
            modifier = Modifier.fillMaxWidth(),
            trackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
        )
        Text("Pregunta ${indice + 1} de $total", color = MaterialTheme.colorScheme.secondary)
        Crossfade(targetState = indice, label = "pregunta") { i ->
            Text(
                Preguntas.lista[i].texto,
                modifier = Modifier.fillMaxWidth().height(80.dp),
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center
            )
        }
        Box(
            modifier = Modifier
                .padding(vertical = 24.dp)
                .size(120.dp)
                .scale(escala)
                .border(2.dp, MaterialTheme.colorScheme.secondary, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Filled.Security, contentDescription = null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(56.dp))
        }
        OutlinedButton(
            onClick = { vm.responder(true, onTerminar) },
            modifier = Modifier.fillMaxWidth().height(52.dp)
        ) { Text("Sí", fontWeight = FontWeight.Bold) }
        OutlinedButton(
            onClick = { vm.responder(false, onTerminar) },
            modifier = Modifier.fillMaxWidth().height(52.dp)
        ) { Text("No", fontWeight = FontWeight.Bold) }
    }
}
