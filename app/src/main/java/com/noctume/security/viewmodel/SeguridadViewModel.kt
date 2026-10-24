package com.noctume.security.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.noctume.security.data.Evaluacion
import com.noctume.security.data.Dispositivo
import com.noctume.security.data.Solicitud
import com.noctume.security.data.NoctumeDatabase
import com.noctume.security.model.Preguntas
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class SeguridadViewModel(app: Application) : AndroidViewModel(app) {
    private val dao = NoctumeDatabase.obtener(app).dao()

    private val usuario = dao.usuarioActivo()
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    val evaluaciones: StateFlow<List<Evaluacion>> = usuario
        .flatMapLatest { u -> if (u == null) flowOf(emptyList()) else dao.evaluaciones(u.id) }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val dispositivos: StateFlow<List<Dispositivo>> = usuario
        .flatMapLatest { u -> if (u == null) flowOf(emptyList()) else dao.dispositivos(u.id) }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val solicitudes: StateFlow<List<Solicitud>> = usuario
        .flatMapLatest { u -> if (u == null) flowOf(emptyList()) else dao.solicitudes(u.id) }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    private val _categoriaSolicitud = MutableStateFlow("General")
    val categoriaSolicitud: StateFlow<String> = _categoriaSolicitud.asStateFlow()
    private val _descripcionSolicitud = MutableStateFlow("")
    val descripcionSolicitud: StateFlow<String> = _descripcionSolicitud.asStateFlow()
    private val _errorSolicitud = MutableStateFlow<String?>(null)
    val errorSolicitud: StateFlow<String?> = _errorSolicitud.asStateFlow()

    fun cambiarCategoriaSolicitud(categoria: String) { _categoriaSolicitud.value = categoria }
    fun cambiarDescripcionSolicitud(descripcion: String) {
        _descripcionSolicitud.value = descripcion
        _errorSolicitud.value = null
    }
    fun enviarSolicitud() {
        val u = usuario.value ?: return
        val descripcion = _descripcionSolicitud.value.trim()
        if (descripcion.length < 10) {
            _errorSolicitud.value = "Describe tu consulta con al menos 10 caracteres"
            return
        }
        viewModelScope.launch {
            dao.insertarSolicitud(Solicitud(usuarioId = u.id, categoria = _categoriaSolicitud.value, descripcion = descripcion, fecha = System.currentTimeMillis()))
            _descripcionSolicitud.value = ""
            _errorSolicitud.value = null
        }
    }

    fun avanzarEstadoSolicitud(solicitud: Solicitud) {
        val estados = listOf("Recibida", "En revisión", "Resuelta")
        val siguiente = estados[(estados.indexOf(solicitud.estado).coerceAtLeast(0) + 1) % estados.size]
        viewModelScope.launch { dao.actualizarSolicitud(solicitud.copy(estado = siguiente)) }
    }

    // Las estadísticas y recomendaciones corresponden únicamente al dispositivo activo.
    val evaluacionesDispositivoActivo: StateFlow<List<Evaluacion>> = combine(evaluaciones, dispositivos) { lista, equipos ->
        val principal = equipos.minByOrNull { it.fecha }?.id
        val activo = equipos.firstOrNull { it.seleccionado }?.id ?: principal
        when {
            activo != null -> lista.filter {
                it.dispositivoId == activo || (it.dispositivoId == null && activo == principal)
            }
            else -> lista.filter { it.dispositivoId == null }
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    fun agregarDispositivo(nombre: String, sistema: String) {
        val u = usuario.value ?: return
        if (nombre.trim().length < 3) return
        viewModelScope.launch {
            dao.insertarDispositivo(Dispositivo(usuarioId = u.id, nombre = nombre.trim(), sistema = sistema, fecha = System.currentTimeMillis(), seleccionado = dispositivos.value.none { it.seleccionado }))
        }
    }

    fun seleccionarDispositivo(id: Int) {
        val usuarioId = usuario.value?.id ?: return
        if (dispositivos.value.none { it.id == id }) return
        viewModelScope.launch {
            dao.establecerDispositivoActivo(usuarioId, id)
        }
    }

    fun eliminarDispositivo(id: Int) {
        val usuarioId = usuario.value?.id ?: return
        val eraActivo = dispositivos.value.firstOrNull { it.id == id }?.seleccionado == true
        val siguiente = dispositivos.value.firstOrNull { it.id != id }
        viewModelScope.launch {
            dao.eliminarDispositivo(id)
            if (eraActivo && siguiente != null) dao.establecerDispositivoActivo(usuarioId, siguiente.id)
        }
    }

    private val _indice = MutableStateFlow(0)
    val indice: StateFlow<Int> = _indice.asStateFlow()

    private val respuestas = mutableListOf<Boolean>()
    private var guardando = false

    fun iniciarAnalisis() {
        respuestas.clear()
        guardando = false
        _indice.value = 0
    }

    fun responder(valor: Boolean, onTerminar: () -> Unit) {
        if (guardando) return
        respuestas.add(valor)
        if (respuestas.size < Preguntas.lista.size) {
            _indice.value = respuestas.size
            return
        }
        val u = usuario.value ?: return
        guardando = true
        val puntaje = respuestas.count { it } * 100 / respuestas.size
        val texto = respuestas.joinToString("") { if (it) "1" else "0" }
        viewModelScope.launch {
            dao.insertarEvaluacion(
                Evaluacion(
                    usuarioId = u.id,
                    fecha = System.currentTimeMillis(),
                    puntaje = puntaje,
                    respuestas = texto,
                    hechas = "0".repeat(texto.length),
                    dispositivoId = dispositivos.value.firstOrNull { it.seleccionado }?.id
                        ?: dispositivos.value.minByOrNull { it.fecha }?.id
                )
            )
            onTerminar()
        }
    }

    fun alternarHecha(evaluacion: Evaluacion, posicion: Int) {
        if (posicion !in Preguntas.lista.indices) return
        val marcas = evaluacion.hechas.padEnd(Preguntas.lista.size, '0')
            .take(Preguntas.lista.size)
            .toCharArray()
        marcas[posicion] = if (marcas[posicion] == '1') '0' else '1'
        viewModelScope.launch {
            dao.actualizarEvaluacion(evaluacion.copy(hechas = String(marcas)))
        }
    }
}
