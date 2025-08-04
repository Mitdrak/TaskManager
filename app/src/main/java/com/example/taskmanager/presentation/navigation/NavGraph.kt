package com.example.taskmanager.presentation.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.example.taskmanager.presentation.screens.calendar.CalendarScreen
import com.example.taskmanager.presentation.screens.home.HomeScreen
import com.example.taskmanager.presentation.screens.login.LoginScreen
import com.example.taskmanager.presentation.screens.newTask.NewTaskScreen
import com.example.taskmanager.presentation.screens.signUp.SignUpScreen
import com.example.taskmanager.presentation.screens.taskDetail.TaskDetailsScreen
import com.example.taskmanager.presentation.screens.taskDetail.TaskDetailsViewModel
import com.example.taskmanager.presentation.screens.tasks.TaskScreen

fun NavGraphBuilder.authNavGraph(
    navController: NavHostController,
    onNavigateToMainGraph: () -> Unit
) {
    navigation(
        route = Graph.AUTHENTICATION,
        startDestination = Screen.Login.route,
    ) {
        composable(
            route = Screen.Login.route
        ) {
            LoginScreen(
                onLoginSuccess = onNavigateToMainGraph,
                onRegisterClick = {
                    navController.navigate(Screen.Register.route)
                })
        }
        composable(
            route = Screen.Register.route
        ) {
            SignUpScreen(
                onRegisterSuccess = onNavigateToMainGraph,
                onLoginClick = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Graph.AUTHENTICATION) {
                            inclusive = true
                        }
                    }

                })
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun NavGraphBuilder.mainNavGraph(
    navController: NavHostController,
    onNavigateToAuthGraph: () -> Unit
) {
    navigation(
        route = Graph.MAIN,
        startDestination = Screen.Home.route
    ) {
        composable(
            route = Screen.Home.route
        ) {
            HomeScreen(
                onSwipe = {
                    navController.navigate(Screen.Calendar.route)
                },
                navigateToNewTask = {
                    navController.navigate(Screen.NewTask.route)
                },
                navigateToLogin = {
                    onNavigateToAuthGraph()
                },
                navigateToTasks = {
                    navController.navigate(Screen.Tasks.route)
                },
                navigateToTaskDetails = { taskId ->
                    navController.navigate(Screen.TaskDetails.createRoute(taskId))
                }
            )
        }
        composable(
            route = Screen.Calendar.route
        ) {
            CalendarScreen(
                onSwipe = {
                    navController.navigateUp()
                },
                navigateToTaskDetails = { taskId ->
                    navController.navigate(route = Screen.TaskDetails.createRoute(taskId))
                },
                navigateToNewTask = {
                    navController.navigate(route = Screen.NewTask.route)
                })
        }
        composable(
            route = Screen.NewTask.route
        ) {
            NewTaskScreen(
                navigateToHome = {
                    navController.navigateUp()
                }
            )
        }
        composable(
            route = Screen.Tasks.route
        ) {
            TaskScreen(
                navigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Graph.MAIN) {
                            inclusive = true
                        }
                    }
                },
                navigateToLogin = {
                    onNavigateToAuthGraph()
                },
                navigateToTaskDetails = { taskId ->
                    navController.navigate(Screen.TaskDetails.createRoute(taskId))
                }
            )
        }
        composable(
            route = Screen.TaskDetails.route,
            arguments = listOf(navArgument("taskId") {
                type = NavType.StringType
            })
        ) {
            val viewModel: TaskDetailsViewModel = hiltViewModel()
            TaskDetailsScreen(
                navigateBack = {
                    navController.navigateUp()
                },
                viewModel = viewModel
            )

        }
    }


}
