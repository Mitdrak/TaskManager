package com.example.taskmanager.presentation.navigation

object Graph {
    const val ROOT = "root_graph"
    const val AUTHENTICATION = "auth_graph"
    const val MAIN = "main_graph"
}

sealed class Screen(val route: String) {
    object Login : Screen("login_screen")
    object Register : Screen("register_screen")
    object Home : Screen("home_screen")
    object Calendar : Screen("calendar_screen")
    object NewTask : Screen("new_task_screen")
    object Tasks : Screen("tasks_screen")
    object TaskDetails : Screen("task_details_screen/{taskId}") {
        fun createRoute(taskId: String) = "task_details_screen/$taskId"
    }

    object Settings : Screen("settings_screen")
}