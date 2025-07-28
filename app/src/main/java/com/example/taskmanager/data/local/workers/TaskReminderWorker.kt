package com.example.taskmanager.data.local.workers

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.taskmanager.domain.usecase.task.getTaskByIdUseCase
import com.example.taskmanager.util.NotificationHelper
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import timber.log.Timber


@HiltWorker
class TaskReminderWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val getTaskByIdUseCase: getTaskByIdUseCase,
    private val notifactionHelper: NotificationHelper
) : CoroutineWorker(appContext, workerParams) {
    companion object {
        const val TASK_ID_KEY = "taskId"
    }

    override suspend fun doWork(): Result {
        val taskId = inputData.getString(TASK_ID_KEY) ?: run {
            Timber.e("TaskReminderWorker received no taskId.")
            return Result.failure()
        }
        Timber.d("TaskReminderWorker started for taskId: $taskId")
        // --- NEW: Check for POST_NOTIFICATIONS permission here ---
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    applicationContext, // Use applicationContext from CoroutineWorker
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Timber.w("Notification permission denied. Cannot show task reminder for ID: $taskId")
                // If permission is denied, the work related to fetching the task is still a success,
                // but the notification itself cannot be shown.
                // You might return Result.success() if the core work (e.g., fetching task) is done,
                // or Result.failure() if the notification is absolutely critical and unrecoverable.
                // For a reminder, success is usually fine.
                return Result.success()
            }
        }
        return try {
            // 1. Obtener la tarea UNA SOLA VEZ del UseCase usando .first()
            // Esto suspende hasta que el Flow emita su primer valor y luego lo devuelve.
            val result = getTaskByIdUseCase(taskId).first() // <-- ¡Aquí está el cambio clave!

            result.onSuccess { task ->
                if (task != null) { // Asegúrate de que la tarea no sea nula si tu Flow emite Task?
                    notifactionHelper.showTaskStartNotification(
                        taskId = task.taskId, // Asumo que tu Task tiene un campo 'id'
                        taskTitle = task.title,
                        taskDescription = task.description,
                    )
                    Timber.d("Notification shown for task: ${task.title}")
                    Result.success() // La tarea se encontró y la notificación se mostró
                } else {
                    Timber.w("Task with ID $taskId not found by UseCase for 'now' reminder.")
                    Result.failure() // La tarea no se encontró
                }
            }.onFailure { e ->
                Timber.e(e, "Error fetching task with ID $taskId from UseCase.")
                Result.failure() // Fallo al obtener la tarea
            }
            Result.success() // Si todo salió bien, devuelve éxito
        } catch (e: Exception) {
            // Captura cualquier otra excepción durante el proceso (ej. red, base de datos)
            Timber.e(e, "Unexpected error in TaskReminderWorker for taskId: $taskId.")
            Result.retry() // Reintentar si es un error transitorio
        }
    }

}
