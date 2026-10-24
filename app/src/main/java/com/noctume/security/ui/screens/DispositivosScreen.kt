package com.noctume.security.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.noctume.security.viewmodel.SeguridadViewModel

@Composable
fun DispositivosScreen(onBack: () -> Unit, vm: SeguridadViewModel = viewModel()) {
    val dispositivos by vm.dispositivos.collectAsState()
    var nombre by remember { mutableStateOf("") }
    var sistema by remember { mutableStateOf("Android") }
    var error by remember { mutableStateOf<String?>(null) }
    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver al inicio")
            }
            Text("Dispositivos de evaluación", style = MaterialTheme.typography.headlineSmall)
        }
        Text("Registra dispositivos ficticios para asociarlos a tus evaluaciones.", style = MaterialTheme.typography.bodyMedium)
        OutlinedTextField(nombre, { nombre = it; error = null }, label = { Text("Nombre o alias") }, isError = error != null, supportingText = { error?.let { Text(it) } }, modifier = Modifier.fillMaxWidth())
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("Android", "iOS").forEach { opcion ->
                FilterChip(selected = sistema == opcion, onClick = { sistema = opcion }, label = { Text(opcion) })
            }
        }
        Button(onClick = {
            if (nombre.trim().length < 3) error = "Escribe al menos 3 caracteres"
            else { vm.agregarDispositivo(nombre, sistema); nombre = "" }
        }, modifier = Modifier.fillMaxWidth()) { Text("Agregar dispositivo") }
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(dispositivos, key = { it.id }) { dispositivo ->
                Row(Modifier.fillMaxWidth().padding(vertical = 8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column(Modifier.weight(1f)) {
                        Text(dispositivo.nombre, style = MaterialTheme.typography.titleMedium)
                        Text(dispositivo.sistema, style = MaterialTheme.typography.bodySmall)
                        if (dispositivo.seleccionado) Text("Seleccionado", color = MaterialTheme.colorScheme.primary)
                    }
                    if (!dispositivo.seleccionado) Button(onClick = { vm.seleccionarDispositivo(dispositivo.id) }) { Text("Usar") }
                    IconButton(onClick = { vm.eliminarDispositivo(dispositivo.id) }) { Text("×") }
                }
            }
        }
    }
}
