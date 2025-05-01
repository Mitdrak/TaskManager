package com.example.taskmanager.domain.usecase.task

import com.example.taskmanager.domain.model.Task
import com.example.taskmanager.domain.repository.TaskRepository
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

class observeTasksForDateUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {
    suspend operator fun invoke(selectedDate: LocalDate): Flow<Result<List<Task>>> {
        return taskRepository.observeTasksForDate(selectedDate)
    }
}