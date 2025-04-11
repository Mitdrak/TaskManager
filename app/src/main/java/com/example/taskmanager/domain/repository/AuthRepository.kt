package com.example.taskmanager.domain.repository

import com.example.taskmanager.domain.model.AuthUser
import kotlinx.coroutines.flow.Flow


interface AuthRepository {
    fun observeAuthState(): Flow<AuthUser?>
    suspend fun loginWithEmailPassword(email: String, password: String): Result<Unit>
    suspend fun signUpWithEmailPassword(email: String, password: String): Result<Unit>
    suspend fun logout(): Result<Unit>
}