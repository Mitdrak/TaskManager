package com.example.taskmanager.data.repository

import com.example.taskmanager.domain.model.Task
import com.example.taskmanager.domain.repository.TaskRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class TaskRepositoryImpl @Inject constructor(
    private val firebaseFirestore: FirebaseFirestore,
) : TaskRepository {
    override suspend fun addTask(task: Task): Result<Unit> {
        return try {
            Timber.d("Agregando tarea: $task")
            val collection =
                firebaseFirestore.collection("users").document(task.userId).collection("tasks")
                    .add(task)
            collection.addOnSuccessListener {
                Timber.d("Tarea agregada con ID: ${collection}")
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
}