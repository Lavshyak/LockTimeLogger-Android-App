package com.example.locktimelogger

import java.util.Date

data class LogEntry(
    val timestamp: Date,
    val event: String
)
