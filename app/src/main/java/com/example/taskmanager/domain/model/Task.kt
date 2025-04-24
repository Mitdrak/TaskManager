package com.example.taskmanager.domain.model

data class Task(
    val userId: String = "",
    val title: String = "",
    val description: String = "",
    val completed: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
)
