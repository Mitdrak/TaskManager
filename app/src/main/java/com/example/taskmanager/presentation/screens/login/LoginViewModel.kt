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
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
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

    val state = _loginState.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        LoginState()
    )
    private val _snackbarEvent = Channel<String>()
    val snackbarEvent = _snackbarEvent.receiveAsFlow()
    // In your ViewModel
    private val _permissionRequestEvents = MutableSharedFlow<Unit>()
    val permissionRequestEvents: SharedFlow<Unit> = _permissionRequestEvents.asSharedFlow()

    init {
        observeAuthState()
    }
    fun onEnableNotificationsClicked() {
        // You might check if permission is needed and then emit the event
        viewModelScope.launch {
            _permissionRequestEvents.emit(Unit)
        }
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
                    _loginState.update {
                        it.copy(isLoading = true)
                    }
                    val result = loginWithEmailAndPasswordUseCase(
                        email = _loginState.value.emailOrMobile,
                        password = _loginState.value.password
                    )
                    result.onSuccess {
                        Timber.d("Login successful")
                        _loginState.update {
                            it.copy(
                                isLoginSuccessful = true,
                                isLoading = true
                            )
                        }
                    }.onFailure {
                        Timber.e("Login failed: ${it.message}")
                        _loginState.update {
                            it.copy(
                                errorState = LoginErrorState(
                                    generalErrorState = ErrorState(
                                        hasError = true,
                                        errorMessage = "Invalid username or password"
                                    )
                                ),
                            )
                        }
                        _snackbarEvent.send(
                            _loginState.value.errorState.generalErrorState.errorMessage
                        )
                    }
                }
            }

            LoginUiEvent.SnackbarDismissed -> {
                onSnackbarDismissed()
            }
            LoginUiEvent.ShowPassword -> {
                Timber.d("Toggle show password")
                _loginState.update {
                    it.copy(
                        showPassword = !it.showPassword
                    )
                }
            }
        }
    }

    private fun onSnackbarDismissed() {
        Timber.d("Snackbar dismissed")
        _loginState.update {
            it.copy(
                isLoading = false,
                errorState = LoginErrorState(),
                snackbarMessage = ""
            )
        }
    }
}
