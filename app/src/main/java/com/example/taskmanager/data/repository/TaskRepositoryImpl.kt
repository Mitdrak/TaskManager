package com.example.taskmanager.data.repository

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.taskmanager.domain.model.Task
import com.example.taskmanager.domain.repository.TaskRepository
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class TaskRepositoryImpl @Inject constructor(
    private val firebaseFirestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth
) : TaskRepository {
    override suspend fun addTask(task: Task): Result<Unit> {
        return try {
            Timber.d("Agregando tarea: $task")
            val collection =
                firebaseFirestore.collection("users").document(task.userId).collection("tasks")
                    .add(task)
            collection.addOnSuccessListener {
                Timber.d("Tarea agregada con ID: ${collection}")
                val taskId = it.id
                it.update(
                    "taskId",
                    taskId
                )
            }
            collection.addOnFailureListener {
                Timber.d("Tarea fallo")
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.e("Error al agregar la tarea: ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun getTasks(userId: String): Result<List<Task>> {
        return try {
            Timber.d("Obteniendo tareas para el usuario: $userId")
            val tasks = firebaseFirestore.collection("users").document(userId).collection("tasks")
                .get()
                .await()
                .toObjects(Task::class.java)
            Result.success(tasks)
        } catch (e: Exception) {
            Timber.e("Error al obtener las tareas: ${e.message}")
            Result.failure(e)
        }


    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun observeTasksForDate(selectedDate: LocalDate): Flow<Result<List<Task>>> {
        val userId = firebaseAuth.currentUser?.uid
        if (userId == null) {
            return kotlinx.coroutines.flow.flowOf(Result.failure(Exception("Usuario no autenticado")))
        }

        // --- Calcular el rango de Timestamps para el día seleccionado ---
        // Inicio del día seleccionado (ej: 2025-04-25 00:00:00)
        val startOfDayTimestamp = Timestamp(Date.from(selectedDate.atStartOfDay(ZoneId.systemDefault()).toInstant()))
        // Inicio del día SIGUIENTE (ej: 2025-04-26 00:00:00)
        val startOfNextDayTimestamp = Timestamp(Date.from(selectedDate.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant()))
        // -------------------------------------------------------------

        val tasksCollection = firebaseFirestore.collection("users").document(userId).collection("tasks")

        Timber.d("Observando tareas para $selectedDate (>= $startOfDayTimestamp y < $startOfNextDayTimestamp)")

        return callbackFlow {
            val listenerRegistration = tasksCollection
                // --- Consulta por rango de fecha ---
                .whereGreaterThanOrEqualTo(
                    "dateStart",
                    startOfDayTimestamp
                ) // >= Inicio del día
                .whereLessThan(
                    "dateStart",
                    startOfNextDayTimestamp
                )      // < Inicio del día siguiente
                // .orderBy("timeStart") // Podrías ordenar por hora de inicio también
                .addSnapshotListener { snapshots, error ->
                    if (error != null) {
                        Timber.w(
                            error,
                            "Error escuchando tareas para $selectedDate"
                        )
                        trySend(Result.failure(error))
                        return@addSnapshotListener
                    }
                    if (snapshots != null) {
                        val tasks = snapshots.documents.mapNotNull { doc ->
                            val task = doc.toObject(Task::class.java)
                            task?.copy(taskId = doc.id) // Assuming your Task data class has an `id: String` field
                        }
                        for (task in tasks) {
                            Timber.d("Tarea ${task.taskId}: ${task.title} - ${task.dateStart}")
                        }
                        Timber.d("${tasks.size} tareas encontradas para $selectedDate")
                        trySend(Result.success(tasks))
                    } else {
                        Timber.d("Snapshot null para $selectedDate")
                        trySend(Result.success(emptyList()))
                    }
                }
            awaitClose { listenerRegistration.remove() }
        }
    }

    override suspend fun updateTask(task: Task): Result<Unit> {
        return try {
            Timber.d("Actualizando tarea: $task")
            firebaseFirestore.collection("users").document(task.userId).collection("tasks").document(task.taskId).set(task)
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.e("Error al actualizar la tarea: ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun getTaskById(taskId: String): Result<Task> {
        return try {
            Timber.d("Obteniendo tarea con ID: $taskId")
            val task = firebaseFirestore.collection("users").document(firebaseAuth.currentUser?.uid ?: "").collection("tasks").document(taskId).get().await().toObject(Task::class.java)
            if (task != null) {
                Result.success(task)
            } else {
                Result.failure(Exception("Tarea no encontrada"))
            }
        } catch (e: Exception) {
            Timber.e("Error al obtener la tarea: ${e.message}")
            Result.failure(e)
        }
    }
}