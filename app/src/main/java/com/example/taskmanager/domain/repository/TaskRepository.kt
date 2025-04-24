package com.example.taskmanager.domain.repository

import com.example.taskmanager.domain.model.Task

interface TaskRepository {
    suspend fun addTask(task: Task): Result<Unit>
    suspend fun getTasks(userId: String): Result<List<Task>>
}