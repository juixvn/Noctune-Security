package com.noctume.security.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.noctume.security.data.NoctumeDatabase
import com.noctume.security.data.Usuario
import com.noctume.security.notifications.Recordatorio
import com.noctume.security.util.Seguridad
import com.noctume.security.util.Validar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class FormularioUi(
    val usuario: String = "",
    val correo: String = "",
    val clave: String = "",
    val errorUsuario: String? = null,
    val errorCorreo: String? = null,
    val errorClave: String? = null,
    val mensaje: String? = null,
    val sinCuenta: Boolean = false,
    val cargando: Boolean = false
)

class UsuarioViewModel(app: Application) : AndroidViewModel(app) {
    private val dao = NoctumeDatabase.obtener(app).dao()

    val usuario: StateFlow<Usuario?> =
        dao.usuarioActivo().stateIn(viewModelScope, SharingStarted.Eagerly, null)

    private val _form = MutableStateFlow(FormularioUi())
    val form: StateFlow<FormularioUi> = _form.asStateFlow()

    fun guardarPerfil(nombre: String, correo: String, nivel: String, fotoPath: String? = usuario.value?.fotoPath) {
        val actual = usuario.value ?: return
        if (Validar.nombre(nombre) != null || Validar.correo(correo) != null) return
        viewModelScope.launch {
            dao.actualizarUsuario(actual.copy(nombre = nombre.trim(), correo = correo.trim().lowercase(), nivel = nivel, fotoPath = fotoPath))
        }
    }

    fun limpiar() {
        _form.value = FormularioUi()
    }

    fun onUsuario(valor: String) {
        _form.update { it.copy(usuario = valor, errorUsuario = null, mensaje = null, sinCuenta = false) }
    }

    fun onCorreo(valor: String) {
        _form.update { it.copy(correo = valor, errorCorreo = null, mensaje = null) }
    }

    fun onClave(valor: String) {
        _form.update { it.copy(clave = valor, errorClave = null, mensaje = null) }
    }

    fun iniciarSesion(onListo: () -> Unit) {
        val f = _form.value
        if (f.cargando) return
        if (f.usuario.isBlank() || f.clave.isEmpty()) {
            _form.update { it.copy(mensaje = "Ingresa tu usuario y contraseña") }
            return
        }
        _form.update { it.copy(cargando = true) }
        viewModelScope.launch {
            val encontrado = dao.buscar(f.usuario.trim())
            if (encontrado == null) {
                _form.update {
                    it.copy(cargando = false, sinCuenta = true, mensaje = "Los datos ingresados no existen. Debes crear una cuenta.")
                }
                return@launch
            }
            val correcta = withContext(Dispatchers.Default) {
                Seguridad.verificar(f.clave, encontrado.sal, encontrado.claveHash)
            }
            if (!correcta) {
                _form.update { it.copy(cargando = false, mensaje = "Usuario o contraseña incorrectos.") }
                return@launch
            }
            dao.cerrarSesiones()
            dao.abrirSesion(encontrado.id)
            if (encontrado.recordatorio) Recordatorio.activar(getApplication())
            limpiar()
            onListo()
        }
    }

    fun crearCuenta(onListo: () -> Unit) {
        val f = _form.value
        if (f.cargando) return
        val revisado = f.copy(
            errorUsuario = Validar.nombre(f.usuario),
            errorCorreo = Validar.correo(f.correo),
            errorClave = Validar.clave(f.clave)
        )
        if (revisado.errorUsuario != null || revisado.errorCorreo != null || revisado.errorClave != null) {
            _form.value = revisado
            return
        }
        _form.update { it.copy(cargando = true) }
        viewModelScope.launch {
            val usuarioRepetido = dao.buscar(f.usuario.trim()) != null
            val correoRepetido = dao.buscar(f.correo.trim()) != null
            if (usuarioRepetido || correoRepetido) {
                _form.update {
                    it.copy(
                        cargando = false,
                        errorUsuario = if (usuarioRepetido) "Este nombre de usuario ya está en uso" else null,
                        errorCorreo = if (correoRepetido) "Este correo ya está registrado" else null
                    )
                }
                return@launch
            }
            val sal = Seguridad.generarSal()
            val hash = withContext(Dispatchers.Default) { Seguridad.calcularHash(f.clave, sal) }
            val id = dao.insertarUsuario(
                Usuario(nombre = f.usuario.trim(), correo = f.correo.trim().lowercase(), claveHash = hash, sal = sal)
            )
            dao.cerrarSesiones()
            dao.abrirSesion(id.toInt())
            limpiar()
            onListo()
        }
    }

    fun cambiarClave(onListo: () -> Unit) {
        val f = _form.value
        if (f.cargando) return
        val errorCorreo = Validar.correo(f.correo)
        val errorClave = Validar.clave(f.clave)
        if (errorCorreo != null || errorClave != null) {
            _form.update { it.copy(errorCorreo = errorCorreo, errorClave = errorClave) }
            return
        }
        _form.update { it.copy(cargando = true) }
        viewModelScope.launch {
            val encontrado = dao.buscar(f.correo.trim())
            if (encontrado == null) {
                _form.update { it.copy(cargando = false, errorCorreo = "No existe una cuenta con este correo") }
                return@launch
            }
            val sal = Seguridad.generarSal()
            val hash = withContext(Dispatchers.Default) { Seguridad.calcularHash(f.clave, sal) }
            dao.actualizarUsuario(encontrado.copy(claveHash = hash, sal = sal))
            limpiar()
            onListo()
        }
    }

    fun activarRecordatorio(activar: Boolean) {
        val actual = usuario.value ?: return
        viewModelScope.launch {
            dao.actualizarUsuario(actual.copy(recordatorio = activar))
            if (activar) Recordatorio.activar(getApplication()) else Recordatorio.desactivar(getApplication())
        }
    }

    fun cerrarSesion(onListo: () -> Unit) {
        viewModelScope.launch {
            Recordatorio.desactivar(getApplication())
            dao.cerrarSesiones()
            limpiar()
            onListo()
        }
    }
}
