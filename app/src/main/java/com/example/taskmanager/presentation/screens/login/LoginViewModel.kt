package com.example.taskmanager.presentation.screens.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskmanager.domain.usecase.auth.LoginWithEmailAndPasswordUseCase
import com.example.taskmanager.domain.usecase.auth.ObserveAuthStateUseCase
import com.example.taskmanager.presentation.screens.login.state.ErrorState
import com.example.taskmanager.presentation.screens.login.state.LoginErrorState
import com.example.taskmanager.presentation.screens.login.state.LoginState
import com.example.taskmanager.presentation.screens.login.state.LoginUiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginWithEmailAndPasswordUseCase: LoginWithEmailAndPasswordUseCase,
    private val observeAuthStateUseCase: ObserveAuthStateUseCase
) : ViewModel() {
    private val _loginState = MutableStateFlow(LoginState())
    val state: StateFlow<LoginState> = _loginState.asStateFlow()

    init {
        observeAuthState()
    }

    private fun observeAuthState() {
        viewModelScope.launch {
            observeAuthStateUseCase().collect { authUser ->
                Timber.d("Auth state changed: $authUser")
                if (authUser != null) {
                    _loginState.update {
                        it.copy(
                            isLoginSuccessful = true,
                            isLoading = false
                        )
                    }
                }
            }
        }
    }

    fun onUiEvent(loginUiEvent: LoginUiEvent) {
        when (loginUiEvent) {
            is LoginUiEvent.EmailOrMobileChanged -> {
                _loginState.value = _loginState.value.copy(
                    emailOrMobile = loginUiEvent.inputValue
                )
            }

            is LoginUiEvent.PasswordChanged -> {
                _loginState.value = _loginState.value.copy(
                    password = loginUiEvent.inputValue
                )
            }

            LoginUiEvent.RememberMe -> TODO()
            LoginUiEvent.SignInWithFacebook -> TODO()
            LoginUiEvent.SignInWithGoogle -> TODO()
            LoginUiEvent.SignUp -> TODO()
            is LoginUiEvent.Submit -> {
                Timber.d("LoginViewModel Submit button clicked")
                viewModelScope.launch {
                    val result = loginWithEmailAndPasswordUseCase(
                        email = _loginState.value.emailOrMobile,
                        password = _loginState.value.password
                    )
                    /*val result = loginWithEmailAndPasswordUseCase(
                        email = "sergio.acs@hotmail.com",
                        password = "123123"
                    )*/
                    result.onSuccess {
                        Timber.d("Login successful")
                        _loginState.update {
                            it.copy(
                                isLoginSuccessful = true,
                                isLoading = false
                            )
                        }
                    }.onFailure {
                        _loginState.update {
                            it.copy(
                                errorState = LoginErrorState(
                                    generalErrorState = ErrorState(
                                        hasError = true,
                                        errorMessage = "Unknown error"
                                    )
                                ),
                                isLoading = false
                            )
                        }
                    }
                }
            }
        }
    }
}