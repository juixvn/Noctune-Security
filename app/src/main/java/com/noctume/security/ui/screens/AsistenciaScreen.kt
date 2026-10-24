package com.noctume.security.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.noctume.security.model.Preguntas
import com.noctume.security.ui.components.Tarjeta
import com.noctume.security.viewmodel.SeguridadViewModel

@Composable
fun AsistenciaScreen(onBack: () -> Unit, vm: SeguridadViewModel = viewModel()) {
    val solicitudes by vm.solicitudes.collectAsState()
    val categoria by vm.categoriaSolicitud.collectAsState()
    val descripcion by vm.descripcionSolicitud.collectAsState()
    val error by vm.errorSolicitud.collectAsState()
    val categorias = listOf("General") + Preguntas.categorias

    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver") }
            Text("Ayuda y solicitudes", style = MaterialTheme.typography.headlineSmall)
        }
        Text("Envía una solicitud ficticia de asistencia y consulta su estado.", style = MaterialTheme.typography.bodyMedium)
        SelectorCategorias(categorias, categoria, vm::cambiarCategoriaSolicitud)
        OutlinedTextField(
            value = descripcion,
            onValueChange = vm::cambiarDescripcionSolicitud,
            label = { Text("Describe tu consulta") },
            minLines = 3,
            isError = error != null,
            supportingText = { error?.let { Text(it) } },
            modifier = Modifier.fillMaxWidth()
        )
        Button(onClick = vm::enviarSolicitud, modifier = Modifier.fillMaxWidth()) { Text("Enviar solicitud") }
        Text("Mis solicitudes", style = MaterialTheme.typography.titleMedium)
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(solicitudes, key = { it.id }) { solicitud ->
                Tarjeta {
                    Text(solicitud.categoria, style = MaterialTheme.typography.titleSmall)
                    Text(solicitud.descripcion)
                    Text("Estado: ${solicitud.estado}", color = MaterialTheme.colorScheme.primary)
                    TextButton(onClick = { vm.avanzarEstadoSolicitud(solicitud) }) { Text("Actualizar estado simulado") }
                }
            }
        }
    }
}

@Composable
private fun SelectorCategorias(
    categorias: List<String>, actual: String, onChange: (String) -> Unit
) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        items(categorias) { valor -> FilterChip(selected = actual == valor, onClick = { onChange(valor) }, label = { Text(valor) }) }
    }
}
