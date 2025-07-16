package com.example.taskmanager.presentation.screens.taskDetail

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskmanager.domain.model.Task
import com.example.taskmanager.domain.usecase.task.getTaskByIdUseCase
import com.example.taskmanager.domain.usecase.task.updateTaskUseCase
import com.example.taskmanager.presentation.screens.taskDetail.state.TaskDetailState
import com.example.taskmanager.presentation.screens.taskDetail.state.TaskDetailUiEvent
import com.example.taskmanager.util.TimeUtils
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
    private val getTaskByIdUseCase: getTaskByIdUseCase,
    private val updateTaskUseCase: updateTaskUseCase
) : ViewModel() {
    private val taskId: String? = savedStateHandle["taskId"] ?: ""

    private val _task = MutableStateFlow<Task>(Task())
    val task: StateFlow<Task> = _task.asStateFlow()


    private val _taskDetailState = MutableStateFlow(
        TaskDetailState(
            taskId = taskId ?: "",
            taskTitle = "",
            taskDescription = "",
            taskDueDate = _task.value.dateStart!!,
            timeStart = "",
            timeEnd = "",
            taskPriority = "Normal",
            isCompleted = false,
            isEditing = false
        )
    )
    val taskDetailState: StateFlow<TaskDetailState> = _taskDetailState.asStateFlow()

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
                    _task.value = _task.value.copy(
                        taskId = task.taskId,
                        userId = task.userId,
                        title = task.title,
                        timeStart = TimeUtils.formatTimeWithAmPm(task.timeStart),
                        timeEnd = TimeUtils.formatTimeWithAmPm(task.timeEnd),
                        dateStart = task.dateStart,
                        description = task.description,
                        taskColor = task.taskColor,
                        priority = task.priority,
                        completed = task.completed,
                        createdAt = task.createdAt
                    )
                    _taskDetailState.value = _taskDetailState.value.copy(
                        taskId = task.taskId,
                        taskTitle = task.title,
                        taskDescription = task.description,
                        taskDueDate = task.dateStart!!,
                        timeStart = TimeUtils.formatTimeWithAmPm(task.timeStart),
                        timeEnd = TimeUtils.formatTimeWithAmPm(task.timeEnd),
                        taskPriority = task.priority,
                        isCompleted = task.completed
                    )
                    _taskDetailState.value = _taskDetailState.value.copy(isLoading = false)
                }.onFailure { exception ->
                    // Handle the error
                    Timber.e("Error retrieving task: ${exception.message}")
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun onUiEvent(taskDetailUiEvent: TaskDetailUiEvent) {
        when (taskDetailUiEvent) {
            is TaskDetailUiEvent.TaskTitleChanged -> {
                _taskDetailState.value = _taskDetailState.value.copy(
                    taskTitle = taskDetailUiEvent.inputValue
                )
            }

            is TaskDetailUiEvent.TaskDescriptionChanged -> {
                _taskDetailState.value = _taskDetailState.value.copy(
                    taskDescription = taskDetailUiEvent.inputValue
                )
            }

            is TaskDetailUiEvent.TaskDueDateChanged -> {
                _taskDetailState.value = _taskDetailState.value.copy(
                    taskDueDate = TimeUtils.convertStringToTimestamp(
                        taskDetailUiEvent.inputValue
                    )
                )
            }

            is TaskDetailUiEvent.TaskPriorityChanged -> {
                _taskDetailState.value = _taskDetailState.value.copy(
                    taskPriority = taskDetailUiEvent.inputValue
                )
            }

            TaskDetailUiEvent.ToggleEditMode -> {

                _taskDetailState.value = _taskDetailState.value.copy(
                    isEditing = true,
                    taskTitle = _task.value.title,
                    taskDescription = _task.value.description,
                    timeStart = _task.value.timeStart,
                    timeEnd = _task.value.timeEnd,
                    taskDueDate = _task.value.dateStart!!,
                    taskPriority = _task.value.priority,
                    isCompleted = _task.value.completed
                )

            }

            TaskDetailUiEvent.SaveChanges -> saveChanges()
            TaskDetailUiEvent.CancelEdit -> {
                _taskDetailState.value = _taskDetailState.value.copy(
                    isEditing = false,
                    taskTitle = _task.value.title,
                    taskDescription = _task.value.description,
                    taskDueDate = _task.value.dateStart!!,
                    taskPriority = _task.value.priority,
                    isCompleted = _task.value.completed
                )
            }

            is TaskDetailUiEvent.TaskStartHourChanged -> {
                _taskDetailState.value = _taskDetailState.value.copy(
                    timeStart = taskDetailUiEvent.inputValue
                )
            }

            is TaskDetailUiEvent.TaskEndHourChanged -> {
                _taskDetailState.value = _taskDetailState.value.copy(
                    timeEnd = taskDetailUiEvent.inputValue
                )
            }
        }
    }

    private fun saveChanges() {
        viewModelScope.launch {
            val updatedTask = _task.value.copy(
                title = _taskDetailState.value.taskTitle,
                description = _taskDetailState.value.taskDescription,
                dateStart = _taskDetailState.value.taskDueDate,
                timeStart = _taskDetailState.value.timeStart.replace("AM", "").replace("PM", "").trim(),
                timeEnd = _taskDetailState.value.timeEnd.replace("AM", "").replace("PM", "").trim(),
                priority = _taskDetailState.value.taskPriority,
                completed = _taskDetailState.value.isCompleted
            )
            updateTaskUseCase(updatedTask).onSuccess {
                Timber.d("Task updated successfully: $updatedTask")
                _taskDetailState.value = _taskDetailState.value.copy(isEditing = false)
            }.onFailure { exception ->
                Timber.e("Error updating task: ${exception.message}")
            }
        }

    }


}
