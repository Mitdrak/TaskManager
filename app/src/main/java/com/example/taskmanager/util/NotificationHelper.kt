package com.example.taskmanager.util

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.taskmanager.MainActivity
import com.example.taskmanager.R

class NotificationHelper(
    private val context: Context,
    private val notificationManagerCompat: NotificationManagerCompat,
    private val notificationManager: NotificationManager
){
    companion object {
        const val CHANNEL_ID = "task_reminders_channel"
        const val CHANNEL_NAME = "Task Reminders"
        const val CHANNEL_DESCRIPTION_TASK_REMINDER = "Notifications for upcoming task deadlines and start times."

        const val NOTIFICATION_ID_TASK_REMINDER_BASE = 2000

    }
    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.O){
            val taskReminderChannel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = CHANNEL_DESCRIPTION_TASK_REMINDER
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 500, 1000, 500, 1000)
            }
            notificationManager.createNotificationChannel(taskReminderChannel)

        }
    }
    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    fun showTaskStartNotification(taskId: String, taskTitle: String, taskDescription: String) {
        val intent = Intent(context, MainActivity::class.java).apply {
            putExtra("task_id", taskId)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            context,
            taskId.hashCode(), // Request code
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher) // ¡Tu icono pequeño!
            .setContentTitle(taskTitle)
            .setContentText(taskDescription)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setStyle(NotificationCompat.BigTextStyle().bigText(taskDescription))

        val notificationId = NOTIFICATION_ID_TASK_REMINDER_BASE + taskId.hashCode()
        notificationManagerCompat.notify(notificationId, builder.build())
    }

    /**
     * Cancela una notificación específica.
     * @param notificationId El ID de la notificación a cancelar.
     */
    fun cancelNotification(notificationId: Int) {
        notificationManagerCompat.cancel(notificationId)
    }
}
