package com.example.taskmanager.domain.model

import com.google.firebase.Timestamp

data class Task(
    val taskId: String = "",
    val userId: String = "",
    val title: String = "",
    val timeStart: String = "00:00",
    val timeEnd: String = "00:00",
    val dateStart: Timestamp? = Timestamp.now(),
    val description: String = "",
    val taskColor: String = "#FFFFFF", // Default color white
    val priority: String = "Low", // Default priority
    val completed: Boolean = false,
    val notificationEnabled: Boolean = true,
    val createdAt: Long = System.currentTimeMillis(),
)
