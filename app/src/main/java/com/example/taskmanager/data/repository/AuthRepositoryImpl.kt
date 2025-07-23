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
    private val firebaseFirestore: FirebaseFirestore,
) : AuthRepository {
    override fun observeAuthState(): Flow<AuthUser?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            Timber.d("AuthState changed: ${firebaseAuth.currentUser?.email}")

            trySend(firebaseAuth.currentUser?.toDomain())
        }
        firebaseAuth.addAuthStateListener(listener)
        awaitClose {
            firebaseAuth.removeAuthStateListener(listener)
        }
    }

    override suspend fun loginWithEmailPassword(
        email: String,
        password: String
    ): Result<Unit> {
        return try {
            Timber.d("Trying to sign in with $email")

            // Sign in
            firebaseAuth.signInWithEmailAndPassword(email, password).await()

            val currentUser = firebaseAuth.currentUser
            if (currentUser == null) {
                Timber.e("Error: User not found after sign in attempt")
                return Result.failure(Exception("Error: User not found"))
            }

            Timber.d("Session successfully started for user: ${currentUser.email}")

            // Now wait for task repository
            /*val tasksResult = taskRepository.getAllTasks() // <- should be suspend and return Result<...>

            return if (tasksResult.isSuccess) {
                Timber.d("Successfully fetched tasks after login.")
                Result.success(Unit)
            } else {
                Timber.e("Failed to fetch tasks after login.")
                Result.failure(Exception("Failed to fetch user tasks"))
            }*/
            Result.success(Unit)

        } catch (e: Exception) {
            Timber.e("Error at signing in: ${e.message}")
            Result.failure(mapFirebaseException(e))
        }
    }

    override suspend fun signUpWithEmailPassword(
        email: String,
        password: String
    ): Result<Unit> {
        return try {
            Timber.d("Trying to sign up with $email")
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
                Timber.d("User registered successfully with ID: $newUserId")
                Result.success(Unit)
            } else {
                Timber.e("Error obtaining user ID after sign up")
                Result.failure(Exception("Error obtaining user ID after sign up"))
            }
        } catch (e: Exception) {
            Timber.e("Error withe the signUp: ${e.message}")
            Result.failure(mapFirebaseException(e))
        }
    }

    override suspend fun getCurrentUser(): Flow<AuthUser?> {
        return callbackFlow {
            val currentUser = firebaseAuth.currentUser
            Timber.d("Actual user: ${currentUser?.email}")
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
            Timber.e("Error closing session : ${e.message}")
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
        return e
    }

}
