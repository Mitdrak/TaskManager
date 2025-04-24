package com.example.taskmanager.domain.usecase.task

import com.example.taskmanager.domain.model.Task
import com.example.taskmanager.domain.repository.TaskRepository
import javax.inject.Inject

class addTaskUseCase @Inject constructor(private val taskRepository: TaskRepository) {
    suspend operator fun invoke(
        userId: String,
        title: String,
        description: String,
        completed: Boolean = false,
    ): Result<Unit> {
        return taskRepository.addTask(
            Task(
                userId = userId,
                title = title,
                description = description,
                completed = completed
            )
        )
    }
}