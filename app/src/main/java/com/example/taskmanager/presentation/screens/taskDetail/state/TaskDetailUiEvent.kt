package com.example.taskmanager.presentation.screens.taskDetail.state

sealed class TaskDetailUiEvent {
    data class TaskTitleChanged(val inputValue: String) : TaskDetailUiEvent()
    data class TaskDescriptionChanged(val inputValue: String) : TaskDetailUiEvent()
    data class TaskDueDateChanged(val inputValue: String) : TaskDetailUiEvent()
    data class TaskPriorityChanged(val inputValue: String) : TaskDetailUiEvent()

    data class TaskStartHourChanged(val inputValue: String): TaskDetailUiEvent()
    data class TaskEndHourChanged(val inputValue: String): TaskDetailUiEvent()
    object ToggleEditMode : TaskDetailUiEvent()
    object SaveChanges : TaskDetailUiEvent()
    object CancelEdit : TaskDetailUiEvent()
}
