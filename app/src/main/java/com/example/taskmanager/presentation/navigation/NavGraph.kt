package com.example.taskmanager.presentation.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.taskmanager.presentation.screens.home.HomeScreen
import com.example.taskmanager.presentation.screens.login.LoginScreen

fun NavGraphBuilder.authNavGraph(navController: NavHostController) {
    navigation(
        route = Graph.AUTHENTICATION, startDestination = Screen.Login.route
    ) {
        composable(
            route = Screen.Login.route
        ) {
            LoginScreen()
        }
        composable(
            route = Screen.Register.route
        ) {
            // RegisterScreen(navController)
        }
    }
}

fun NavGraphBuilder.mainNavGraph(navController: NavHostController) {
    navigation(
        route = Graph.MAIN, startDestination = Screen.Home.route
    ) {
        composable(
            route = Screen.Home.route
        ) {
            HomeScreen()
        }
        composable(
            route = Screen.HabitDetails.route
        ) {
            // HabitDetailsScreen(navController)
        }
        composable(
            route = Screen.Settings.route
        ) {
            // SettingsScreen(navController)
        }
    }
}