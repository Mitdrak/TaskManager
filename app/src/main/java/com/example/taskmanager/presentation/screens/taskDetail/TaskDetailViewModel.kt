package com.example.taskmanager.presentation.screens.taskDetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskmanager.domain.model.Task
import com.example.taskmanager.domain.usecase.task.getTaskByIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject


@HiltViewModel
class TaskDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getTaskByIdUseCase: getTaskByIdUseCase
) : ViewModel() {
    private val taskId: String? = savedStateHandle["taskId"] ?: ""

    private val _task = MutableStateFlow<Task>(Task())
    val task: StateFlow<Task> = _task.asStateFlow()

    init {
        taskId?.let {
            getTaskById(it)
        }
    }

    fun getTaskById(taskId: String) {
        viewModelScope.launch {
            getTaskByIdUseCase(taskId).collect { result ->
                result.onSuccess { task ->
                    Timber.d("Task retrieved successfully: $task")
                    _task.value = task
                }.onFailure { exception ->
                    // Handle the error
                    Timber.e("Error retrieving task: ${exception.message}")
                }
            }
        }
    }
}
