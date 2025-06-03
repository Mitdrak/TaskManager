package com.example.taskmanager.domain.usecase.auth

import com.example.taskmanager.domain.repository.AuthRepository
import jakarta.inject.Inject

class LogOutUseCase @Inject constructor(private val authRepository: AuthRepository) {
    suspend operator fun invoke(): Result<Unit> {
        return authRepository.logout()
    }

}