package com.example.taskmanager.domain.usecase.task

import com.example.taskmanager.domain.repository.TaskRepository
import jakarta.inject.Inject

class stopObservingTasksUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {
    suspend operator fun invoke() {
        taskRepository.stopObservingTasks()
    }
}
