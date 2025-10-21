package com.example.locktimelogger

import android.app.KeyguardManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean

class ScreenEventReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val action = intent?.action ?: return
        val keyguard = context.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager

        val state = when (action) {
            Intent.ACTION_SCREEN_OFF -> {
                if (keyguard.isKeyguardLocked) "SCREEN_OFF_LOCKED"
                else "SCREEN_OFF_NO_LOCK"
            }

            Intent.ACTION_SCREEN_ON -> {
                if (keyguard.isKeyguardLocked) "SCREEN_ON_LOCKED"
                else "SCREEN_ON_UNCLOCKED"
            }

            Intent.ACTION_USER_PRESENT -> "DEVICE_UNLOCKED"
            else -> "UNKNOWN"
        }

        if (state == "SCREEN_OFF_NO_LOCK") {
            LockStateMonitor.startWaitForLock(context)
        } else if(state != "UNKNOWN") {
            LockStateMonitor.requestBreak()
        }

        LogUtils.logState(context, state)
    }

    object LockStateMonitor {
        private val isWaiting = AtomicBoolean(false)
        private val isBreakRequested = AtomicBoolean(false)

        fun requestBreak() {
            isBreakRequested.set(true)
        }

        fun startWaitForLock(context: Context) {
            if (isWaiting.compareAndExchange(false, true)) return // Уже запущен — выходим

            isBreakRequested.set(false)

            CoroutineScope(Dispatchers.Default).launch {
                val keyguard = context.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
                val start = System.currentTimeMillis()
                val timeout = 30_000L

                while ((System.currentTimeMillis() - start < timeout) && !isBreakRequested.get()) {
                    if (keyguard.isKeyguardLocked) {
                        LogUtils.logState(context, "DEVICE_LOCKED_AFTER_SCREEN_OFF")
                        break
                    }
                    delay(500)
                }

                isWaiting.set(false) // Освобождаем флаг
            }
        }
    }


}