package com

import com.controller.MenuController
import com.repository.TaskRepository

fun main() {
    val repository = TaskRepository()
    val menuController = MenuController(repository)
    menuController.start()
}