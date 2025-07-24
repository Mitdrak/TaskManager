package com.example.taskmanager.presentation.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskmanager.domain.model.AuthUser
import com.example.taskmanager.domain.usecase.task.getAllTasksUseCase
import com.example.taskmanager.domain.usecase.task.startObservingTasksUseCase
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

enum class AuthState {
    UNKNOWN, AUTHENTICATED, UNAUTHENTICATED, AUTHENTICATING
}

sealed class GlobalAuthState {
    object UNKNOWN : GlobalAuthState()
    object UNAUTHENTICATED : GlobalAuthState()
    data class AUTHENTICATED(val user: AuthUser) : GlobalAuthState() // Puede contener info básica
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val getAllTasksUseCase: getAllTasksUseCase,
    private val startObservingTasksUseCase: startObservingTasksUseCase
) : ViewModel() {
    private val _authState = MutableStateFlow<GlobalAuthState>(GlobalAuthState.UNKNOWN)
    val authState: StateFlow<GlobalAuthState> = _authState

    init {
        observeAuthState()
    }

    private fun observeAuthState() {
        viewModelScope.launch {
            firebaseAuth.addAuthStateListener { authUser ->
                if (authUser.currentUser != null) {
                    Timber.d(
                        "AuthViewModel: Estado GLOBAL = AUTENTICADO (Usuario: " +
                                "${authUser.currentUser?.email})"
                    )
                    viewModelScope.launch {
                        val tasksResult = getAllTasksUseCase()
                        if (tasksResult.isSuccess) {
                            startObservingTasksUseCase()
                            _authState.value = GlobalAuthState.AUTHENTICATED(
                                AuthUser(
                                    authUser.currentUser?.uid.toString(),
                                    authUser.currentUser?.email
                                )
                            )
                        } else {
                            Timber.e("AuthViewModel: User without tasks: ${tasksResult.exceptionOrNull()}")
                            startObservingTasksUseCase()
                            _authState.value = GlobalAuthState.AUTHENTICATED(
                                AuthUser(
                                    authUser.currentUser?.uid.toString(),
                                    authUser.currentUser?.email
                                )
                            )
                        }

                    }
                } else {
                    Timber.w("AuthViewModel: Glogal state = UNAUTHENTICATED")
                    _authState.value = GlobalAuthState.UNAUTHENTICATED
                }
            }
        }
    }
}
