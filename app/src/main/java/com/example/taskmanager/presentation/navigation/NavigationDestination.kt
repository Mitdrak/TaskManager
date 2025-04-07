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
    object HabitDetails : Screen("habit_details_screen/{habitId}") {
        fun createRoute(habitId: String) = "habit_details_screen/$habitId"
    }

    object Settings : Screen("settings_screen")
}