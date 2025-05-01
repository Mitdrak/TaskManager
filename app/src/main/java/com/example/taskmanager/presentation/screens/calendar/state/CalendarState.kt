package com.example.taskmanager.presentation.screens.calendar.state

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.taskmanager.domain.model.Task
import java.time.LocalDate

data class CalendarState @RequiresApi(Build.VERSION_CODES.O) constructor(
    val selectedDate: LocalDate = LocalDate.now(),
    val isLoading: Boolean = false,
    val tasks: List<Task> = emptyList(),
    val isTaskAdded: Boolean = false,
    val isTaskEdited: Boolean = false,
    val isTaskDeleted: Boolean = false,
)
