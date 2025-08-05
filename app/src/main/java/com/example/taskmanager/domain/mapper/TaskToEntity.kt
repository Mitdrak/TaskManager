package com.example.taskmanager.domain.mapper

import com.example.taskmanager.data.local.entity.TaskEntity
import com.example.taskmanager.domain.model.Task

fun Task.toEntity(): TaskEntity {
    return TaskEntity(
        taskId = taskId,
        title = title,
        description = description,
        timeStart = timeStart,
        timeEnd = timeEnd,
        userId = userId,
        dateStart = dateStart,
        completed = completed,
        priority = priority,
        taskColor = taskColor,
        createdAt = createdAt,
        notificationEnabled = notificationEnabled
    )
}
