package com.example.taskmanager.presentation.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskmanager.domain.model.AuthUser
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
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
    private val firebaseFirestore: FirebaseFirestore
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
                    /*_authState.value =
                        GlobalAuthState.AUTHENTICATED(
                            AuthUser(
                                authUser.currentUser?.uid.toString(),
                                authUser.currentUser?.email
                            )
                        )*/
                    //
                    // Actualiza
                    // Sealed Class
                    // _authStateSimple.value = AuthState.AUTHENTICATED // Actualiza Enum
                } else {
                    // Si no hay usuario, no estamos autenticados
                    Timber.w("AuthViewModel: Estado GLOBAL = NO AUTENTICADO")
                    _authState.value = GlobalAuthState.UNAUTHENTICATED // Actualiza Sealed Class
                    // _authStateSimple.value = AuthState.UNAUTHENTICATED // Actualiza Enum
                }
            }
        }

        // Alternativa con addAuthStateListener (como lo tenías):
        // Si usas esta, ¡recuerda remover el listener en onCleared()!
        /*
        val authListener = FirebaseAuth.AuthStateListener { auth ->
             val firebaseUser = auth.currentUser
             if (firebaseUser != null) {
                 val authUser = firebaseUser.toDomain()
                 Timber.d("AuthViewModel [Listener]: Estado GLOBAL = AUTENTICADO (Usuario: ${authUser.email})")
                 _authState.value = GlobalAuthState.AUTHENTICATED(authUser)
             } else {
                 Timber.w("AuthViewModel [Listener]: Estado GLOBAL = NO AUTENTICADO")
                 _authState.value = GlobalAuthState.UNAUTHENTICATED
             }
        }
        firebaseAuth.addAuthStateListener(authListener)
        // En onCleared() necesitarías llamar a firebaseAuth.removeAuthStateListener(authListener)
        */

    }

    /*private fun checkAuthState() {
        viewModelScope.launch {
            firebaseAuth.addAuthStateListener { auth ->
                if (auth.currentUser != null) {
                    Timber.d("Usuario autenticado A: ${auth.currentUser?.email}")
                    _authState.value = AuthState.AUTHENTICATED
                } else {
                    Timber.w("Usuario no autenticado")
                    _authState.value = AuthState.UNAUTHENTICATED
                }
            }
        }
    }*/


}