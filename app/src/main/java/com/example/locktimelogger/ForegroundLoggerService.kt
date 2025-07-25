package com.example.locktimelogger

import android.app.Service
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import androidx.core.app.NotificationCompat

class ForegroundLoggerService : Service() {
    private val receiver = ScreenEventReceiver()

    override fun onCreate() {
        super.onCreate()

        NotificationHelper.createNotificationChannel(this)
        val notification = NotificationHelper.buildNotification(this)
        startForeground(1, notification)

        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_SCREEN_OFF)
            addAction(Intent.ACTION_SCREEN_ON)
            addAction(Intent.ACTION_USER_PRESENT)
        }
        registerReceiver(receiver, filter)
    }

    override fun onDestroy() {
        unregisterReceiver(receiver)
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
