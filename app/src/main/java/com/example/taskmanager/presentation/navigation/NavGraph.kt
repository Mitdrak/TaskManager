package com.example.taskmanager.presentation.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.taskmanager.presentation.screens.calendar.CalendarScreen
import com.example.taskmanager.presentation.screens.home.HomeScreen
import com.example.taskmanager.presentation.screens.login.LoginScreen
import com.example.taskmanager.presentation.screens.signUp.SignUpScreen

fun NavGraphBuilder.authNavGraph(
    navController: NavHostController, onNavigateToMainGraph: () -> Unit
) {
    navigation(
        route = Graph.AUTHENTICATION, startDestination = Screen.Login.route,
    ) {
        composable(
            route = Screen.Login.route
        ) {
            LoginScreen(onLoginSuccess = onNavigateToMainGraph, onRegisterClick = {
                navController.navigate(Screen.Register.route)
            })
        }
        composable(
            route = Screen.Register.route
        ) {
            SignUpScreen(onRegisterSuccess = onNavigateToMainGraph, onLoginClick = {
                navController.navigate(Screen.Login.route) {
                    popUpTo(Graph.AUTHENTICATION) {
                        inclusive = true
                    }
                }

            })
        }
    }
}

fun NavGraphBuilder.mainNavGraph(
    navController: NavHostController, onNavigateToAuthGraph: () -> Unit
) {
    navigation(
        route = Graph.MAIN, startDestination = Screen.Home.route
    ) {
        composable(
            route = Screen.Home.route
        ) {
            HomeScreen(onSwipe = {
                navController.navigate(Screen.Calendar.route) {
                    popUpTo(Graph.MAIN) {
                        inclusive = true
                    }
                }
            })
        }
        composable(
            route = Screen.Calendar.route
        ) {
            CalendarScreen(onSwipe = {
                navController.navigate(Screen.Home.route) {
                    popUpTo(Graph.MAIN) {
                        inclusive = true
                    }
                }
            })
        }
    }


}
