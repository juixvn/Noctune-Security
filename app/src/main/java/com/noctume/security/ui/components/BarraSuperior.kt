package com.noctume.security.ui.components

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import java.io.File
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import androidx.core.content.ContextCompat
import com.noctume.security.viewmodel.UsuarioViewModel

@Composable
fun BarraSuperior(
    vm: UsuarioViewModel,
    onPerfil: () -> Unit = {},
    onAsistencia: () -> Unit = {},
    onSesionCerrada: () -> Unit
) {
    val usuario by vm.usuario.collectAsState()
    val contexto = LocalContext.current
    var menu by remember { mutableStateOf(false) }

    val permiso = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { concedido ->
        vm.activarRecordatorio(concedido)
        val mensaje = if (concedido) "Recordatorio activado: cada 1 hora" else "Permiso de notificaciones denegado"
        Toast.makeText(contexto, mensaje, Toast.LENGTH_SHORT).show()
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(Modifier.size(48.dp))
        Text(
            text = buildAnnotatedString {
                append("Noctune ")
                withStyle(SpanStyle(color = MaterialTheme.colorScheme.primary)) { append("Security") }
            },
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold
        )
        Box {
            IconButton(onClick = { menu = true }) {
                if (usuario?.fotoPath != null) {
                    AsyncImage(
                        model = usuario?.fotoPath?.let(::File),
                        contentDescription = "Foto de perfil",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.size(34.dp).clip(CircleShape)
                    )
                } else {
                    Icon(Icons.Filled.AccountCircle, contentDescription = "Perfil", tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(32.dp))
                }
            }
            DropdownMenu(expanded = menu, onDismissRequest = { menu = false }) {
                Text(usuario?.nombre ?: "", Modifier.padding(horizontal = 16.dp, vertical = 4.dp), fontWeight = FontWeight.Bold)
                Text(
                    usuario?.correo ?: "",
                    Modifier.padding(horizontal = 16.dp, vertical = 2.dp),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                HorizontalDivider(Modifier.padding(vertical = 6.dp))
                DropdownMenuItem(text = { Text("Editar perfil") }, onClick = { menu = false; onPerfil() })
                DropdownMenuItem(
                    text = { Text("Recordatorio cada hora") },
                    trailingIcon = { Switch(checked = usuario?.recordatorio == true, onCheckedChange = null) },
                    onClick = {
                        val activar = usuario?.recordatorio != true
                        val faltaPermiso = Build.VERSION.SDK_INT >= 33 &&
                            ContextCompat.checkSelfPermission(contexto, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
                        if (activar && faltaPermiso) {
                            permiso.launch(Manifest.permission.POST_NOTIFICATIONS)
                        } else {
                            vm.activarRecordatorio(activar)
                            val mensaje = if (activar) "Recordatorio activado: cada 1 hora" else "Recordatorio desactivado"
                            Toast.makeText(contexto, mensaje, Toast.LENGTH_SHORT).show()
                        }
                    }
                )
                DropdownMenuItem(
                    text = { Text("Ayuda y soporte") },
                    onClick = { menu = false; onAsistencia() }
                )
                DropdownMenuItem(
                    text = { Text("Cerrar sesión") },
                    leadingIcon = { Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null) },
                    onClick = {
                        menu = false
                        vm.cerrarSesion(onSesionCerrada)
                    }
                )
            }
        }
    }
}
