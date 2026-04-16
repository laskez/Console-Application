package com.controller

import com.models.Priority
import com.models.Task
import com.repository.SortBy
import com.repository.TaskRepository
import kotlin.system.exitProcess

class MenuController(private val repository: TaskRepository) {

    fun start() {
        println("\n" + "-".repeat(20))
        println("          Консольное Приложение")
        println("-".repeat(20))

        while (true) {
            printMenu()
            print("Выберите действие: ")
            when (readlnOrNull()?.trim()) {
                "1" -> createTask()
                "2" -> showAllTasks()
                "3" -> searchTasks()
                "4" -> editTask()
                "5" -> deleteTask()
                "6" -> sortMenu()
                "7" -> saveToFile()
                "8" -> loadFromFile()
                "9" -> markMultipleDone()
                "0" -> exit()
                else -> println("Ошибка: Неверный ввод!")
            }
        }
    }

    private fun printMenu() {
        println("\n" + "-".repeat(20))
        println("       Главное Меню")
        println("-".repeat(20))
        println(" 1  - Создать задачу")
        println(" 2  - Показать все задачи")
        println(" 3  - Найти задачу")
        println(" 4  - Редактировать задачу")
        println(" 5  - Удалить задачу")
        println(" 6  - Сортировка")
        println(" 7  - Сохранить в файл")
        println(" 8  - Загрузить из файла")
        println(" 9  - Отметить несколько задач")
        println(" 0  - Выход")
        println("-".repeat(20))
        println("Всего задач: ${repository.getTasksCount()}")
    }

    private fun createTask() {
        println("\nСОЗДАНИЕ НОВОЙ ЗАДАЧИ")
        print("Название: ")
        val title = readlnOrNull()?.trim() ?: ""
        if (title.isEmpty()) {
            println("Ошибка: Название не может быть пустым!")
            return
        }

        print("Описание: ")
        val description = readlnOrNull()?.trim() ?: ""

        print("Приоритет (1-5): ")
        val priorityInput = readlnOrNull()?.trim() ?: ""
        val priorityInt = priorityInput.toIntOrNull()
        if (priorityInt == null || priorityInt !in 1..5) {
            println("Ошибка: Приоритет должен быть от 1 до 5!")
            return
        }

        val task = repository.addTask(title, description, Priority.fromInt(priorityInt))
        println("Задача создана с ID: ${task.id}")
    }

    private fun showAllTasks() {
        val tasks = repository.getAllTasks()
        if (tasks.isEmpty()) {
            println("\nНет задач")
            return
        }

        println("\nВсе Задачи")
        println("+-----+--------------------------+------------+------+---------------------+")
        println("| ID  | Название                 | Приоритет  | Вып. | Дата создания       |")
        println("+-----+--------------------------+------------+------+---------------------+")

        tasks.forEach { task ->
            val doneMark = if (task.isDone) "Да" else "Нет"
            val priorityStr = Priority.toInt(task.priority).toString()

            println("| ${task.id.toString().padEnd(3)} | ${task.title.take(24).padEnd(24)} | ${priorityStr.padEnd(10)} | ${doneMark.padEnd(4)} | ${task.formatCreatedAt().padEnd(19)} |")
        }
        println("+-----+--------------------------+------------+------+---------------------+")
        println("Всего: ${tasks.size} задач(и)")
    }

    private fun searchTasks() {
        println("\nПоиск задач")
        println("-".repeat(40))
        println("Оставьте поле пустым, чтобы пропустить")

        print("По названию: ")
        val title = readlnOrNull()?.trim()

        print("По приоритету (1-5): ")
        val priorityInput = readlnOrNull()?.trim()
        val priority = if (priorityInput.isNullOrEmpty()) null else Priority.fromInt(priorityInput.toIntOrNull() ?: 0)

        print("По статусу (1-выполнена / 0-не выполнена): ")
        val doneInput = readlnOrNull()?.trim()?.lowercase()
        val isDone = when (doneInput) {
            "1", "y", "yes", "да" -> true
            "0", "n", "no", "нет" -> false
            else -> null
        }

        val results = repository.searchTasks(
            title = if (title.isNullOrEmpty()) null else title,
            priority = priority,
            isDone = isDone
        )

        if (results.isEmpty()) {
            println("\nЗадачи не найдены")
        } else {
            println("\nНайдено ${results.size} задач(и):")
            results.forEach { task ->
                println("  ID ${task.id}: ${task.title} [${if (task.isDone) "Выполнена" else "Не выполнена"}]")
            }
        }
    }

    private fun editTask() {
        print("Введите ID задачи: ")
        val id = readlnOrNull()?.trim()?.toIntOrNull()
        if (id == null) {
            println("Ошибка: Неверный ID")
            return
        }

        val task = repository.findTaskById(id)
        if (task == null) {
            println("Ошибка: Задача не найдена")
            return
        }

        println("\nРедактирование")
        print("Название (было: ${task.title}): ")
        val newTitle = readlnOrNull()?.trim()

        print("Описание (было: ${task.description}): ")
        val newDesc = readlnOrNull()?.trim()

        print("Приоритет (1-5) (был: ${Priority.toInt(task.priority)}): ")
        val newPriorityInput = readlnOrNull()?.trim()
        val newPriority = if (newPriorityInput.isNullOrEmpty()) null else Priority.fromInt(newPriorityInput.toIntOrNull() ?: 0)

        print("Выполнена? (да/нет) (было: ${if (task.isDone) "да" else "нет"}): ")
        val newDoneInput = readlnOrNull()?.trim()?.lowercase()
        val newIsDone = when (newDoneInput) {
            "да", "yes", "y", "1" -> true
            "нет", "no", "n", "0" -> false
            else -> null
        }

        if (repository.updateTask(id,
                if (newTitle.isNullOrEmpty()) null else newTitle,
                if (newDesc.isNullOrEmpty()) null else newDesc,
                newPriority,
                newIsDone
            )) {
            println("Задача обновлена")
        } else {
            println("Ошибка: Не удалось обновить задачу")
        }
    }

    private fun deleteTask() {
        print("Введите ID задачи: ")
        val id = readlnOrNull()?.trim()?.toIntOrNull()
        if (id == null) {
            println("Ошибка: Неверный ID")
            return
        }

        val task = repository.findTaskById(id)
        if (task == null) {
            println("Ошибка: Задача не найдена")
            return
        }

        println("Задача: ${task.title}")
        print("Вы уверены? (y/n): ")
        val confirm = readlnOrNull()?.trim()?.lowercase()

        if (confirm == "y" || confirm == "yes" || confirm == "да") {
            if (repository.deleteTask(id)) {
                println("Задача удалена")
            } else {
                println("Ошибка: Не удалось удалить задачу")
            }
        } else {
            println("Удаление отменено")
        }
    }

    private fun sortMenu() {
        println("\nСОРТИРОВКА")
        println("1 - По дате создания")
        println("2 - По приоритету")
        println("3 - По названию")
        print("Выберите: ")
        val choice = readlnOrNull()?.trim()

        val sortBy = when (choice) {
            "1" -> SortBy.DATE
            "2" -> SortBy.PRIORITY
            "3" -> SortBy.TITLE
            else -> {
                println("Ошибка: Неверный выбор")
                return
            }
        }

        println("1 - По возрастанию")
        println("2 - По убыванию")
        print("Выберите: ")
        val order = readlnOrNull()?.trim()
        val ascending = order == "1"

        val sortedTasks = repository.sortTasks(sortBy, ascending)
        val orderText = if (ascending) "возрастанию" else "убыванию"

        println("\nОТСОРТИРОВАННЫЙ СПИСОК (по $orderText)")
        sortedTasks.forEach { task ->
            println("  ID ${task.id}: ${task.title} | Приоритет: ${Priority.toInt(task.priority)} | Создана: ${task.formatCreatedAt()}")
        }
    }

    private fun saveToFile() {
        if (repository.saveToFile()) {
            println("Задачи сохранены в файл tasks.csv")
        } else {
            println("Ошибка: Не удалось сохранить")
        }
    }

    private fun loadFromFile() {
        if (repository.loadFromFile()) {
            println("Задачи загружены из файла tasks.csv")
        } else {
            println("Ошибка: Файл не найден или повреждён")
        }
    }

    private fun markMultipleDone() {
        println("\nОтметка нескольких задач")
        print("Введите ID через пробел: ")
        val idsInput = readlnOrNull()?.trim() ?: ""
        val ids = idsInput.split(" ").mapNotNull { it.toIntOrNull() }

        if (ids.isEmpty()) {
            println("Ошибка: Нет корректных ID")
            return
        }

        val count = repository.markMultipleDone(ids)
        println("Отмечено $count задач(и)")
    }

    private fun exit() {
        println("\nДо свидания!")
        exitProcess(0)
    }
}