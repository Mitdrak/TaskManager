package com.example.taskmanager.presentation.screens.home

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskmanager.domain.manager.UserSessionManager
import com.example.taskmanager.domain.model.Task
import com.example.taskmanager.domain.usecase.task.addTaskUseCase
import com.example.taskmanager.domain.usecase.task.getTasksUseCase
import com.example.taskmanager.domain.usecase.task.observeTasksForDateUseCase
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
    private val addTaskUseCase: addTaskUseCase,
    private val getTaskUseCase: getTasksUseCase,
    private val observeTasksForDateUseCase: observeTasksForDateUseCase,
    private val userSessionManager: UserSessionManager
) : ViewModel() {

    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks.asStateFlow()
    private val _tasksCompleted = MutableStateFlow<List<Task>>(emptyList())
    val tasksCompleted: StateFlow<List<Task>> = _tasksCompleted.asStateFlow()

    init {
        observeTasksForDate()
    }


    fun addNewTask() {
        viewModelScope.launch {
            userSessionManager.userIdFlow.collect { userId ->
                val task1 = Task(
                    userId = userId ?: "",
                    title = "Meeting with boss",
                    description = "Discuss project updates",
                    timeStart = "3",
                    timeEnd = "11"
                )
                val task2 = Task(
                    userId = userId ?: "",
                    title = "Hang out with friend",
                    description = "Catch up over coffee",
                    timeStart = "12",
                    timeEnd = "13"
                )
                val task3 = Task(
                    userId = userId ?: "",
                    title = "Workout",
                    description = "Gym session",
                    timeStart = "14",
                    timeEnd = "16"
                )

                if (userId != null) {
                    addTaskUseCase(task1)
                }
            }
        }
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


    fun getTasks() {
        viewModelScope.launch {
            userSessionManager.userIdFlow.collect { userId ->
                if (userId != null) {
                    val result = getTaskUseCase(userId)
                    Timber.d("Tasks: $result")
                }
            }
        }
    }


}