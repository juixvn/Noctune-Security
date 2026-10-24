package com.noctume.security.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.noctume.security.ui.components.BarraSuperior
import com.noctume.security.ui.screens.AnalisisScreen
import com.noctume.security.ui.screens.DispositivosScreen
import com.noctume.security.ui.screens.PerfilScreen
import com.noctume.security.ui.screens.AsistenciaScreen
import com.noctume.security.ui.screens.AprendizajeScreen
import com.noctume.security.ui.screens.HistorialScreen
import com.noctume.security.ui.screens.InicioScreen
import com.noctume.security.ui.screens.LoginScreen
import com.noctume.security.ui.screens.RecuperarScreen
import com.noctume.security.ui.screens.RegistroScreen
import com.noctume.security.ui.screens.SplashScreen
import com.noctume.security.viewmodel.SeguridadViewModel
import com.noctume.security.viewmodel.UsuarioViewModel

sealed class Screen(val ruta: String) {
    data object Splash : Screen("splash")
    data object Login : Screen("login")
    data object Registro : Screen("registro")
    data object Recuperar : Screen("recuperar")
    data object Inicio : Screen("inicio")
    data object Analisis : Screen("analisis")
    data object Historial : Screen("historial")
    data object Dispositivos : Screen("dispositivos")
    data object Perfil : Screen("perfil")
    data object Asistencia : Screen("asistencia")
    data object Aprendizaje : Screen("aprendizaje")
}

private data class ItemMenu(val destino: Screen, val titulo: String, val icono: ImageVector)

private val itemsMenu = listOf(
    ItemMenu(Screen.Inicio, "Inicio", Icons.Filled.Home),
    ItemMenu(Screen.Analisis, "Análisis", Icons.Filled.Assessment),
    ItemMenu(Screen.Historial, "Historial", Icons.Filled.History)
)

@Composable
fun AppNavigation() {
    val nav = rememberNavController()
    val usuarioVm: UsuarioViewModel = viewModel()
    val seguridadVm: SeguridadViewModel = viewModel()

    val entrada by nav.currentBackStackEntryAsState()
    val rutaActual = entrada?.destination?.route
    val enMenu = itemsMenu.any { it.destino.ruta == rutaActual } ||
        rutaActual == Screen.Dispositivos.ruta || rutaActual == Screen.Perfil.ruta ||
        rutaActual == Screen.Asistencia.ruta || rutaActual == Screen.Aprendizaje.ruta
    val rutaSeleccionada = when (rutaActual) {
        Screen.Dispositivos.ruta, Screen.Perfil.ruta, Screen.Asistencia.ruta, Screen.Aprendizaje.ruta -> Screen.Inicio.ruta
        else -> rutaActual
    }

    fun irA(destino: Screen) {
        nav.navigate(destino.ruta) {
            popUpTo(Screen.Inicio.ruta) { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    }

    fun entrar() {
        nav.navigate(Screen.Inicio.ruta) { popUpTo(nav.graph.id) { inclusive = true } }
    }

    Scaffold(
        topBar = {
            if (enMenu) {
                BarraSuperior(
                    vm = usuarioVm,
                    onPerfil = { nav.navigate(Screen.Perfil.ruta) },
                    onAsistencia = { nav.navigate(Screen.Asistencia.ruta) },
                    onSesionCerrada = {
                        nav.navigate(Screen.Login.ruta) { popUpTo(nav.graph.id) { inclusive = true } }
                    }
                )
            }
        },
        bottomBar = {
            if (enMenu) {
                NavigationBar {
                    itemsMenu.forEach { item ->
                        NavigationBarItem(
                            selected = rutaSeleccionada == item.destino.ruta,
                            onClick = { irA(item.destino) },
                            icon = { Icon(item.icono, contentDescription = item.titulo) },
                            label = { Text(item.titulo) }
                        )
                    }
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = nav,
            startDestination = Screen.Splash.ruta,
            modifier = Modifier.padding(padding)
        ) {
            composable(Screen.Splash.ruta) {
                SplashScreen {
                    val destino = if (usuarioVm.usuario.value != null) Screen.Inicio else Screen.Login
                    nav.navigate(destino.ruta) { popUpTo(Screen.Splash.ruta) { inclusive = true } }
                }
            }
            composable(Screen.Login.ruta) {
                LoginScreen(
                    vm = usuarioVm,
                    onIngreso = { entrar() },
                    onRegistro = { nav.navigate(Screen.Registro.ruta) },
                    onOlvido = { nav.navigate(Screen.Recuperar.ruta) }
                )
            }
            composable(Screen.Registro.ruta) {
                RegistroScreen(usuarioVm, onListo = { entrar() }, onVolver = { nav.popBackStack() })
            }
            composable(Screen.Recuperar.ruta) {
                RecuperarScreen(usuarioVm, onListo = { nav.popBackStack() }, onVolver = { nav.popBackStack() })
            }
            composable(Screen.Inicio.ruta) {
                InicioScreen(
                    seguridadVm,
                    onDispositivos = { nav.navigate(Screen.Dispositivos.ruta) },
                    onAprendizaje = { nav.navigate(Screen.Aprendizaje.ruta) }
                )
            }
            composable(Screen.Dispositivos.ruta) { DispositivosScreen(onBack = { nav.popBackStack() }, vm = seguridadVm) }
            composable(Screen.Perfil.ruta) {
                PerfilScreen(
                    onBack = { nav.popBackStack() },
                    vm = usuarioVm
                )
            }
            composable(Screen.Asistencia.ruta) { AsistenciaScreen(onBack = { nav.popBackStack() }, vm = seguridadVm) }
            composable(Screen.Aprendizaje.ruta) { AprendizajeScreen(onBack = { nav.popBackStack() }) }
            composable(Screen.Analisis.ruta) {
                AnalisisScreen(seguridadVm, onTerminar = { irA(Screen.Historial) })
            }
            composable(Screen.Historial.ruta) {
                HistorialScreen(seguridadVm, onReevaluar = { irA(Screen.Analisis) })
            }
        }
    }
}
