package com.example.taskmanager.presentation.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

enum class AuthState {
    UNKNOWN, AUTHENTICATED, UNAUTHENTICATED,
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {
    private val _authState = MutableStateFlow(AuthState.UNKNOWN)
    val authState: StateFlow<AuthState> = _authState

    init {
        checkAuthState()
    }

    private fun checkAuthState() {
        viewModelScope.launch {
            firebaseAuth.addAuthStateListener { auth ->
                if (auth.currentUser != null) {
                    Timber.d("Usuario autenticado: ${auth.currentUser?.email}")
                    _authState.value = AuthState.AUTHENTICATED
                } else {
                    Timber.w("Usuario no autenticado")
                    _authState.value = AuthState.UNAUTHENTICATED
                }
            }
            if (firebaseAuth.currentUser != null) {
                Timber.d("Usuario autenticado: ${firebaseAuth.currentUser?.email}")
                _authState.value = AuthState.AUTHENTICATED
            } else {
                Timber.w("Usuario no autenticado")
                _authState.value = AuthState.UNAUTHENTICATED
            }
        }
    }
}