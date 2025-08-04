package com.example.taskmanager.presentation.screens.calendar

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskmanager.domain.manager.UserSessionManager
import com.example.taskmanager.domain.model.Task
import com.example.taskmanager.domain.usecase.task.observeTasksForDateUseCase
import com.example.taskmanager.presentation.screens.calendar.state.CalendarState
import com.example.taskmanager.presentation.screens.calendar.state.CalendarUiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.LocalDate
import javax.inject.Inject


@OptIn(ExperimentalCoroutinesApi::class)
@RequiresApi(Build.VERSION_CODES.O)
@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val observeTasksForDateUseCase: observeTasksForDateUseCase,
    private val userSessionManager: UserSessionManager
) : ViewModel() {
    // State to hold the tasks for the calendar
    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks.asStateFlow()
    private val _selectedDate = MutableStateFlow(LocalDate.now()) // Fecha seleccionada actual
    val selectedDate: StateFlow<LocalDate> = _selectedDate.asStateFlow()

    private val _calendarState = MutableStateFlow(CalendarState())
    val calendarState: StateFlow<CalendarState> = _calendarState.asStateFlow()


    init {
        Timber.d("CalendarViewModel initialized")
    }

    init {
        observeTasksForDate()
    }

    fun observeTasksForDate() {
        viewModelScope.launch {
            selectedDate.flatMapLatest { date ->
                observeTasksForDateUseCase(date)
            }.collect { result ->
                result.onSuccess {
                    Timber.d("Tasks for date ${selectedDate.value}: ${result.getOrDefault(emptyList())}")
                    _tasks.value = result.getOrDefault(emptyList())
                }.onFailure { error ->
                    Timber.e(error, "No task at date ${selectedDate.value}: ", error.message)
                }
            }
        }
    }


    fun onUiEvent(event: CalendarUiEvent) {
        when (event) {
            is CalendarUiEvent.ChangeDate -> {
                Timber.d("Change date event triggered with date: ${event}")
                _calendarState.value = _calendarState.value.copy(
                    selectedDate = event.date
                )
                _selectedDate.value = event.date
            }

            CalendarUiEvent.NavigateBack -> TODO()
            CalendarUiEvent.NavigateToAddTask -> TODO()
            CalendarUiEvent.NavigateToEditTask -> TODO()
            CalendarUiEvent.NavigateToTaskDetails -> TODO()
            is CalendarUiEvent.ShowSnackbar -> TODO()
        }
    }

}
