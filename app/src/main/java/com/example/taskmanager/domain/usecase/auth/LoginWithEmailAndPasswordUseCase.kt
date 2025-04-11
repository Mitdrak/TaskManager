package com.example.taskmanager.domain.usecase.auth

import com.example.taskmanager.domain.repository.AuthRepository
import javax.inject.Inject

class LoginWithEmailAndPasswordUseCase @Inject constructor(private val authRepository: AuthRepository) {
    suspend operator fun invoke(email: String, password: String): Result<Unit> {
        return authRepository.loginWithEmailPassword(email, password)
    }
}