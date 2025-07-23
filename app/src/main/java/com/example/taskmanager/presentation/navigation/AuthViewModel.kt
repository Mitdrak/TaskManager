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
        // Usar authStateFlow (más moderno y se gestiona mejor con el scope)
        viewModelScope.launch {

            firebaseAuth.addAuthStateListener { authUser -> // Recolecta los cambios
                if (authUser.currentUser != null) {
                    // Si hay usuario, estamos autenticados
                    Timber.d(
                        "AuthViewModel: Estado GLOBAL = AUTENTICADO (Usuario: " +
                                "${authUser.currentUser?.email})"
                    )
                    viewModelScope.launch {
                        val tasksResult = getAllTasksUseCase() // Llama al caso de uso para obtener tareas
                        if(tasksResult.isSuccess){
                            startObservingTasksUseCase()
                            _authState.value = GlobalAuthState.AUTHENTICATED(
                                AuthUser(
                                    authUser.currentUser?.uid.toString(),
                                    authUser.currentUser?.email
                                )
                            )
                        }else{
                            Timber.e("AuthViewModel: Error al obtener tareas después de autenticarse: ${tasksResult.exceptionOrNull()}")
                            _authState.value = GlobalAuthState.UNAUTHENTICATED // Actualiza Sealed Class
                        }

                    }
                } else {
                    // Si no hay usuario, no estamos autenticados
                    Timber.w("AuthViewModel: Estado GLOBAL = NO AUTENTICADO")
                    _authState.value = GlobalAuthState.UNAUTHENTICATED // Actualiza Sealed Class
                }
            }
        }
    }
}
