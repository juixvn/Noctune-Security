package com.noctume.security.ui.screens

import android.net.Uri
import android.widget.Toast
import java.io.File
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.noctume.security.util.ImagenPerfil
import com.noctume.security.viewmodel.UsuarioViewModel

@Composable
fun PerfilScreen(
    onBack: () -> Unit,
    vm: UsuarioViewModel = viewModel()
) {
    val usuario by vm.usuario.collectAsState()
    val context = LocalContext.current
    var nombre by remember(usuario?.id) { mutableStateOf(usuario?.nombre.orEmpty()) }
    var correo by remember(usuario?.id) { mutableStateOf(usuario?.correo.orEmpty()) }
    var nivel by remember(usuario?.id) { mutableStateOf(usuario?.nivel ?: "Principiante") }
    var foto by remember(usuario?.id) { mutableStateOf(usuario?.fotoPath) }
    var uriCamara by remember { mutableStateOf<Uri?>(null) }
    val galeria = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) foto = ImagenPerfil.guardar(context, uri) ?: foto
    }
    val camara = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { ok ->
        if (ok) foto = uriCamara?.let { ImagenPerfil.guardar(context, it) } ?: foto
    }
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        TextButton(onClick = onBack) { Text("Volver") }
        Text("Mi perfil", style = MaterialTheme.typography.headlineMedium)
        AsyncImage(
            model = foto?.let(::File),
            contentDescription = "Foto de perfil",
            contentScale = ContentScale.Crop,
            modifier = Modifier.size(112.dp).clip(CircleShape).align(Alignment.CenterHorizontally)
        )
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(onClick = { galeria.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) }, modifier = Modifier.weight(1f)) { Text("Galería") }
            OutlinedButton(onClick = { runCatching { ImagenPerfil.uriTemporal(context).also { uriCamara = it; camara.launch(it) } }.onFailure { Toast.makeText(context, "No se pudo abrir la cámara", Toast.LENGTH_SHORT).show() } }, modifier = Modifier.weight(1f)) { Text("Cámara") }
        }
        OutlinedTextField(nombre, { nombre = it }, label = { Text("Nombre de usuario") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(correo, { correo = it }, label = { Text("Correo") }, modifier = Modifier.fillMaxWidth())
        Text("Nivel de experiencia digital")
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            listOf("Principiante", "Intermedio", "Avanzado").forEach { opcion -> FilterChip(nivel == opcion, { nivel = opcion }, label = { Text(opcion) }) }
        }
        Button(onClick = { vm.guardarPerfil(nombre, correo, nivel, foto); onBack() }, modifier = Modifier.fillMaxWidth()) { Text("Guardar perfil") }
    }
}
