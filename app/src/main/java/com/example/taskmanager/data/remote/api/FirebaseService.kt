package com.example.taskmanager.data.remote.api

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.taskmanager.data.local.entity.TaskEntity
import com.example.taskmanager.domain.model.Task
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseService @Inject constructor(
    private val firebaseFirestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth
) {
    private val _firebaseTasks = MutableStateFlow<List<TaskEntity>>(emptyList())
    val firebaseTasks: StateFlow<List<TaskEntity>> = _firebaseTasks.asStateFlow()

    fun addTaskToFirebase(task: Task): Result<Unit> {
        return try {
            Timber.d("Adding task: $task")
            val collection =
                firebaseFirestore.collection("users").document(task.userId).collection("tasks")
                    .add(task)
            collection.addOnSuccessListener {
                Timber.d("Task added with ID: ${collection}")
                val taskId = it.id
                it.update(
                    "taskId",
                    taskId
                )
            }
            collection.addOnFailureListener {
                Timber.d("Task addition failed")
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.e("Error adding task: ${e.message}")
            Result.failure(e)
        }
    }

    fun updateTask(task: Task): Result<Unit> {
        return try {
            Timber.d("Updating task to firebase: $task")
            firebaseFirestore.collection("users").document(task.userId).collection("tasks")
                .document(task.taskId).set(task)
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.e("Error updating task: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun getAllTasksOnce(userId: String): Result<List<Task>> {
        return try {

            val snapshot = firebaseFirestore.collection("users").document(userId).collection("tasks").get().await()

            val tasks = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Task::class.java)?.copy(taskId = doc.id)
            }

            Result.success(tasks)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun observeTasksForDate(selectedDate: LocalDate): Flow<Result<List<Task>>> {
        val userId = firebaseAuth.currentUser?.uid
        if (userId == null) {
            return kotlinx.coroutines.flow.flowOf(Result.failure(Exception("User not logged in")))
        }
        val startOfDayTimestamp =
            Timestamp(Date.from(selectedDate.atStartOfDay(ZoneId.systemDefault()).toInstant()))
        val startOfNextDayTimestamp = Timestamp(
            Date.from(
                selectedDate.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant()
            )
        )
        val tasksCollection =
            firebaseFirestore.collection("users").document(userId).collection("tasks")
        Timber.d("Observing tasks for date: $selectedDate, from $startOfDayTimestamp to $startOfNextDayTimestamp")

        return callbackFlow {
            val listenerRegistration = tasksCollection
                .whereGreaterThanOrEqualTo(
                    "dateStart",
                    startOfDayTimestamp
                )
                .whereLessThan(
                    "dateStart",
                    startOfNextDayTimestamp
                )
                .orderBy("timeStart")
                .addSnapshotListener { snapshots, error ->
                    if (error != null) {
                        Timber.w(
                            error,
                            "Error $selectedDate"
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
                            Timber.d("Task ${task.taskId}: ${task.title} - ${task.dateStart}")
                        }
                        Timber.d("${tasks.size} task founded for $selectedDate")
                        trySend(Result.success(tasks))
                    } else {
                        Timber.d("Snapshot null for $selectedDate")
                        trySend(Result.success(emptyList()))
                    }
                }
            awaitClose { listenerRegistration.remove() }
        }
    }

    suspend fun observeAllUserTasks(userId: String): Flow<Result<DocumentChange>> {
        val tasksCollection = firebaseFirestore.collection("users").document(userId).collection("tasks")
        return callbackFlow {
            val listenerRegistration = tasksCollection
                .addSnapshotListener { snapshots, error ->
                    if (error != null) {
                        Timber.w(error, "Error to listening to tasks for user $userId")
                        trySend(Result.failure(error))
                        return@addSnapshotListener
                    }
                    if (snapshots != null) {
                        for (documentChange in snapshots.documentChanges) {
                            val task = documentChange
                            Timber.d("Task change detected: ${task.document.id} - ${task.document.data}")
                            trySend(Result.success(task))
                        }
                    } else {
                        trySend(
                            Result.failure(
                                Exception("Snapshot null for user: $userId")
                            )
                        )
                    }
                }
            awaitClose { listenerRegistration.remove() }
        }
    }

    suspend fun getTaskById(taskId: String): Result<Task> {
        return try {
            Timber.d("Obtaining task with ID: $taskId")
            val task =
                firebaseFirestore.collection("users").document(firebaseAuth.currentUser?.uid ?: "")
                    .collection("tasks").document(taskId).get().await().toObject(Task::class.java)
            if (task != null) {
                Result.success(task)
            } else {
                Result.failure(Exception("Task not found with ID: $taskId"))
            }
        } catch (e: Exception) {
            Timber.e("Error obtaining task: ${e.message}")
            Result.failure(e)
        }
    }
    suspend fun deleteTask(taskId: String): Result<Unit> {
        return try {
            Timber.d("Deleting task with ID: $taskId")
            firebaseFirestore.collection("users").document(firebaseAuth.currentUser?.uid ?: "")
                .collection("tasks").document(taskId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.e("Error deleting task: ${e.message}")
            Result.failure(e)
        }
    }
}
