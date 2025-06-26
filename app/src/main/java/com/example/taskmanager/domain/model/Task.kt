package com.example.taskmanager.domain.model

import com.google.firebase.Timestamp

data class Task(
    val taskId: String = "",
    val userId: String = "",
    val title: String = "",
    val timeStart: String = "",
    val timeEnd: String = "",
    val dateStart: Timestamp? = Timestamp.now(),
    val description: String = "",
    val taskColor: String = "#FFFFFF", // Default color white
    val priority: String = "Low", // Default priority
    val completed: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
)
