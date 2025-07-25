package com.example.locktimelogger

import android.content.Context
import android.util.Log
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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

    fun getLogEntries(context: Context): List<LogEntry> {
        val file = File(context.filesDir, "locklog.txt")
        if (!file.exists()) return emptyList()

        return file.readLines().mapNotNull { line ->
            val parts = line.split("|")
            if (parts.size != 2) return@mapNotNull null

            try {
                val date = SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH)
                    .parse(parts[0])
                LogEntry(date!!, parts[1])
            } catch (e: Exception) {
                null
            }
        }.reversed() // последние события сверху
    }

    fun clearLog(context: Context) {
        File(context.filesDir, "locklog.txt").writeText("")
    }
}
