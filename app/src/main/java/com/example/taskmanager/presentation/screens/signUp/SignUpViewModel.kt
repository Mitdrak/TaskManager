package com.example.taskmanager.presentation.screens.signUp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskmanager.domain.usecase.auth.SignUpWithEmailPassword
import com.example.taskmanager.presentation.screens.signUp.state.ErrorState
import com.example.taskmanager.presentation.screens.signUp.state.SignUpErrorState
import com.example.taskmanager.presentation.screens.signUp.state.SignUpState
import com.example.taskmanager.presentation.screens.signUp.state.SignUpUiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val signUpWithEmailPassword: SignUpWithEmailPassword,
) : ViewModel() {
    private val _signUpState = MutableStateFlow(SignUpState())
    val state: StateFlow<SignUpState> = _signUpState.asStateFlow()
    private val _snackbarEvent = Channel<String>()
    val snackbarEvent = _snackbarEvent.receiveAsFlow()

    fun onUiEvent(signUpUiEvent: SignUpUiEvent) {
        when (signUpUiEvent) {
            is SignUpUiEvent.EmailOrMobileChanged -> {
                _signUpState.update {
                    it.copy(
                        emailOrMobile = signUpUiEvent.inputValue
                    )
                }
            }

            is SignUpUiEvent.PasswordChanged -> {
                _signUpState.update {
                    it.copy(
                        password = signUpUiEvent.inputValue
                    )
                }
            }

            SignUpUiEvent.SignUp -> {
                val email = state.value.emailOrMobile
                val password = state.value.password

                if (email.isNotBlank() && password.isNotBlank()) {
                    _signUpState.update {
                        it.copy(
                            isLoading = true,
                            errorState = SignUpErrorState(
                                emailOrMobileErrorState = ErrorState(),
                                passwordErrorState = ErrorState(),
                                generalErrorState = ErrorState(),
                                emptyFieldErrorState = ErrorState()
                            )
                        )
                    }
                    viewModelScope.launch {
                        signUpWithEmailPassword(email, password).onSuccess {
                            _signUpState.update {
                                it.copy(
                                    isSignUpSuccessful = true,
                                    isLoading = false,
                                )
                            }
                        }.onFailure { exception ->
                            _signUpState.update {
                                it.copy(
                                    snackbarMessage = "Invalid username or password", errorState = SignUpErrorState(
                                        generalErrorState = ErrorState(
                                            hasError = true, errorMessage = exception.message ?: "Unknown error"
                                        )
                                    )
                                )
                            }
                            _snackbarEvent.send(
                                _signUpState.value.snackbarMessage
                            )

                        }
                    }
                }

            }

            SignUpUiEvent.SignUpWithFacebook -> {

            }

            SignUpUiEvent.SignUpWithGoogle -> {

            }

            SignUpUiEvent.ShowPassword -> {
                _signUpState.update {
                    it.copy(
                        showPassword = !it.showPassword
                    )
                }
            }

            SignUpUiEvent.SnackbarDismissed -> {
                onSnackbarDismissed()
            }
        }

    }

    private fun onSnackbarDismissed() {
        _signUpState.update {
            it.copy(
                isLoading = false, errorState = SignUpErrorState(), snackbarMessage = ""
            )
        }
    }
}
