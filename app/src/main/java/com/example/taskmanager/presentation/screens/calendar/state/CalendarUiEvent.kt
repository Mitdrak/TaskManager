package com.example.taskmanager.presentation.screens.calendar.state

import java.time.LocalDate

sealed class CalendarUiEvent {
    data class ShowSnackbar(val message: String) : CalendarUiEvent()
    object NavigateToTaskDetails : CalendarUiEvent()
    data class ChangeDate(val date: LocalDate) : CalendarUiEvent()
    object NavigateToAddTask : CalendarUiEvent()
    object NavigateToEditTask : CalendarUiEvent()
    object NavigateBack : CalendarUiEvent()
}