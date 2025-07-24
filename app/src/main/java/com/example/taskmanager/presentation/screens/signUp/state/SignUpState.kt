package com.example.taskmanager.presentation.screens.signUp.state

data class SignUpState(
    val emailOrMobile: String = "",
    val password: String = "",
    val errorState: SignUpErrorState = SignUpErrorState(),
    val isLoading: Boolean = false,
    val processingSignUp: Boolean = false,
    val isSignUpSuccessful: Boolean = false,
    val showPassword: Boolean = false,
    val snackbarMessage: String = "",
)

data class SignUpErrorState(
    val emailOrMobileErrorState: ErrorState = ErrorState(),
    val passwordErrorState: ErrorState = ErrorState(),
    val generalErrorState: ErrorState = ErrorState(),
    var emptyFieldErrorState: ErrorState = ErrorState()
)

data class ErrorState(
    var hasError: Boolean = false,
    val errorMessageStringResource: Int = 0,
    var errorMessage: String = ""
)
