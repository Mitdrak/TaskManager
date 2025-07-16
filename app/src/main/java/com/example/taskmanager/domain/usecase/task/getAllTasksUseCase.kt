package com.example.taskmanager.domain.usecase.task

import com.example.taskmanager.domain.repository.TaskRepository

class getAllTasksUseCase(
    private val taskRepository: TaskRepository
) {
    suspend operator fun invoke(): Result<Unit> {
        return taskRepository.getAllTasks()
    }
}
