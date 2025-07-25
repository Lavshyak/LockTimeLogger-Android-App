package com.example.locktimelogger

import android.content.Context
import android.util.Log
import java.io.File
import java.io.FileWriter
import java.util.Date

object LogUtils {
    fun logState(context: Context, state: String, time: Date = Date())
    {
        try {
            val text = "$time|$state"
            val file = File(context.filesDir, "locklog.txt")
            logToFile(context, text, file)
        } catch (e: Exception) {
            logException(context, e)
        }
    }

    fun logAny(context: Context, text: String) {
        try {
            val file = File(context.filesDir, "anyLog.txt")
            logToFile(context, text, file)
        } catch (e: Exception) {
            logException(context, e)
        }
    }

    fun logToFile(context: Context, text: String, file: File)
    {
        try {
            Log.d("LogUtils.logToFile_${file.absolutePath}", text)
            FileWriter(file, true).use { writer ->
                writer.appendLine(text)
            }
        } catch (e: Exception) {
            logException(context, e)
        }
    }

    fun logException(context: Context, e:Exception)
    {
        try {
            e.printStackTrace()
            val file = File(context.filesDir, "exceptionsLog.txt")
            FileWriter(file, true).use { writer ->
                writer.appendLine(e.stackTraceToString())
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
