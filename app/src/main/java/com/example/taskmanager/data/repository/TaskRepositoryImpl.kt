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
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
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
                        Timber.d("Repository: Recibidas ${firebaseTasks.size} tareas de Firebase para sincronizar.")
                        val roomEntities = firebaseTasks.map { it.toEntity() }
                        taskDao.deleteAllTasks() // ¡Cuidado si tienes más tablas!
                        taskDao.insertAll(roomEntities)
                        Timber.d("Repository: Room sincronizado con ${roomEntities.size} tareas.")
                    }.onFailure { error ->
                        Timber.e("Repository: Error al observar tareas de Firebase para sincronizar: ${error.message}")
                    }
                }
            } else {
                Timber.w("Repository: No hay usuario autenticado para sincronizar tareas desde Firebase.")
            }
        }
    }

    override suspend fun addTask(task: Task): Result<Unit> {
        return withContext(Dispatchers.IO) {
            Timber.d("Repository: Solicitud de añadir tarea enviada a Firebase: ${task.title}")
            firebaseService.addTaskToFirebase(task)
        }
    }


    override suspend fun getTaskById(taskId: String): Result<Task> {
        return withContext(Dispatchers.IO) {
            val taskEntity = taskDao.getTaskById(taskId).firstOrNull()
            if (taskEntity != null) {
                Result.success(taskEntity.toTask())
            } else {
                Result.failure(Exception("Tarea no encontrada"))
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun observeTasksForDate(selectedDate: LocalDate): Flow<Result<List<Task>>> {
        val startOfDayMillis = selectedDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val endOfDayMillis = selectedDate.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        return withContext(Dispatchers.IO) {
            taskDao.getTaskbyDateRange(startOfDayMillis, endOfDayMillis).map { tasks ->
                Timber.d("Repository: Recibidas ${tasks.size} tareas de Room para la fecha $selectedDate.")
                Result.success(tasks.map { it.toTask() })
            }
        }
    }

    override suspend fun updateTask(task: Task): Result<Unit> {
        val result = firebaseService.updateTask(task)
        return if (result.isSuccess) {
            Timber.d("Tarea actualizada con ID: ${task.taskId}")
            Result.success(Unit)
        } else {
            Timber.e("Error al actualizar la tarea: ${result.exceptionOrNull()?.message}")
            Result.failure(result.exceptionOrNull() ?: Exception("Error al actualizar la tarea"))
        }
    }
}
