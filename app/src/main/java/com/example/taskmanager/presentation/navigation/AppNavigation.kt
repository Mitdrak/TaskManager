package com.example.taskmanager.presentation.navigation

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val authState = authViewModel.authState.collectAsState().value

    NavHost(
        navController = navController,
        route = Graph.ROOT,
        startDestination = when (authState) {
            AuthState.AUTHENTICATED -> Graph.MAIN
            AuthState.UNAUTHENTICATED -> Graph.AUTHENTICATION
            AuthState.UNKNOWN -> "loading_screen"
        }
    ) {
        composable("loading_screen") {
            CircularProgressIndicator()
        }
        authNavGraph(navController = navController)
        mainNavGraph(navController = navController)
    }
}