package com.example.locktimelogger

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat

object NotificationHelper {
    private const val CHANNEL_ID = "lock_logger_channel"

    fun createNotificationChannel(context: Context) {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Lock Logger Background Service",
            NotificationManager.IMPORTANCE_LOW
        )
        val manager = context.getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }

    fun buildNotification(context: Context): Notification {
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            Intent(context, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle("Логирование блокировок")
            .setContentText("Сервис работает")
            .setSmallIcon(R.drawable.ic_launcher_foreground) // Заменить на свою иконку
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build()
    }
}