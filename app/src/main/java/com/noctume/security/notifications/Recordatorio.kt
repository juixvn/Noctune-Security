package com.noctume.security.notifications

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.noctume.security.R
import java.util.concurrent.TimeUnit

object Recordatorio {
    private const val CANAL = "noctume_alertas"
    private const val TRABAJO = "noctume_recordatorio"

    fun activar(context: Context) {
        val pedido = PeriodicWorkRequestBuilder<RecordatorioWorker>(1, TimeUnit.HOURS).build()
        WorkManager.getInstance(context)
            .enqueueUniquePeriodicWork(TRABAJO, ExistingPeriodicWorkPolicy.KEEP, pedido)
    }

    fun desactivar(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork(TRABAJO)
    }

    @SuppressLint("MissingPermission")
    fun mostrar(context: Context) {
        val permitido = Build.VERSION.SDK_INT < 33 ||
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
        if (!permitido) return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val canal = NotificationChannel(CANAL, "Recordatorios", NotificationManager.IMPORTANCE_DEFAULT)
            context.getSystemService(NotificationManager::class.java).createNotificationChannel(canal)
        }
        val notificacion = NotificationCompat.Builder(context, CANAL)
            .setSmallIcon(R.drawable.ic_notificacion)
            .setContentTitle("Noctune Security")
            .setContentText("Es momento de revisar tu seguridad. Realiza un nuevo análisis.")
            .setAutoCancel(true)
            .build()
        NotificationManagerCompat.from(context).notify(1001, notificacion)
    }
}

class RecordatorioWorker(context: Context, parametros: WorkerParameters) : Worker(context, parametros) {
    override fun doWork(): Result {
        Recordatorio.mostrar(applicationContext)
        return Result.success()
    }
}
