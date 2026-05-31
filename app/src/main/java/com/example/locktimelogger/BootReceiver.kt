package com.example.locktimelogger

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED ||
            intent.action == Intent.ACTION_LOCKED_BOOT_COMPLETED
        ) {

            val serviceIntent = Intent(context, ForegroundLoggerService::class.java)
            context.startForegroundService(serviceIntent);
        }
    }
}