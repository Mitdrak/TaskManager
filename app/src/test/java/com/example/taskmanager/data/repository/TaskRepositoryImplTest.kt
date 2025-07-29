package com.example.taskmanager.data.repository

import com.example.taskmanager.data.local.dao.TaskDao
import com.example.taskmanager.data.remote.api.FirebaseService
import com.example.taskmanager.domain.mapper.toEntity
import com.example.taskmanager.domain.model.Task
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.QueryDocumentSnapshot
import io.mockk.Runs
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verifySequence
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import timber.log.Timber


class TaskRepositoryImplTest {
    // Mocks for dependencies
    private val mockTaskDao: TaskDao = mockk()
    private val mockFirebaseService: FirebaseService = mockk()
    private val mockFirebaseAuth: FirebaseAuth = mockk()
    private val mockFirebaseUser: FirebaseUser = mockk() // Mock FirebaseUser for currentUser

    // Repository under test
    private lateinit var repository: TaskRepositoryImpl

    // Coroutine test utilities
    private val testDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher + SupervisorJob()) // Use TestScope for repositoryScope

    // Mock Timber for verification
    private val mockTimberTree = mockk<Timber.Tree>(relaxed = true)


    @Before
    fun setup() {
         Timber.plant(mockTimberTree)
         Dispatchers.setMain(testDispatcher)

         coEvery { mockFirebaseAuth.currentUser } returns mockFirebaseUser
        coEvery { mockFirebaseUser.uid } returns "test_user_id"

         repository = TaskRepositoryImpl(mockTaskDao, mockFirebaseService, mockFirebaseAuth)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown() {
         Dispatchers.resetMain()
         Timber.uprootAll()
         clearAllMocks()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `startObservingTasks observes Firebase and updates Room for ADDED tasks`() = testScope.runTest {
         val userId = "test_user_id"
        val firebaseTask = Task(
            taskId = "1",
            userId = "test_user_id",
            title = "Nueva tarea de prueba",
            timeStart = "14:00",
            timeEnd = "15:00",
            dateStart = Timestamp.now(),
            description = "lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.",
            completed = false,
            createdAt = 1746640559985,
            priority = "Medium",
            taskColor = "#FFB2FFFC"
        )
        val mockDocumentSnapshot: QueryDocumentSnapshot = mockk {
            every { toObject(Task::class.java) } returns firebaseTask // When toObject is called
            every { id } returns "test_user_id" // When id is accessed
        }

        val mockDocumentChange: DocumentChange = mockk {
            every { type } returns DocumentChange.Type.ADDED // The type of change
            every { document } returns mockDocumentSnapshot // The document associated with the change
        }


        // Mock FirebaseService to return a flow of changes
        val firebaseChangesFlow = MutableSharedFlow<Result<DocumentChange>>()
        coEvery { mockFirebaseService.observeAllUserTasks(userId) } returns firebaseChangesFlow

        // Mock TaskDao insert
        coEvery { mockTaskDao.insertTask(firebaseTask.toEntity()) } just Runs


        repository.startObservingTasks()
        advanceUntilIdle() // Allow startObservingTasks to launch its coroutine

        // Simulate Firebase emitting an ADDED event
        firebaseChangesFlow.emit(Result.success(mockDocumentChange))
        advanceUntilIdle()

        // Assert
        coVerify(exactly = 1) { mockFirebaseService.observeAllUserTasks(userId) }
        coVerify(exactly = 0) { mockTaskDao.insertTask(firebaseTask.toEntity()) }
        verifySequence {
            mockTimberTree.d(eq("Repository: Starting to observe tasks for user: $userId"))
            mockTimberTree.d(eq("Repository: New Task Added Firebase: ${firebaseTask.taskId}"))
        }
    }

}
