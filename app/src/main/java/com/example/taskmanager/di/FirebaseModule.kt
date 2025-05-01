package com.example.taskmanager.di

import com.example.taskmanager.data.repository.AuthRepositoryImpl
import com.example.taskmanager.data.repository.TaskRepositoryImpl
import com.example.taskmanager.domain.manager.UserSessionManager
import com.example.taskmanager.domain.repository.AuthRepository
import com.example.taskmanager.domain.repository.TaskRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

    @Provides
    @Singleton
    fun provideAuthRepository(
        firebaseAuth: FirebaseAuth,
        firebaseFirestore: FirebaseFirestore
    ): AuthRepository {
        return AuthRepositoryImpl(
            firebaseAuth,
            firebaseFirestore
        )
    }

    @Provides
    @Singleton
    fun provideUserSessionManager(authRepository: AuthRepository): UserSessionManager =
        UserSessionManager(
            authRepository = authRepository
        )

    @Provides
    @Singleton
    fun provideTaskRepository(
        firebaseFirestore: FirebaseFirestore,
        firebaseAuth: FirebaseAuth
    ): TaskRepository {
        return TaskRepositoryImpl(
            firebaseFirestore,
            firebaseAuth
        )
    }
}