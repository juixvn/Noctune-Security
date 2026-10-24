package com.noctume.security.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

val AzulNeon = Color(0xFF1E88FF)
val CianNeon = Color(0xFF29C7FF)
val Verde = Color(0xFF3DDC84)
val Naranja = Color(0xFFFFA726)
val Rojo = Color(0xFFFF5252)

private val Esquema = darkColorScheme(
    primary = AzulNeon,
    onPrimary = Color.White,
    secondary = CianNeon,
    tertiary = Naranja,
    error = Rojo,
    background = Color(0xFF02050C),
    onBackground = Color.White,
    surface = Color(0xFF0A1220),
    onSurface = Color.White,
    surfaceVariant = Color(0xFF0B1528),
    onSurfaceVariant = Color(0xFF9FB3D9),
    outline = Color(0xFF1F4E8C)
)

@Composable
fun NoctumeTheme(contenido: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = Esquema,
        shapes = Shapes(
            small = RoundedCornerShape(10.dp),
            medium = RoundedCornerShape(14.dp),
            large = RoundedCornerShape(22.dp)
        ),
        content = contenido
    )
}
