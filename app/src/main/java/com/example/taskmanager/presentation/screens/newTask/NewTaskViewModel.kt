package com.example.taskmanager.presentation.screens.newTask

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskmanager.domain.manager.UserSessionManager
import com.example.taskmanager.domain.model.Task
import com.example.taskmanager.domain.usecase.task.addTaskUseCase
import com.example.taskmanager.presentation.screens.newTask.state.ErrorState
import com.example.taskmanager.presentation.screens.newTask.state.NewTaskErrorState
import com.example.taskmanager.presentation.screens.newTask.state.NewTaskState
import com.example.taskmanager.presentation.screens.newTask.state.NewTaskUiEvent
import com.google.firebase.Timestamp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.util.Date
import javax.inject.Inject


@HiltViewModel
class NewTaskViewModel @Inject constructor(
    private val addTaskUseCase: addTaskUseCase,
    private val userSessionManager: UserSessionManager
) : ViewModel() {
    private val _newTaskState = MutableStateFlow(NewTaskState())
    val newTaskState: StateFlow<NewTaskState> = _newTaskState.asStateFlow()
    private val _selectedDateTime = MutableStateFlow<Timestamp?>(null)
    val selectedDateTime: StateFlow<Timestamp?> = _selectedDateTime
    val isFieldsNotEmpty: StateFlow<Boolean> = newTaskState.map { state ->
        state.title.isNotBlank() && state.description.isNotBlank() && state.timeStart.isNotBlank() && state.timeEnd.isNotBlank() && state.date.isNotBlank()
    }.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        false
    )

    fun onUiEvent(newTaskUiEvent: NewTaskUiEvent) {
        when (newTaskUiEvent) {
            NewTaskUiEvent.AddTask -> {
                addTask()
            }


            is NewTaskUiEvent.DateChanged -> {
                _newTaskState.value = _newTaskState.value.copy(
                    date = newTaskUiEvent.inputValue
                )
            }

            is NewTaskUiEvent.DescriptionChanged -> {
                _newTaskState.value = _newTaskState.value.copy(
                    description = newTaskUiEvent.inputValue
                )
            }

            is NewTaskUiEvent.TimeEndChanged -> {
                _newTaskState.value = _newTaskState.value.copy(
                    timeEnd = newTaskUiEvent.inputValue
                )
            }

            is NewTaskUiEvent.TimeStartChanged -> {
                _newTaskState.value = _newTaskState.value.copy(
                    timeStart = newTaskUiEvent.inputValue
                )
            }

            is NewTaskUiEvent.TitleChanged -> {
                _newTaskState.value = _newTaskState.value.copy(
                    title = newTaskUiEvent.inputValue
                )
            }

            is NewTaskUiEvent.TaskColorChanged -> {
                _newTaskState.value = _newTaskState.value.copy(
                    taskColor = newTaskUiEvent.inputValue
                )
            }

            is NewTaskUiEvent.PriorityChanged -> {
                _newTaskState.value = _newTaskState.value.copy(
                    priority = newTaskUiEvent.inputValue
                )
            }

            is NewTaskUiEvent.ShowSnackbar -> {
                Timber.d("Snackbar message: ${newTaskUiEvent.message}")
                _newTaskState.value = _newTaskState.value.copy(
                    snackBarMessage = newTaskUiEvent.message
                )
            }
        }
    }

    private fun addTask() {
        if (!fieldsNotEmpty()) {
            _newTaskState.value = _newTaskState.value.copy(
                isTaskAdded = false,
                errorState = NewTaskErrorState(
                    generalErrorState = ErrorState(
                        hasError = true,
                        errorMessage = "Please fill all the fields"
                    )
                )
            )
            Timber.d("Please fill all the fields")
            return
        }
        viewModelScope.launch {
            userSessionManager.userIdFlow.collect { userId ->
                val task = Task(
                    userId = userId ?: "",
                    title = newTaskState.value.title,
                    description = newTaskState.value.description,
                    timeStart = newTaskState.value.timeStart,
                    timeEnd = newTaskState.value.timeEnd,
                    dateStart = selectedDateTime.value,
                    taskColor = newTaskState.value.taskColor,
                    priority = newTaskState.value.priority
                )
                if (userId != null) {
                    val result = addTaskUseCase(task)
                    result.onSuccess {
                        _newTaskState.value = _newTaskState.value.copy(
                            isTaskAdded = true
                        )
                        Timber.d("Task added successfully")
                        onUiEvent(NewTaskUiEvent.ShowSnackbar("Task added successfully"))
                        clearTaskForm()
                    }.onFailure { error ->
                        _newTaskState.value = _newTaskState.value.copy(
                            isTaskAdded = false,
                            errorState = NewTaskErrorState(
                                generalErrorState = ErrorState(
                                    hasError = true,
                                    errorMessage = "Unknown error: $error"
                                )
                            )
                        )
                        onUiEvent(NewTaskUiEvent.ShowSnackbar("Unknown error: $error"))
                    }

                }
            }
        }
    }

    private fun fieldsNotEmpty(): Boolean {
        return newTaskState.value.title.isNotEmpty() && newTaskState.value.description.isNotEmpty() && newTaskState.value.timeStart.isNotEmpty() && newTaskState.value.timeEnd.isNotEmpty() && newTaskState.value.date.isNotEmpty()
    }

    fun clearTaskForm() {
        _newTaskState.value = _newTaskState.value.copy(
            title = "",
            description = "",
            timeStart = "",
            timeEnd = "",
            date = "",
            taskColor = "#FFB2FFFC",
            priority = "Medium",
            isTaskAdded = false
        )
    }

    fun snackbarMessageShown() {
        _newTaskState.value = _newTaskState.value.copy(snackBarMessage = "")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun setDateAndTime(
        date: LocalDate,
        time: LocalTime
    ) {
        val dateTime = LocalDateTime.of(
            date,
            time
        )
        val instant = dateTime.atZone(ZoneId.systemDefault()).toInstant()
        val timestamp = Timestamp(Date.from(instant))
        _selectedDateTime.value = timestamp
        _newTaskState.value = _newTaskState.value.copy(
            date = date.toString(),
        )
    }

}
