package com.models

enum class Priority {
    VERY_LOW, LOW, MEDIUM, HIGH, VERY_HIGH;

    companion object {
        fun fromInt(value: Int): Priority = when (value) {
            1 -> VERY_LOW
            2 -> LOW
            3 -> MEDIUM
            4 -> HIGH
            5 -> VERY_HIGH
            else -> MEDIUM
        }

        fun toInt(priority: Priority): Int = when (priority) {
            VERY_LOW -> 1
            LOW -> 2
            MEDIUM -> 3
            HIGH -> 4
            VERY_HIGH -> 5
        }

        fun toDisplayString(priority: Priority): String = when (priority) {
            VERY_LOW -> "1 (Очень низкий)"
            LOW -> "2 (Низкий)"
            MEDIUM -> "3 (Средний)"
            HIGH -> "4 (Высокий)"
            VERY_HIGH -> "5 (Очень высокий)"
        }
    }
}