package com.example.taskmanager.presentation.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val authState = authViewModel.authState.collectAsState().value

    val startDestination = when (authState) {
        is GlobalAuthState.AUTHENTICATED -> Graph.MAIN
        GlobalAuthState.UNAUTHENTICATED -> Graph.AUTHENTICATION
        GlobalAuthState.UNKNOWN -> "loading_screen"
    }


    if (startDestination == "loading_screen") {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = Color.Red
            )
        }
    } else {
        NavHost(
            navController = navController,
            startDestination = startDestination,
            route = Graph.ROOT,
        ) {
            authNavGraph(
                navController = navController,
                onNavigateToMainGraph = {
                    navController.navigate(Graph.MAIN) {
                        popUpTo(Graph.AUTHENTICATION) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
            mainNavGraph(
                navController = navController,
                onNavigateToAuthGraph = {
                    navController.navigate(Graph.AUTHENTICATION) {
                        popUpTo(Graph.MAIN) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }
    }

}
