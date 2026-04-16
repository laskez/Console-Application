package com.utils

import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object Logger {
    private val logFile = File("logs.txt")

    fun log(message: String, type: String = "INFO") {
        val timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        val logMessage = "[$type] $timestamp: $message\n"

        println(if (type == "ERROR") "\u001B[31m$logMessage\u001B[0m" else logMessage)
        logFile.appendText(logMessage)
    }

    fun error(message: String, e: Exception? = null) {
        val errorMsg = if (e != null) "$message - ${e.message}" else message
        log(errorMsg, "ERROR")
    }

    fun info(message: String) = log(message, "INFO")
}