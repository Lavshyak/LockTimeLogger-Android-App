package com.example.locktimelogger

import android.accessibilityservice.AccessibilityService
import android.app.Service
import android.content.Context
import android.content.IntentFilter
import android.content.Intent
import android.util.Log

/**
 * AccessibilityService для отслеживания блокировки экрана на Android 14+.
 *
 * На Android 14-16 foreground service (dataSync) убивается системой через ~6 часов.
 * AccessibilityService — единственный способ держать процесс живым постоянно.
 *
 * Регистрирует ScreenEventReceiver, который ловит SCREEN_ON/OFF/USER_PRESENT
 * и логирует состояния через LogUtils.
 */
class ScreenStateAccessibilityService : AccessibilityService() {

    companion object {
        private const val TAG = "ScreenStateAccess"
        var instance: ScreenStateAccessibilityService? = null
            private set
    }

    private val receiver = ScreenEventReceiver()

    override fun onCreate() {
        super.onCreate()
        NotificationHelper.createNotificationChannel(this)
        val notification = NotificationHelper.buildNotification(this)
        startForeground(1, notification)
    }

    override fun onAccessibilityEvent(event: android.view.accessibility.AccessibilityEvent?) {
        // Не используется — всю работу делает ScreenEventReceiver.
        // Этот метод обязателен для AccessibilityService.
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        Log.d(TAG, "connected")
        instance = this

        // Регистрируем ScreenEventReceiver — он будет ловить SCREEN_ON/OFF/USER_PRESENT
        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_SCREEN_OFF)
            addAction(Intent.ACTION_SCREEN_ON)
            addAction(Intent.ACTION_USER_PRESENT)
        }
        registerReceiver(receiver, filter)
    }

    override fun onInterrupt() {
        Log.d(TAG, "interrupted")
    }

    override fun onDestroy() {
        Log.d(TAG, "destroyed")
        unregisterReceiver(receiver)
        instance = null
        super.onDestroy()
    }
}