package com.example.taskmanager.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.Timestamp

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey val taskId: String,
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
