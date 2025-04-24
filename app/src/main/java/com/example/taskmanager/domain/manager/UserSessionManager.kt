package com.example.taskmanager.domain.manager

import com.example.taskmanager.domain.model.AuthUser
import com.example.taskmanager.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class UserSessionManager @Inject constructor(
    private val authRepository: AuthRepository,
) {
    val authUserFlow: Flow<AuthUser?> = authRepository.observeAuthState()

    val userIdFlow: Flow<String?> = authRepository.observeAuthState().map { authUser ->
        authUser?.uid
    }

}