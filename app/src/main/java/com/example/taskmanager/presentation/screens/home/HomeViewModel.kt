package com.example.taskmanager.presentation.screens.home

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskmanager.domain.model.Task
import com.example.taskmanager.domain.usecase.auth.LogOutUseCase
import com.example.taskmanager.domain.usecase.task.deleteAllTasksUseCase
import com.example.taskmanager.domain.usecase.task.deleteTaskByIdUseCase
import com.example.taskmanager.domain.usecase.task.observeTasksForDateUseCase
import com.example.taskmanager.domain.usecase.task.updateTaskUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.LocalDate
import javax.inject.Inject


@RequiresApi(Build.VERSION_CODES.O)
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val deleteTaskByIdUseCase: deleteTaskByIdUseCase,
    private val deleteAlltasksUseCase: deleteAllTasksUseCase,
    private val logOutUseCase: LogOutUseCase,
    private val updateTaskUseCase: updateTaskUseCase,
    private val observeTasksForDateUseCase: observeTasksForDateUseCase,
) : ViewModel() {

    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks.asStateFlow()
    private val _tasksCompleted = MutableStateFlow<List<Task>>(emptyList())
    val tasksCompleted: StateFlow<List<Task>> = _tasksCompleted.asStateFlow()

    init {
        observeTasksForDate()
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun observeTasksForDate() {
        viewModelScope.launch {
            val result = observeTasksForDateUseCase(LocalDate.now())

            observeTasksForDateUseCase(
                LocalDate.now()
            ).onStart {
                Timber.d("Observing tasks for date: ${LocalDate.now()}")
            }.onCompletion {
                Timber.d("Completed observing tasks for date: ${LocalDate.now()}")
            }
                .collect { result ->
                    result.onSuccess { tasks ->
                        Timber.d("Tasks for date: ${result.getOrDefault(emptyList())}")
                        val filteredTasks = tasks.filter { it.completed == true }
                        val notCompletedTasks = tasks.filter { it.completed == false }
                        _tasksCompleted.value = filteredTasks
                        _tasks.value = notCompletedTasks
                        //Complete the flow


                    }.onFailure {
                        Timber.e("Error fetching tasks: ${it.message}")
                    }
                }
        }
    }


    fun updateTask(task: Task) {
        viewModelScope.launch {
            updateTaskUseCase(task)
        }
    }

    fun logout() {
        viewModelScope.launch {
            val result = logOutUseCase()
            result.onSuccess {
                Timber.d("User logged out successfully")
                deleteAlltasksUseCase().onSuccess {
                    Timber.d("All tasks deleted successfully")
                }.onFailure {
                    Timber.e("Error deleting all tasks: ${it.message}")
                }
            }.onFailure {
                Timber.e("Error logging out: ${it.message}")
            }

        }
    }


}
