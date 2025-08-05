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

            val result = getTaskByIdUseCase(taskId).first()

            result.fold(
                onSuccess = { task ->
                    if (task != null) {
                        // Task found, show the notification
                        notifactionHelper.showTaskStartNotification(
                            taskId = task.taskId,
                            taskTitle = task.title,
                            taskDescription = task.description,
                        )
                        Timber.d("Notification shown for task: ${task.title}")
                        Result.success()
                    } else {
                        // Task not found
                        Timber.w("Task with ID $taskId not found by UseCase.")
                        Result.failure()
                    }
                },
                onFailure = { e ->
                    // Error fetching the task
                    Timber.e(e, "Error fetching task with ID $taskId from UseCase.")
                    Result.failure()
                }
            )
        } catch (e: Exception) {
            Timber.e(e, "Unexpected error in TaskReminderWorker for taskId: $taskId.")
            Result.retry()
        }
    }

}
