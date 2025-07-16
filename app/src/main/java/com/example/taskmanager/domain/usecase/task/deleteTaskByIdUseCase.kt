package com.example.taskmanager.domain.usecase.task

import com.example.taskmanager.domain.repository.TaskRepository
import jakarta.inject.Inject

class deleteTaskByIdUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {
    suspend operator fun invoke(taskId: String): Result<Unit> {
        return taskRepository.deleteTask(taskId)
    }
}
