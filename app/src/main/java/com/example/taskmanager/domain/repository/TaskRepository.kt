package com.example.taskmanager.domain.repository

import com.example.taskmanager.domain.model.Task
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface TaskRepository {
    suspend fun addTask(task: Task): Result<Unit>

    suspend fun getTaskById(taskId: String): Flow<Result<Task>>
    suspend fun observeTasksForDate(selectedDate: LocalDate): Flow<Result<List<Task>>>
    suspend fun updateTask(task: Task): Result<Unit>
}
