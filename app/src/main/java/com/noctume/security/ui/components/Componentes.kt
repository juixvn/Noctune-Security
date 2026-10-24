package com.noctume.security.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.noctume.security.ui.theme.AzulNeon
import com.noctume.security.ui.theme.CianNeon
import com.noctume.security.ui.theme.Naranja
import com.noctume.security.ui.theme.Rojo
import com.noctume.security.ui.theme.Verde
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun Tarjeta(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    contenido: @Composable ColumnScope.() -> Unit
) {
    val forma = MaterialTheme.shapes.medium
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(forma)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .border(1.dp, MaterialTheme.colorScheme.outline, forma)
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
        content = contenido
    )
}

@Composable
fun LogoNoctume(tamano: Dp, modifier: Modifier = Modifier) {
    Canvas(modifier.size(tamano)) {
        scale(size.minDimension / 24f, Offset.Zero) {
            val escudo = Path().apply {
                moveTo(12f, 2f)
                lineTo(4f, 5f)
                lineTo(4f, 11f)
                cubicTo(4f, 16.55f, 7.84f, 21.74f, 12f, 23f)
                cubicTo(16.16f, 21.74f, 20f, 16.55f, 20f, 11f)
                lineTo(20f, 5f)
                close()
            }
            val ene = Path().apply {
                moveTo(8.5f, 16.5f)
                lineTo(8.5f, 7.5f)
                lineTo(15.5f, 16.5f)
                lineTo(15.5f, 7.5f)
            }
            fun trazo(ancho: Float) = Stroke(ancho, cap = StrokeCap.Round, join = StrokeJoin.Round)
            drawPath(escudo, AzulNeon.copy(alpha = 0.2f), style = trazo(2.4f))
            drawPath(ene, CianNeon.copy(alpha = 0.25f), style = trazo(3f))
            drawPath(escudo, AzulNeon, style = trazo(0.8f))
            drawPath(ene, CianNeon, style = trazo(1.6f))
            drawPath(ene, Color.White, style = trazo(0.5f))
        }
    }
}

@Composable
fun AnilloProgreso(valor: Int, tamano: Dp, color: Color, modifier: Modifier = Modifier) {
    val progreso = remember { Animatable(0f) }
    LaunchedEffect(valor) { progreso.animateTo(valor / 100f, tween(1000)) }
    Box(modifier.size(tamano), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(
            progress = { progreso.value },
            modifier = Modifier.fillMaxSize(),
            color = color,
            trackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
            strokeWidth = 8.dp
        )
        Text("$valor%", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
    }
}

fun colorPorcentaje(valor: Int): Color = when {
    valor >= 75 -> Verde
    valor >= 40 -> Naranja
    else -> Rojo
}

fun iconoCategoria(nombre: String): ImageVector = when (nombre) {
    "Sistema y actualizaciones" -> Icons.Filled.SystemUpdate
    "Copia de seguridad" -> Icons.Filled.Backup
    "Autenticación" -> Icons.Filled.VerifiedUser
    "Recuperación de cuenta" -> Icons.Filled.Person
    "Protección de datos" -> Icons.Filled.Lock
    else -> Icons.Filled.Wifi
}

fun formatoFecha(ms: Long): String =
    SimpleDateFormat("dd MMM yyyy - hh:mm a", Locale("es", "CL")).format(Date(ms))
