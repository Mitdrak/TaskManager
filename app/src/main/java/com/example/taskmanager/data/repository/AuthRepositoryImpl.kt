package com.example.taskmanager.data.repository

import com.example.taskmanager.domain.model.AuthUser
import com.example.taskmanager.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firebaseFirestore: FirebaseFirestore
) : AuthRepository {
    override fun observeAuthState(): Flow<AuthUser?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            Timber.d("AuthState changed: ${firebaseAuth.currentUser?.email}")
            trySend(firebaseAuth.currentUser?.toDomain())
        }
        firebaseAuth.addAuthStateListener(listener)
        // Limpiar el listener cuando el flow se cierra
        awaitClose {
            firebaseAuth.removeAuthStateListener(listener)
        }
    }

    override suspend fun loginWithEmailPassword(
        email: String,
        password: String
    ): Result<Unit> {
        return try {
            Timber.d("Intentando iniciar sesión con $email")
            firebaseAuth.signInWithEmailAndPassword(
                email,
                password
            ).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.e("Error al iniciar sesión: ${e.message}")
            Result.failure(mapFirebaseException(e))
        }
    }

    override suspend fun signUpWithEmailPassword(
        email: String,
        password: String
    ): Result<Unit> {
        return try {
            Timber.d("Intentando registrarse con $email")
            val authResult = firebaseAuth.createUserWithEmailAndPassword(
                email,
                password
            ).await()
            val newUserId = authResult.user?.uid
            if (newUserId != null) {
                // Guardar el usuario en Firestore
                val userData = mapOf(
                    "email" to email,
                    "name" to "Anonymous",
                    "createdAt" to System.currentTimeMillis()
                )
                firebaseFirestore.collection("users").document(newUserId).set(userData).await()
                Timber.d("Usuario registrado y guardado en Firestore: $newUserId")
                Result.success(Unit)
            } else {
                Timber.e("Error al obtener el ID del usuario después de registrarse")
                Result.failure(Exception("Error al obtener el ID del usuario después de registrarse"))
            }
        } catch (e: Exception) {
            Timber.e("Error al registrarse: ${e.message}")
            Result.failure(mapFirebaseException(e))
        }
    }

    override suspend fun getCurrentUser(): Flow<AuthUser?> {
        return callbackFlow {
            val currentUser = firebaseAuth.currentUser
            Timber.d("Usuario actual: ${currentUser?.email}")
            trySend(currentUser?.toDomain())
            // Limpiar el flow cuando se cierra
            awaitClose { }
        }
    }

    override suspend fun logout(): Result<Unit> {
        return try {
            firebaseAuth.signOut()
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.e("Error al cerrar sesión: ${e.message}")
            Result.failure(mapFirebaseException(e))
        }
    }

    private fun FirebaseUser.toDomain(): AuthUser {
        return AuthUser(
            uid = this.uid,
            email = this.email
        )
    }

    private fun mapFirebaseException(e: Exception): Exception {
        // Aquí puedes convertir FirebaseAuthException específicas a errores de dominio
        return e // Por ahora, devolvemos la misma
    }

}