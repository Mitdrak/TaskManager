package com.example.taskmanager.presentation.screens.signUp

import androidx.lifecycle.ViewModel
import com.example.taskmanager.presentation.screens.login.state.LoginState
import com.example.taskmanager.presentation.screens.signUp.state.SignUpUiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject


@HiltViewModel
class SignUpViewModel @Inject constructor() : ViewModel() {
    private val _loginState = MutableStateFlow(LoginState())
    val state: StateFlow<LoginState> = _loginState.asStateFlow()

    fun onUiEvent(signUpUiEvent: SignUpUiEvent) {
        when (signUpUiEvent) {
            is SignUpUiEvent.EmailOrMobileChanged -> TODO()
            is SignUpUiEvent.PasswordChanged -> TODO()
            SignUpUiEvent.SignUp -> TODO()
            SignUpUiEvent.SignUpWithFacebook -> TODO()
            SignUpUiEvent.SignUpWithGoogle -> TODO()
        }
    }

}
