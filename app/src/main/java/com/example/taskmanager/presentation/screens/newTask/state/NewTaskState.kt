package com.example.taskmanager.presentation.screens.newTask.state


data class NewTaskState(
    val title: String = "",
    val description: String = "",
    val timeStart: String = "",
    val timeEnd: String = "",
    val date: String = "",
    val taskColor: String = "#FFFFFF", // Default color white
    val priority: String = "Low", // Default priority
    val isLoading: Boolean = false,
    val isTaskAdded: Boolean = false,
    val snackBarMessage: String = "",
    val errorState: NewTaskErrorState = NewTaskErrorState()
)

data class NewTaskErrorState(
    val titleErrorState: ErrorState = ErrorState(),
    val descriptionErrorState: ErrorState = ErrorState(),
    val timeStartErrorState: ErrorState = ErrorState(),
    val timeEndErrorState: ErrorState = ErrorState(),
    val dateErrorState: ErrorState = ErrorState(),
    var emptyFieldErrorState: ErrorState = ErrorState(),
    val generalErrorState: ErrorState = ErrorState(),
)

data class ErrorState(
    var hasError: Boolean = false,
    val errorMessageStringResource: Int = 0,
    var errorMessage: String = ""
)
