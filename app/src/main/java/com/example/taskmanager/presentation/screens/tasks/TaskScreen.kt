package com.example.taskmanager.presentation.screens.tasks

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.taskmanager.domain.model.Task
import com.example.taskmanager.presentation.common.components.DrawerContent
import com.example.taskmanager.presentation.common.components.DrawerScreen
import com.example.taskmanager.presentation.screens.home.HomeViewModel
import kotlinx.coroutines.launch


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
    navigateToHome: () -> Unit,
    navigateToTaskDetails: (String) -> Unit = {},
    navigateToLogin: () -> Unit
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val tasks: List<Task> by viewModel.tasks.collectAsStateWithLifecycle()
    val tasksCompleted: List<Task> by viewModel.tasksCompleted.collectAsStateWithLifecycle()

    val onDrawerItemSelected: (DrawerScreen) -> Unit = { screen ->
        scope.launch { drawerState.close() }
        when (screen) {
            is DrawerScreen.Logout -> {
            }
            // Handle other drawer items here
            else -> { /* Do nothing for now */
            }
        }
    }
    ModalNavigationDrawer(
        drawerState = drawerState,
        scrimColor = Color.Unspecified,

        drawerContent = {
            DrawerContent(
                onItemSelected = onDrawerItemSelected,
                modifier = Modifier.width(200.dp)
            )
        }) {
        Scaffold(
            modifier = modifier,
            topBar = {
                Row(
                    modifier = Modifier.padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { navigateToHome() },
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back to Main Menu",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    Text(
                        text = "Tasks",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = Bold,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp)
                    )
                }
            },
            floatingActionButton = {
                // Floating action button can be added here
            },
            bottomBar = {
                // Bottom bar content can be added here
            }) { contentPadding ->
            Column(
                modifier = Modifier
                    .padding(contentPadding)
                    .fillMaxSize()
            ) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Tasks",
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier.padding(16.dp)
                    )
                }
                LazyColumn {
                    items(tasks.size) { index ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(80.dp)
                                .padding(16.dp)
                                .clickable(
                                    onClick = {
                                        navigateToTaskDetails(tasks[index].taskId)
                                    }
                                )
                        ) {
                            Checkbox(
                                checked = tasks[index].completed,
                                onCheckedChange = { it2 ->
                                    viewModel.updateTask(tasks[index].copy(completed = true))
                                },
                                modifier = Modifier.padding(end = 16.dp)
                            )
                            Column {
                                Text(
                                    text = tasks[index].title,
                                    fontSize = 20.sp,
                                    style = MaterialTheme.typography.headlineLarge,
                                    fontWeight = Bold,
                                )
                                Text(
                                    text = "Description: ${tasks[index].description}",
                                    fontSize = 16.sp,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                )
                            }
                        }
                    }
                }
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Tasks Completed",
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier.padding(16.dp)
                    )
                }
                LazyColumn {
                    items(tasksCompleted.size) { index ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(80.dp)
                                .padding(16.dp)
                                .clickable(
                                    onClick = {
                                        navigateToTaskDetails(tasks[index].taskId)
                                    }
                                )
                        ) {
                            Checkbox(
                                checked = tasksCompleted[index].completed,
                                onCheckedChange = { it2 ->
                                    viewModel.updateTask(tasksCompleted[index].copy(completed = false))
                                },
                                modifier = Modifier.padding(end = 16.dp)
                            )
                            Column {
                                Text(
                                    text = tasksCompleted[index].title,
                                    fontSize = 20.sp,
                                    style = MaterialTheme.typography.headlineLarge,
                                    fontWeight = Bold,
                                )
                                Text(
                                    text = "Description: ${tasksCompleted[index].description}",
                                    fontSize = 16.sp,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                )
                            }
                        }
                    }
                }

            }
        }
    }

}

