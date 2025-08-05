package com.example.taskmanager.presentation.screens.taskDetail.state

import com.google.firebase.Timestamp

data class TaskDetailState(
    val taskId: String = "",
    val taskTitle: String = "",
    val taskDescription: String = "",
    val taskDueDate: Timestamp,
    val timeStart: String = "",
    val timeEnd: String = "",
    val taskPriority: String = "Normal",
    val isCompleted: Boolean = false,
    val notificationEnabled: Boolean = true,
    val isTaskDeleted: Boolean = false,
    val isEditing: Boolean = false,
    val errorState: TaskDetailErrorState = TaskDetailErrorState(),
    val isLoading: Boolean = true
)

data class TaskDetailErrorState(
    val taskTitleErrorState: ErrorState = ErrorState(),
    val taskDescriptionErrorState: ErrorState = ErrorState(),
    val taskDueDateErrorState: ErrorState = ErrorState(),
    val taskPriorityErrorState: ErrorState = ErrorState(),
    val generalErrorState: ErrorState = ErrorState()
)
data class ErrorState(
    var hasError: Boolean = false,
    val errorMessageStringResource: Int = 0,
    var errorMessage: String = ""
)
