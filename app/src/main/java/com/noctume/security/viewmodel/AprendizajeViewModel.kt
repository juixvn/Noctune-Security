package com.noctume.security.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.noctume.security.data.ClienteContenidoDemo
import com.noctume.security.data.PublicacionDemo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AprendizajeViewModel : ViewModel() {
    private val _publicaciones = MutableStateFlow<List<PublicacionDemo>>(emptyList())
    val publicaciones = _publicaciones.asStateFlow()
    private val _cargando = MutableStateFlow(false)
    val cargando = _cargando.asStateFlow()
    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    fun cargar() {
        if (_cargando.value) return
        viewModelScope.launch {
            _cargando.value = true
            _error.value = null
            try {
                _publicaciones.value = withContext(Dispatchers.IO) { ClienteContenidoDemo.servicio.publicaciones() }
            } catch (_: Exception) {
                _error.value = "No se pudo conectar al servicio de demostración. Revisa tu conexión."
            } finally {
                _cargando.value = false
            }
        }
    }
}
