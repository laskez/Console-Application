package com.models

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class Task(
    val id: Int,
    val title: String,
    val description: String,
    val priority: Priority,
    val isDone: Boolean,
    val createdAt: LocalDateTime
) {
    fun formatCreatedAt(): String {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        return createdAt.format(formatter)
    }

    fun toCsvString(): String {
        return "$id|$title|$description|${Priority.toInt(priority)}|$isDone|$createdAt"
    }

    companion object {
        fun fromCsvString(csv: String): Task? {
            val parts = csv.split("|")
            return try {
                Task(
                    id = parts[0].toInt(),
                    title = parts[1],
                    description = parts[2],
                    priority = Priority.fromInt(parts[3].toInt()),
                    isDone = parts[4].toBoolean(),
                    createdAt = LocalDateTime.parse(parts[5])
                )
            } catch (e: Exception) {
                null
            }
        }
    }
}