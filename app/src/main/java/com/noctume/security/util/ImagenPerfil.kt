package com.noctume.security.util

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File

object ImagenPerfil {
    fun uriTemporal(context: Context): Uri {
        val dir = File(context.cacheDir, "perfil").apply { mkdirs() }
        val file = File(dir, "captura_${System.currentTimeMillis()}.jpg")
        return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
    }
    fun guardar(context: Context, uri: Uri): String? = runCatching {
        val destino = File(context.filesDir, "perfil_${System.currentTimeMillis()}.jpg")
        context.contentResolver.openInputStream(uri)!!.use { entrada -> destino.outputStream().use { salida -> entrada.copyTo(salida) } }
        destino.absolutePath
    }.getOrNull()
}
