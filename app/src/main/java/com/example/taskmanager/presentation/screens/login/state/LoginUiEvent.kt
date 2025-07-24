package com.example.taskmanager.presentation.screens.login.state

sealed class LoginUiEvent {
    data class EmailOrMobileChanged(val inputValue: String) : LoginUiEvent()
    data class PasswordChanged(val inputValue: String) : LoginUiEvent()
    object ShowPassword : LoginUiEvent()
    object Submit : LoginUiEvent()
    object SignInWithGoogle : LoginUiEvent()
    object SignInWithFacebook : LoginUiEvent()
    object RememberMe : LoginUiEvent()
    object SignUp : LoginUiEvent()
    object SnackbarDismissed : LoginUiEvent()
}
