package com.example.taskmanager.domain.usecase.task

import com.example.taskmanager.domain.model.Task
import com.example.taskmanager.domain.repository.TaskRepository
import javax.inject.Inject

class getTasksUseCase @Inject constructor(private val taskRepository: TaskRepository) {
    suspend operator fun invoke(userId: String): Result<List<Task>> {
        return taskRepository.getTasks(userId)
    }
}