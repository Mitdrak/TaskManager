package com.example.taskmanager.presentation.screens.newTask.state

sealed class NewTaskUiEvent {
    data class TitleChanged(val inputValue: String) : NewTaskUiEvent()
    data class DescriptionChanged(val inputValue: String) : NewTaskUiEvent()
    data class TimeStartChanged(val inputValue: String) : NewTaskUiEvent()
    data class TimeEndChanged(val inputValue: String) : NewTaskUiEvent()
    data class DateChanged(val inputValue: String) : NewTaskUiEvent()
    data class TaskColorChanged(val inputValue: String) : NewTaskUiEvent()
    data class PriorityChanged(val inputValue: String) : NewTaskUiEvent()
    data class ShowSnackbar(val message: String) : NewTaskUiEvent()
    data class AddTask(val time: Long?) : NewTaskUiEvent()
}
