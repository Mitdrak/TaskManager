package com.example.taskmanager.presentation.screens.home

import com.example.taskmanager.domain.model.Task
import com.example.taskmanager.domain.usecase.auth.LogOutUseCase
import com.example.taskmanager.domain.usecase.task.deleteAllTasksUseCase
import com.example.taskmanager.domain.usecase.task.observeTasksForDateUseCase
import com.example.taskmanager.domain.usecase.task.stopObservingTasksUseCase
import com.example.taskmanager.domain.usecase.task.updateTaskUseCase
import com.google.firebase.Timestamp
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import timber.log.Timber
import java.io.IOException
import java.time.LocalDate
import kotlin.test.assertEquals

@ExperimentalCoroutinesApi
class HomeViewModelTest {
    private lateinit var viewModel: HomeViewModel
    private val mockDeleteAllTasksUseCase: deleteAllTasksUseCase = mockk()
    private val mockLogOutUseCase: LogOutUseCase = mockk()
    private val mockUpdateTaskUseCase: updateTaskUseCase = mockk()
    private val mockObserveTasksForDateUseCase: observeTasksForDateUseCase = mockk()
    private val mockStopObservingTaskUseCase: stopObservingTasksUseCase = mockk()

    private val testDispatcher = StandardTestDispatcher()
    private val mockTimberTree = mockk<Timber.Tree>(relaxed = true)
    private val tasksFlow = MutableStateFlow<List<Task>>(emptyList())
    private val rawTasksFlow = MutableStateFlow<List<Task>>(emptyList())


    @Before
    fun setup() {
        Timber.plant(mockTimberTree)

        Dispatchers.setMain(testDispatcher)

        coEvery { mockObserveTasksForDateUseCase(any()) } returns rawTasksFlow
            .map { tasks ->
                Result.success(tasks) // Transform each emitted List<Task> into a Result.success(List<Task>)
            }
            .onStart {} // Keep the onStart/onCompletion if your ViewModel's flow expects them
            .onCompletion {}

        coEvery { mockStopObservingTaskUseCase() } just Runs
        viewModel = HomeViewModel(
            deleteAlltasksUseCase = mockDeleteAllTasksUseCase,
            logOutUseCase = mockLogOutUseCase,
            updateTaskUseCase = mockUpdateTaskUseCase,
            observeTasksForDateUseCase = mockObserveTasksForDateUseCase,
            stopObservingTasksUseCase = mockStopObservingTaskUseCase,
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        // Unplant Timber
        Timber.uprootAll()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `init block observes tasks for current date and filters them`() = runTest {
        // Given
        val taskcompleted1 = Task(
            taskId = "1",
            userId = "1",
            title = "Nueva tarea de prueba",
            timeStart = "14:00",
            timeEnd = "15:00",
            dateStart = Timestamp.now(),
            description = "lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.",
            completed = true,
            createdAt = 1746640559985,
            priority = "Medium",
            taskColor = "#FFB2FFFC" // Example color
        )
        val taskCompleted2 = Task(
            taskId = "3",
            userId = "1",
            title = "Tarea completada",
            timeStart = "18:00",
            timeEnd = "19:00",
            dateStart = Timestamp.now(),
            description = "lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.",
            completed = true,
            createdAt = 1746640559985,
            priority = "Low",
            taskColor = "#FFB2FFFC" // Example color
        )
        val taskIncompleted1 = Task(
            taskId = "2",
            userId = "1",
            title = "Otra tarea de prueba",
            timeStart = "16:00",
            timeEnd = "17:00",
            dateStart = Timestamp.now(),
            description = "lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.",
            completed = false,
            createdAt = 1746640559985,
            priority = "High",
            taskColor = "#FFB2FFFC" // Example color
        )
        val taskIncompleted2 = Task(
            taskId = "4",
            userId = "1",
            title = "Tarea incompleta",
            timeStart = "20:00",
            timeEnd = "21:00",
            dateStart = Timestamp.now(),
            description = "lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.",
            completed = false,
            createdAt = 1746640559985,
            priority = "Low",
            taskColor = "#FFB2FFFC" // Example color
        )

        val dummyTasks = listOf<Task>(
            taskcompleted1,
            taskIncompleted1,
            taskCompleted2,
            taskIncompleted2
        )
        rawTasksFlow.emit(dummyTasks)

        advanceUntilIdle()
        assertEquals(listOf(taskIncompleted1, taskIncompleted2), viewModel.tasks.first())
        assertEquals(listOf(taskcompleted1, taskCompleted2), viewModel.tasksCompleted.first())
        val capturedDate = slot<LocalDate>()
        coVerify { mockObserveTasksForDateUseCase(capture(capturedDate)) }


        assertEquals(LocalDate.now(), capturedDate.captured)

    }

    @Test
    fun `observeTasksForDate handles empty tasks list correctly`() = runTest {
        // Arrange: Make the flow emit an empty list
        rawTasksFlow.emit(emptyList())

        // Act
        advanceUntilIdle()

        // Assert
        assertEquals(emptyList<Task>(), viewModel.tasks.first())
        assertEquals(emptyList<Task>(), viewModel.tasksCompleted.first())
    }
    @Test
    fun `observeTasksForDate handles error gracefully`() = runTest {
        // Arrange: Directly mock the use case to return a flow containing an error
        coEvery { mockObserveTasksForDateUseCase(any()) } returns flowOf(Result.failure(IOException("No tasks found for date")))

        // Act: Call the method explicitly (or rely on init if this is the first test)
        viewModel.observeTasksForDate() // Explicitly call to trigger this specific flow emission
        advanceUntilIdle()

        // Assert
        assertEquals(emptyList<Task>(), viewModel.tasks.first())
        assertEquals(emptyList<Task>(), viewModel.tasksCompleted.first())
        verify { mockTimberTree.e(any<Throwable>(), "Error fetching tasks: %s", "No tasks found for date") } // Verify error logging

    }
    @Test
    fun `updateTask calls updateTaskUseCase`() = runTest {
        // Arrange: Define behavior for updateTaskUseCase
        val taskToUpdate = Task(
            taskId = "",
            userId = "1",
            title = "Updated Task",
            timeStart = "10:00",
            timeEnd = "11:00",
            dateStart = Timestamp.now(),
            description = "Updated description",
            completed = false,
            createdAt = 1746640559985,
            priority = "High",
            taskColor = "#FFB2FFFC" // Example color
        )
        coEvery { mockUpdateTaskUseCase(taskToUpdate) }.returns(Result.success(Unit))    // Just complete successfully

        // Act
        viewModel.updateTask(taskToUpdate)
        advanceUntilIdle() // Let the coroutine finish

        // Assert: Verify that the use case was called with the correct task
        coVerify(exactly = 1) { mockUpdateTaskUseCase(taskToUpdate) }
    }
    @Test
    fun `logout performs logout and deletes all tasks on success`() = runTest {
        // Arrange: Define success behavior for logOutUseCase and deleteAllTasksUseCase
        coEvery { mockLogOutUseCase() } returns Result.success(Unit)
        coEvery { mockDeleteAllTasksUseCase() } returns Result.success(Unit)

        // Act
        viewModel.logout()
        advanceUntilIdle() // Let all launched coroutines complete

        // Assert: Verify that both use cases were called
        coVerify(exactly = 1) { mockLogOutUseCase() }
        coVerify(exactly = 1) { mockDeleteAllTasksUseCase() }
        coVerify(exactly = 1) { mockStopObservingTaskUseCase() } // Verify stop observing is called
        verify { mockTimberTree.d("User logged out successfully") }
        verify { mockTimberTree.d("All tasks deleted successfully") }
    }
    @Test
    fun `logout handles logOutUseCase failure`() = runTest {
        // Arrange: Define failure behavior for logOutUseCase
        val logoutError = Exception("Failed to log out")
        coEvery { mockLogOutUseCase() } returns Result.failure(logoutError)
        // Ensure deleteAllTasksUseCase is NOT called if logout fails
        coEvery { mockDeleteAllTasksUseCase() } throws AssertionError("deleteAllTasksUseCase should not be called")

        // Act
        viewModel.logout()
        advanceUntilIdle()

        // Assert: Verify only logOutUseCase was called and an error was logged
        coVerify(exactly = 1) { mockLogOutUseCase() }
        coVerify(exactly = 0) { mockDeleteAllTasksUseCase() } // Crucial: Verify it was NOT called
        coVerify(exactly = 0) { mockStopObservingTaskUseCase() }
        /*verify { mockTimberTree.e(logoutError, "Error logging out: %s", "Failed to log out") }*/
        verify { mockTimberTree.e(any<Throwable>(),"Error logging out: %s", logoutError.message) }

    }
    @Test
    fun `logout handles deleteAllTasksUseCase failure after successful logout`() = runTest {
        // Arrange: Log out success, but delete tasks fails
        val deleteError = Exception("Failed to delete tasks")
        coEvery { mockLogOutUseCase() } returns Result.success(Unit)
        coEvery { mockDeleteAllTasksUseCase() } returns Result.failure(deleteError)

        // Act
        viewModel.logout()
        advanceUntilIdle()

        // Assert: Both logOut and deleteAll should be called, but the error logged for delete
        coVerify(exactly = 1) { mockLogOutUseCase() }
        coVerify(exactly = 1) { mockDeleteAllTasksUseCase() }
        coVerify(exactly = 0) { mockStopObservingTaskUseCase() } // Stop observing should still be called
        verify { mockTimberTree.d("User logged out successfully") }
        verify { mockTimberTree.e(deleteError, "Error deleting all tasks: %s", "Failed to delete tasks") }
    }
}
