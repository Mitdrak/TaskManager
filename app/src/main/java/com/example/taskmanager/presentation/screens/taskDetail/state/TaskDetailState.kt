package com.example.taskmanager.presentation.screens.taskDetail.state

data class TaskDetailState(
    val taskId: String = "",
    val taskTitle: String = "",
    val taskDescription: String = "",
    val taskDueDate: String = "",
    val taskPriority: String = "Normal",
    val isCompleted: Boolean = false,
    val isEditing: Boolean = false,
    val errorState: TaskDetailErrorState = TaskDetailErrorState(),
    val isLoading: Boolean = false
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
