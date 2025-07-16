package com.example.taskmanager.data.repository

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.taskmanager.data.local.dao.TaskDao
import com.example.taskmanager.data.local.entity.TaskEntity
import com.example.taskmanager.data.local.mapper.toTask
import com.example.taskmanager.data.remote.api.FirebaseService
import com.example.taskmanager.domain.mapper.toEntity
import com.example.taskmanager.domain.model.Task
import com.example.taskmanager.domain.repository.TaskRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class TaskRepositoryImpl @Inject constructor(
    private val taskDao: TaskDao,
    private val firebaseService: FirebaseService, // Tu FirebaseService real
    private val firebaseAuth: FirebaseAuth
) : TaskRepository {
    private val repositoryScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    val allTasksFromRoom: Flow<List<TaskEntity>> = taskDao.getAllTasks()

    init {
        repositoryScope.launch {
            val userId = firebaseAuth.currentUser?.uid
            if (userId != null) {
                firebaseService.observeAllUserTasks(userId).collect { result ->
                    result.onSuccess { firebaseTasks ->
                        when (firebaseTasks.type) {
                            DocumentChange.Type.ADDED -> {
                                val task = firebaseTasks.document.toObject(Task::class.java)
                                if (task.taskId.isNotEmpty()) {
                                    Timber.d("Repository: New Task Added Firebase: ${task.taskId}")
                                    val roomEntities = task.toEntity()
                                    taskDao.insertTask(roomEntities)
                                } else {
                                    //Insert with temporary ID
                                    val tempTask = task.copy(taskId = firebaseTasks.document.id)
                                    Timber.d("Repository: New Task Added Firebase with temp ID: ${tempTask.taskId}")
                                    val roomEntities = tempTask.toEntity()
                                    taskDao.insertTask(roomEntities)
                                }
                                /*val roomEntities = task.toEntity()
                                taskDao.insertTask(roomEntities)*/
                            }

                            DocumentChange.Type.MODIFIED -> {
                                val task = firebaseTasks.document.toObject(Task::class.java)
                                Timber.d("Repository: Task Updated Firebase: ${task.taskId}")
                                val roomEntities = task.toEntity()
                                taskDao.updateTask(roomEntities)
                            }

                            DocumentChange.Type.REMOVED -> {
                                Timber.d("Repository: Task eliminated from Firebase: ${firebaseTasks.document.id}")
                                taskDao.deleteTask(firebaseTasks.document.id)
                                Timber.d("Repository: Task eliminated from Room with ID: ${firebaseTasks.document.id}")
                                return@collect
                            }
                        }
                    }.onFailure { error ->
                        Timber.e("Repository: Error  : ${error.message}")
                    }
                }
            } else {
                Timber.w("Repository: No user logged in, cannot observe tasks.")
            }
        }
    }

    override suspend fun getAllTasks(): Result<Unit> {
        return withContext(Dispatchers.IO) {
            Timber.d("Repository: Fetching all tasks from Firebase")
            val userId = firebaseAuth.currentUser?.uid
            if (userId != null) {
                val result = firebaseService.getAllTasksOnce(userId)
                return@withContext if (result.isSuccess) {
                    Timber.d("Repository: Tasks fetched successfully from Firebase")
                    val tasks = result.getOrDefault(emptyList())
                    taskDao.insertAll(tasks.map { it.toEntity() })
                    Result.success(Unit)
                } else {
                    Timber.e("Repository: Error fetching tasks: ${result.exceptionOrNull()?.message}")
                    Result.failure(result.exceptionOrNull() ?: Exception("Error fetching tasks"))
                }
            } else {
                Timber.w("Repository: No user logged in, cannot fetch tasks.")
                Result.failure(Exception("No user logged in"))
            }
        }
    }

    override suspend fun addTask(task: Task): Result<Unit> {
        return withContext(Dispatchers.IO) {
            Timber.d("Repository: Adding task to firebase: ${task.title}")
            val result = firebaseService.addTaskToFirebase(task)
            return@withContext if (result.isSuccess) {
                Timber.d("Repository: Task added successfully to firebase: ${task.title}")
                Result.success(Unit)
            } else {
                Timber.e("Repository: Error adding task: ${result.exceptionOrNull()?.message}")
                Result.failure(result.exceptionOrNull() ?: Exception("Error adding task"))
            }
        }
    }


    override suspend fun getTaskById(taskId: String): Flow<Result<Task>> {
        return withContext(Dispatchers.IO) {
            taskDao.getTaskById(taskId).map {
                it.let { taskEntity ->
                    Timber.d("Repository: Task obtained by ID: $taskId")
                    Result.success(taskEntity.toTask())
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun observeTasksForDate(selectedDate: LocalDate): Flow<Result<List<Task>>> {
        val startOfDayMillis = selectedDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val endOfDayMillis = selectedDate.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        return withContext(Dispatchers.IO) {
            taskDao.getTaskbyDateRange(startOfDayMillis, endOfDayMillis).map { tasks ->
                Timber.d("Repository : ${tasks.size} Task obtained for date: $selectedDate")
                Result.success(tasks.map { it.toTask() })
            }
        }
    }

    override suspend fun updateTask(task: Task): Result<Unit> {
        val result = firebaseService.updateTask(task)
        return if (result.isSuccess) {
            Timber.d("Task updated successfully to firebase: ${task.title}")
            Result.success(Unit)
        } else {
            Timber.e("Error with the update: ${result.exceptionOrNull()?.message}")
            Result.failure(result.exceptionOrNull() ?: Exception("Error updating task"))
        }
    }

    override suspend fun deleteTask(taskId: String): Result<Unit> {
        return withContext(Dispatchers.IO) {
            Timber.d("Repository: Deleting task from firebase: $taskId")
            val result = firebaseService.deleteTask(taskId)
            return@withContext if (result.isSuccess) {
                Timber.d("Repository: Task deleted successfully from firebase: $taskId")
                taskDao.deleteTask(taskId)
                Result.success(Unit)
            } else {
                Timber.e("Repository: Error deleting task: ${result.exceptionOrNull()?.message}")
                Result.failure(result.exceptionOrNull() ?: Exception("Error deleting task"))
            }
        }
    }

    override suspend fun deleteAllTasks(): Result<Unit> {
        return withContext(Dispatchers.IO) {
            Timber.d("Repository: Deleting all tasks from firebase")
            val result = taskDao.deleteAllTasks()
            return@withContext if (result > 0) {
                Timber.d("Repository: All tasks deleted successfully from firebase")
                Result.success(Unit)
            } else {
                Timber.e("Repository: Error deleting all tasks")
                Result.failure(Exception("Error deleting all tasks"))
            }
        }
    }
}
