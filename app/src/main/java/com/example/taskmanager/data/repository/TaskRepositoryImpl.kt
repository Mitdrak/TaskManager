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
                                Timber.d("Repository: New Task Added Firebase: ${firebaseTasks.document.id}")
                                val task = firebaseTasks.document.toObject(Task::class.java)
                                val roomEntities = task.toEntity()
                                taskDao.insertTask(roomEntities)
                            }

                            DocumentChange.Type.MODIFIED -> {
                                Timber.d("Repository: Task Updated Firebase: ${firebaseTasks.document.id}")
                                val task = firebaseTasks.document.toObject(Task::class.java)
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

    override suspend fun addTask(task: Task): Result<Unit> {
        return withContext(Dispatchers.IO) {
            Timber.d("Repository: Adding task to firebase: ${task.title}")
            firebaseService.addTaskToFirebase(task)
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
}
