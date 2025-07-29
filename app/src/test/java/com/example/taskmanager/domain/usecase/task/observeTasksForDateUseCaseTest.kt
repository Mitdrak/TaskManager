package com.example.taskmanager.domain.usecase.task

import com.example.taskmanager.domain.model.Task
import com.example.taskmanager.domain.repository.TaskRepository
import com.google.firebase.Timestamp
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import java.time.LocalDate
import kotlin.test.assertEquals


class ObserveTasksForDateUseCaseTest {

    private lateinit var mockTaskRepository: TaskRepository
    private lateinit var observeTasksForDateUseCase: observeTasksForDateUseCase

    @Before
    fun setup(){
        mockTaskRepository = mockk()
        observeTasksForDateUseCase = observeTasksForDateUseCase(mockTaskRepository)

    }

    @Test
    fun `invoke returns succes with tasks from repository`() = runTest {
        // Arrange
        val testDate = LocalDate.of(2025, 7, 28)
        val tasks = listOf(
            Task(
                taskId = "1",
                userId = "1",
                title = "Nueva tarea de prueba",
                timeStart = "14:00",
                timeEnd = "15:00",
                dateStart = Timestamp.now(),
                description = "lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.",
                completed = false,
                createdAt = 1746640559985,
                priority = "Medium",
                taskColor = "#FFB2FFFC" // Example color
            ),
            Task(
                taskId = "2",
                userId = "2",
                title = "Nueva tarea de prueba 2",
                timeStart = "14:00",
                timeEnd = "15:00",
                dateStart = Timestamp.now(),
                description = "lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.",
                completed = true,
                createdAt = 1746640559985,
                priority = "Medium",
                taskColor = "#FFB2FFFC" // Example color
            )
        )
        // Mock the repository to return a successful flow
        coEvery { mockTaskRepository.observeTasksForDate(testDate) } returns flowOf(Result.success(tasks))

        // Act
        val resultFlow = observeTasksForDateUseCase(testDate)
        val result = resultFlow.first() // Collect the first emission

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(tasks, result.getOrNull())
    }
    @Test
    fun `invoke returns empty list when repository returns empty list`() = runTest {
        // Arrange
        val testDate = LocalDate.of(2025, 7, 28)
        val emptyTasks = emptyList<Task>()
        coEvery { mockTaskRepository.observeTasksForDate(testDate) } returns flowOf(Result.success(emptyTasks))

        // Act
        val resultFlow = observeTasksForDateUseCase(testDate)
        val result = resultFlow.first()

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(emptyTasks, result.getOrNull())
    }
}


