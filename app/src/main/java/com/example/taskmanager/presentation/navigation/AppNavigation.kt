package com.example.taskmanager.presentation.navigation

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
            startDestination = startDestination, // Establece el grafo correcto al inicio
            route = Graph.ROOT,                  // Ruta para el NavHost en sí (opcional)
        ) {
            // 3. Define el Grafo de Autenticación Anidado
            authNavGraph( // Esta es tu función que define las pantallas Login/SignUp, etc.
                navController = navController,
                // 4. Pasa la acción para navegar a MAIN cuando el login/registro sea exitoso
                onNavigateToMainGraph = {
                    navController.navigate(Graph.MAIN) {
                        // Limpia el backstack de autenticación para que el usuario no pueda volver atrás
                        popUpTo(Graph.AUTHENTICATION) { inclusive = true }
                        // Evita múltiples copias del grafo principal si ya estaba en el backstack
                        launchSingleTop = true
                    }
                }
                // Podrías pasar otras acciones si las necesitas, como onNavigateToPasswordReset
            )

            // 3. Define el Grafo Principal de la App Anidado
            mainNavGraph( // Esta es tu función que define Home, Settings, Details, etc.
                navController = navController,
                // 4. Pasa la acción para navegar a AUTH cuando se haga logout
                onNavigateToAuthGraph = {
                    navController.navigate(Graph.AUTHENTICATION) {
                        // Limpia el backstack del grafo principal
                        popUpTo(Graph.MAIN) { inclusive = true }
                        // Evita múltiples copias del grafo de autenticación
                        launchSingleTop = true
                    }
                }
                // Podrías pasar otras acciones de navegación desde el grafo principal
            )
        }
    }

}