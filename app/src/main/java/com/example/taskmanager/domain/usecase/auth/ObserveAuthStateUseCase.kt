package com.example.taskmanager.domain.usecase.auth

import com.example.taskmanager.domain.model.AuthUser
import com.example.taskmanager.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveAuthStateUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    operator fun invoke(): Flow<AuthUser?> {
        return authRepository.observeAuthState()
    }
}