package com.example.taskmanager.domain.usecase.task

import com.example.taskmanager.domain.model.Task
import com.example.taskmanager.domain.repository.TaskRepository
import jakarta.inject.Inject

class enableNotificationUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {
    suspend operator fun invoke(task: Task): Result<Unit> {
        return taskRepository.enableNotification(task)
    }
}
