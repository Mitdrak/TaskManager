package com.example.taskmanager.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskmanager.domain.manager.UserSessionManager
import com.example.taskmanager.domain.model.Task
import com.example.taskmanager.domain.usecase.task.addTaskUseCase
import com.example.taskmanager.domain.usecase.task.getTasksUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject


@HiltViewModel
class HomeViewModel @Inject constructor(
    private val addTaskUseCase: addTaskUseCase,
    private val getTaskUseCase: getTasksUseCase,
    private val userSessionManager: UserSessionManager
) : ViewModel() {


    val mockedTasks = listOf(
        Task(userId = "1", title = "Task 1", description = "Description 1", completed = false),
        Task(userId = "2", title = "Task 2", description = "Description 2", completed = true),
        Task(userId = "3", title = "Task 3", description = "Description 3", completed = false),
    )


    fun addNewTask(title: String, description: String) {
        viewModelScope.launch {
            userSessionManager.userIdFlow.collect { userId ->
                if (userId != null) {
                    addTaskUseCase(
                        userId = userId, title = title, description = description, completed = false
                    )
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