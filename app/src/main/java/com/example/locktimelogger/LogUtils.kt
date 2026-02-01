package com.example.locktimelogger

import android.content.Context
import android.util.Log
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object LogUtils {
    fun logState(context: Context, state: String, time: Date = Date()) {
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

    fun logToFile(context: Context, text: String, file: File) {
        try {
            Log.d("LogUtils.logToFile_${file.absolutePath}", text)
            FileWriter(file, true).use { writer ->
                writer.appendLine(text)
            }
        } catch (e: Exception) {
            logException(context, e)
        }
    }

    fun logException(context: Context, e: Exception) {
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

        var isLastLocked: Boolean? = null;
        var lastDate: Date? = null;

        return file.readLines().mapNotNull { line ->
            val parts = line.split("|")
            if (parts.size != 2) return@mapNotNull null

            try {
                val date = SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH)
                    .parse(parts[0])

                var eventStr = parts[1]

                if (date == null) {
                    throw Exception("can't parse date")
                }

                val isLocked = if (eventStr.contains("UNLOCKED", true)) false
                else if (eventStr.contains("NO_LOCK")) false
                else true

                if (isLastLocked == null) {
                    isLastLocked = isLocked;
                    lastDate = date;

                    return@mapNotNull LogEntry(date, eventStr)
                } else if (isLastLocked == isLocked) {
                    return@mapNotNull LogEntry(date, eventStr)
                } else if (isLastLocked != isLocked && lastDate != null) {

                    val diffMillis = date.time - lastDate.time
                    val totalMinutes = diffMillis / 60_000

                    val hours = totalMinutes / 60
                    val minutes = totalMinutes % 60
                    val seconds = (diffMillis/1000)%60

                    val hhmmss = String.format("%d:%02d:%02d", hours, minutes,seconds)

                    val str = "was ${if (isLastLocked) "L" else "NoL"} ${
                        SimpleDateFormat("HH:mm").format(lastDate)
                    }-${SimpleDateFormat("HH:mm").format(date)} ($hhmmss)";

                    isLastLocked = isLocked;
                    lastDate = date;

                    return@mapNotNull LogEntry(date, eventStr + " ~ " + str)
                }

                throw Exception("Unreachable")
            } catch (e: Exception) {
                logException(context, e)
                null
            }
        }.reversed() // последние события сверху
    }

    fun clearLog(context: Context) {
        File(context.filesDir, "locklog.txt").writeText("")
    }
}
