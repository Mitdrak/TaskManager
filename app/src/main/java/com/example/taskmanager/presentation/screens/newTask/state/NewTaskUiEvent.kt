package com.example.taskmanager.presentation.screens.newTask.state

sealed class NewTaskUiEvent {
    data class TitleChanged(val inputValue: String) : NewTaskUiEvent()
    data class DescriptionChanged(val inputValue: String) : NewTaskUiEvent()
    data class TimeStartChanged(val inputValue: String) : NewTaskUiEvent()
    data class TimeEndChanged(val inputValue: String) : NewTaskUiEvent()
    data class DateChanged(val inputValue: String) : NewTaskUiEvent()
    object AddTask : NewTaskUiEvent()
}