package com.example.taskmanager

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.example.taskmanager.presentation.common.theme.TaskManagerTheme
import com.example.taskmanager.presentation.navigation.AppNavigation
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                // Permission is granted. You can proceed with scheduling notifications.
                // You might trigger a function in your ViewModel here.
                Toast.makeText(this, "Notification permission granted!", Toast.LENGTH_SHORT).show()
            } else {
                // Permission is denied. You should inform the user why notifications won't work.
                Toast.makeText(this, "Notification permission denied. Cannot schedule reminders.", Toast.LENGTH_LONG).show()
                // Optionally, show a dialog explaining the rationale and guiding them to settings.
            }
        }

    // A function to check and request the permission
    fun askNotificationPermission() {
        // This is a crucial check to prevent asking on older Android versions where the permission doesn't exist
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                // Case 1: Permission is already granted
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {
                    // You can proceed with your notification logic here
                    // e.g., schedule notifications for existing tasks.
                    Timber.d("Permission already granted.")

                    Toast.makeText(this, "Notification permission already granted.", Toast.LENGTH_SHORT).show()
                }

                // Case 2: You should show a rationale for why you need the permission
                shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) -> {
                    Timber.d("Should show rationale, then request permission.")

                    // Here, you would show a custom UI (e.g., a Dialog) explaining
                    // why your app needs notification permission.
                    // Example: "We need this permission to show you reminders for your tasks."
                    // After the user clicks "OK" on your dialog, you would call:
                    Toast.makeText(this, "Please allow notification permission for reminders.", Toast.LENGTH_LONG).show()
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }

                // Case 3: Permission has not been requested yet
                else -> {
                    // Directly ask for the permission
                    Timber.d("No rationale needed, launching permission request.")

                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        } else {
            // For older Android versions, no action is needed
            // Notifications are enabled by default
        }
    }

    // You would call askNotificationPermission() from somewhere in your UI logic,
    // for example, when the user enables a notification setting or tries to create a task
    // with a reminder.

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TaskManagerTheme {
                // A surface container using the 'background' color from the theme
                AppNavigation()
            }
        }
    }
}
