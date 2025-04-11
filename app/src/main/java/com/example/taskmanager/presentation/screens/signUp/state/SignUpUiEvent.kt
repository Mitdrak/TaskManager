package com.example.taskmanager.presentation.screens.signUp.state

sealed class SignUpUiEvent {
    data class EmailOrMobileChanged(val inputValue: String) : SignUpUiEvent()
    data class PasswordChanged(val inputValue: String) : SignUpUiEvent()
    object SignUpWithGoogle : SignUpUiEvent()
    object SignUpWithFacebook : SignUpUiEvent()
    object SignUp : SignUpUiEvent()
}