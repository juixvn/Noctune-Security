package com.noctume.security.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.noctume.security.ui.components.LogoNoctume
import com.noctume.security.viewmodel.UsuarioViewModel

@Composable
private fun Cabecera(titulo: String, subtitulo: String) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        LogoNoctume(tamano = 100.dp)
        Text("NOCTUNE", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Light, letterSpacing = 8.sp)
        Text("SECURITY", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary, letterSpacing = 6.sp)
        Text(titulo, Modifier.padding(top = 20.dp), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Text(subtitulo, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
    }
}

@Composable
private fun CampoClave(valor: String, onCambio: (String) -> Unit, etiqueta: String, error: String?, ayuda: String? = null) {
    var visible by remember { mutableStateOf(false) }
    OutlinedTextField(
        value = valor,
        onValueChange = onCambio,
        label = { Text(etiqueta) },
        singleLine = true,
        leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = null) },
        trailingIcon = {
            IconButton(onClick = { visible = !visible }) {
                Icon(
                    if (visible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                    contentDescription = if (visible) "Ocultar contraseña" else "Mostrar contraseña"
                )
            }
        },
        visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        isError = error != null,
        supportingText = { (error ?: ayuda)?.let { Text(it) } },
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun BotonPrincipal(texto: String, cargando: Boolean, onClick: () -> Unit) {
    Button(onClick = onClick, enabled = !cargando, modifier = Modifier.fillMaxWidth().padding(top = 4.dp)) {
        if (cargando) {
            CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp, color = MaterialTheme.colorScheme.onPrimary)
        } else {
            Text(texto, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun LoginScreen(vm: UsuarioViewModel, onIngreso: () -> Unit, onRegistro: () -> Unit, onOlvido: () -> Unit) {
    val f by vm.form.collectAsState()
    LaunchedEffect(Unit) { vm.limpiar() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Cabecera("Iniciar sesión", "Accede para proteger tu dispositivo")
        OutlinedTextField(
            value = f.usuario,
            onValueChange = vm::onUsuario,
            label = { Text("Usuario o correo") },
            singleLine = true,
            leadingIcon = { Icon(Icons.Filled.Email, contentDescription = null) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
            modifier = Modifier.fillMaxWidth()
        )
        CampoClave(f.clave, vm::onClave, "Contraseña", null)
        f.mensaje?.let {
            Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium)
            TextButton(onClick = if (f.sinCuenta) onRegistro else onOlvido) {
                Text(if (f.sinCuenta) "Crear una cuenta" else "¿Deseas cambiar la contraseña?")
            }
        }
        TextButton(onClick = onOlvido, modifier = Modifier.align(Alignment.End)) {
            Text("¿Olvidaste tu contraseña?", style = MaterialTheme.typography.bodySmall)
        }
        BotonPrincipal("INICIAR SESIÓN", f.cargando) { vm.iniciarSesion(onIngreso) }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
            Text("¿No tienes cuenta?", color = MaterialTheme.colorScheme.onSurfaceVariant)
            TextButton(onClick = onRegistro) { Text("Regístrate", fontWeight = FontWeight.Bold) }
        }
    }
}

@Composable
fun RegistroScreen(vm: UsuarioViewModel, onListo: () -> Unit, onVolver: () -> Unit) {
    val f by vm.form.collectAsState()
    LaunchedEffect(Unit) { vm.limpiar() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        IconButton(onClick = onVolver) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver") }
        Cabecera("Crear cuenta", "Protege tu información desde hoy")
        OutlinedTextField(
            value = f.usuario,
            onValueChange = vm::onUsuario,
            label = { Text("Nombre de usuario") },
            singleLine = true,
            leadingIcon = { Icon(Icons.Filled.Person, contentDescription = null) },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            isError = f.errorUsuario != null,
            supportingText = { f.errorUsuario?.let { Text(it) } },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = f.correo,
            onValueChange = vm::onCorreo,
            label = { Text("Correo") },
            singleLine = true,
            leadingIcon = { Icon(Icons.Filled.Email, contentDescription = null) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
            isError = f.errorCorreo != null,
            supportingText = { f.errorCorreo?.let { Text(it) } },
            modifier = Modifier.fillMaxWidth()
        )
        CampoClave(f.clave, vm::onClave, "Contraseña", f.errorClave, "Mínimo 8 caracteres, con letras y números")
        BotonPrincipal("CREAR CUENTA", f.cargando) { vm.crearCuenta(onListo) }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
            Text("¿Ya tienes cuenta?", color = MaterialTheme.colorScheme.onSurfaceVariant)
            TextButton(onClick = onVolver) { Text("Inicia sesión", fontWeight = FontWeight.Bold) }
        }
    }
}

@Composable
fun RecuperarScreen(vm: UsuarioViewModel, onListo: () -> Unit, onVolver: () -> Unit) {
    val f by vm.form.collectAsState()
    LaunchedEffect(Unit) { vm.limpiar() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        IconButton(onClick = onVolver) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver") }
        Cabecera("Cambiar contraseña", "Ingresa tu correo y tu nueva contraseña")
        OutlinedTextField(
            value = f.correo,
            onValueChange = vm::onCorreo,
            label = { Text("Correo") },
            singleLine = true,
            leadingIcon = { Icon(Icons.Filled.Email, contentDescription = null) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
            isError = f.errorCorreo != null,
            supportingText = { f.errorCorreo?.let { Text(it) } },
            modifier = Modifier.fillMaxWidth()
        )
        CampoClave(f.clave, vm::onClave, "Nueva contraseña", f.errorClave, "Mínimo 8 caracteres, con letras y números")
        BotonPrincipal("CAMBIAR CONTRASEÑA", f.cargando) { vm.cambiarClave(onListo) }
    }
}
