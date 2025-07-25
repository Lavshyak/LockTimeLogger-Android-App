package com.example.locktimelogger

import android.app.admin.DeviceAdminReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class MyDeviceAdminReceiver : DeviceAdminReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        /*val event = when (intent.action) {
            Intent.ACTION_SCREEN_ON -> "SCREEN_ON"
            Intent.ACTION_USER_UNLOCKED -> "USER_UNLOCKED"
            Intent.ACTION_QUICK_CLOCK -> "QUICK_CLOCK"
            Intent.ACTION_SCREEN_OFF -> "SCREEN_OFF"
            Intent.ACTION_USER_PRESENT -> "USER_PRESENT"
            else -> return
        }

        val timestamp = System.currentTimeMillis()
        val log = "$event at ${java.util.Date(timestamp)}"*/


        //Log.d("LockLogger", log)
        //LogUtils.appendToLogFile(context, log)

        //Log.d("ActionLogged", intent.action.toString())
    }
}
