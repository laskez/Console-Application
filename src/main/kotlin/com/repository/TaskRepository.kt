package com.repository

import com.models.Priority
import com.models.Task
import com.utils.Logger
import java.io.File
import java.time.LocalDateTime

enum class SortBy {
    DATE, PRIORITY, TITLE
}

class TaskRepository {
    private val tasks = mutableListOf<Task>()
    private var nextId = 1
    private val dataFile = File("tasks.csv")

    init {
        loadFromFile()
    }

    fun addTask(title: String, description: String, priority: Priority): Task {
        val task = Task(nextId, title, description, priority, false, LocalDateTime.now())
        tasks.add(task)
        nextId++
        Logger.info("Добавлена задача: $title (ID: ${task.id})")
        return task
    }

    fun getAllTasks(): List<Task> = tasks.toList()

    fun findTaskById(id: Int): Task? = tasks.find { it.id == id }

    fun updateTask(
        id: Int,
        newTitle: String?,
        newDescription: String?,
        newPriority: Priority?,
        newIsDone: Boolean?
    ): Boolean {
        val task = findTaskById(id) ?: return false

        val updatedTask = task.copy(
            title = newTitle ?: task.title,
            description = newDescription ?: task.description,
            priority = newPriority ?: task.priority,
            isDone = newIsDone ?: task.isDone
        )

        val index = tasks.indexOf(task)
        tasks[index] = updatedTask
        Logger.info("Обновлена задача ID: $id")
        return true
    }

    fun deleteTask(id: Int): Boolean {
        val task = findTaskById(id) ?: return false
        tasks.remove(task)
        Logger.info("Удалена задача ID: $id")
        return true
    }

    fun searchTasks(
        title: String? = null,
        priority: Priority? = null,
        isDone: Boolean? = null
    ): List<Task> {
        return tasks.filter { task ->
            (title == null || task.title.contains(title, ignoreCase = true)) &&
                    (priority == null || task.priority == priority) &&
                    (isDone == null || task.isDone == isDone)
        }
    }

    // ИСПРАВЛЕННАЯ ФУНКЦИЯ - принимает List<Int>
    fun markMultipleDone(ids: List<Int>): Int {
        var count = 0
        ids.forEach { id ->
            if (updateTask(id, null, null, null, true)) {
                count++
            }
        }
        Logger.info("Отмечено выполнено $count задач")
        return count
    }

    fun sortTasks(by: SortBy, ascending: Boolean): List<Task> {
        val sorted = when (by) {
            SortBy.DATE -> tasks.sortedBy { it.createdAt }
            SortBy.PRIORITY -> tasks.sortedBy { Priority.toInt(it.priority) }
            SortBy.TITLE -> tasks.sortedBy { it.title.lowercase() }
        }
        return if (ascending) sorted else sorted.reversed()
    }

    fun saveToFile(): Boolean {
        return try {
            dataFile.writeText("")
            tasks.forEach { task ->
                dataFile.appendText(task.toCsvString() + "\n")
            }
            Logger.info("Сохранено ${tasks.size} задач в файл")
            true
        } catch (e: Exception) {
            Logger.error("Ошибка сохранения", e)
            false
        }
    }

    fun loadFromFile(): Boolean {
        if (!dataFile.exists()) return false

        return try {
            tasks.clear()
            nextId = 1

            dataFile.readLines().forEach { line ->
                val task = Task.fromCsvString(line)
                if (task != null) {
                    tasks.add(task)
                    if (task.id >= nextId) nextId = task.id + 1
                }
            }

            Logger.info("Загружено ${tasks.size} задач из файла")
            true
        } catch (e: Exception) {
            Logger.error("Ошибка загрузки", e)
            false
        }
    }

    fun getTasksCount(): Int = tasks.size
}