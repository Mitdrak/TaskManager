package com.example.taskmanager.presentation.screens.taskDetail

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.taskmanager.domain.model.Task
import com.example.taskmanager.presentation.common.theme.TaskManagerTheme
import com.google.firebase.Timestamp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDetailsScreen(
    modifier: Modifier = Modifier,
    navigateBack: () -> Unit,
    viewModel: TaskDetailsViewModel = hiltViewModel(),
) {

    val task = viewModel.task.collectAsStateWithLifecycle()
    TaskDetailsContent(modifier, navigateBack, task.value)

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDetailsContent(modifier: Modifier = Modifier, navigateBack: () -> Unit, task: Task) {
    Scaffold(modifier = modifier, containerColor = Color(task.taskColor.toColorInt()), topBar = {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(
                onClick = { navigateBack() },
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back to Main Menu",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }/*IconButton(
                    onClick = { scope.launch { drawerState.open() } },
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                ) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Menu",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }*/
        }
    }, floatingActionButton = {
        // Floating action button can be added here
    }, bottomBar = {
        // Bottom bar content can be added here
    }) {
        Column(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
                .background(Color(task.taskColor.toColorInt()))
        ) {
            Column(modifier = Modifier.padding(start = 16.dp)) {
                Text(
                    text = task.title,
                    color = if (Color(task.taskColor.toColorInt()).luminance() > 0.5) Color.Black else MaterialTheme.colorScheme.onPrimary,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = Bold,
                    fontSize = 24.sp,
                    modifier = Modifier.padding(16.dp)
                )
                Row {
                    Text(
                        text = "Start Date:",
                        color = if (Color(task.taskColor.toColorInt()).luminance() > 0.5) Color.Black.copy(
                            0.5f
                        ) else MaterialTheme.colorScheme.onPrimary.copy(0.5f),
                        style = MaterialTheme.typography.bodyLarge,
                        fontSize = 18.sp,
                        modifier = Modifier
                            .padding(16.dp)
                            .weight(0.6f)
                    )
                    Text(
                        text = task.dateStart?.toDate().toString().substring(0, 10),
                        color = if (Color(task.taskColor.toColorInt()).luminance() > 0.5) Color.Black else MaterialTheme.colorScheme.onPrimary,
                        style = MaterialTheme.typography.bodyLarge,
                        fontSize = 18.sp,
                        modifier = Modifier
                            .padding(16.dp)
                            .weight(1f)
                    )

                }
                Row {
                    Text(
                        text = "Time:",
                        color = if (Color(task.taskColor.toColorInt()).luminance() > 0.5) Color.Black.copy(
                            0.5f
                        ) else MaterialTheme.colorScheme.onPrimary.copy(0.5f),
                        style = MaterialTheme.typography.bodyLarge,
                        fontSize = 18.sp,
                        modifier = Modifier
                            .padding(16.dp)
                            .weight(0.6f)
                    )
                    Text(
                        text = "${task.timeStart} - ${task.timeEnd}",
                        color = if (Color(task.taskColor.toColorInt()).luminance() > 0.5) Color.Black else MaterialTheme.colorScheme.onPrimary,
                        style = MaterialTheme.typography.bodyLarge,
                        fontSize = 18.sp,
                        modifier = Modifier
                            .padding(16.dp)
                            .weight(1f)
                    )
                }
                Row(
                    modifier = Modifier.padding(0.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "Priority:",
                        color = if (Color(task.taskColor.toColorInt()).luminance() > 0.5) Color.Black.copy(
                            0.5f
                        ) else MaterialTheme.colorScheme.onPrimary.copy(0.5f),
                        style = MaterialTheme.typography.bodyLarge,
                        fontSize = 18.sp,
                        modifier = Modifier
                            .padding(start = 16.dp)
                            .weight(0.6f)
                    )
                    Box(
                        modifier = Modifier
                            .padding(16.dp)
                            .weight(1f)
                    ) {
                        Text(
                            text = task.priority,
                            color = if (Color(task.taskColor.toColorInt()).luminance() > 0.5) Color.Black else MaterialTheme.colorScheme.onPrimary,
                            style = MaterialTheme.typography.bodyLarge,
                            fontSize = 18.sp,
                            modifier = Modifier
                                .background(
                                    if (task.priority == "High") Color.Red.copy(0.5f) else if (task.priority == "Medium") Color.Yellow.copy(
                                        0.5f
                                    ) else Color.Green.copy(0.5f), shape = RoundedCornerShape(16.dp)
                                )
                                .padding(8.dp)
                        )
                    }
                }
                Row {
                    Text(
                        text = "Completed:",
                        color = if (Color(task.taskColor.toColorInt()).luminance() > 0.5) Color.Black.copy(
                            0.5f
                        ) else MaterialTheme.colorScheme.onPrimary.copy(0.5f),
                        style = MaterialTheme.typography.bodyLarge,
                        fontSize = 18.sp,
                        modifier = Modifier
                            .padding(16.dp)
                            .weight(0.6f)
                    )
                    Text(
                        text = if (task.completed) "Yes" else "No",
                        color = if (Color(task.taskColor.toColorInt()).luminance() > 0.5) Color.Black else MaterialTheme.colorScheme.onPrimary,
                        style = MaterialTheme.typography.bodyLarge,
                        fontSize = 18.sp,
                        modifier = Modifier
                            .padding(16.dp)
                            .weight(1f)

                    )
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Description",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    Text(
                        text = task.description,
                        maxLines = 5,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    Divider(
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                        thickness = 1.dp,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                }
            }
        }
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun TaskDetailContentPreview() {
    TaskManagerTheme {
        TaskDetailsContent(
            navigateBack = {}, task = Task(
                taskId = "1",
                userId = "1",
                title = "Nueva tarea de prueba",
                timeStart = "14:00",
                timeEnd = "15:00",
                dateStart = Timestamp.now(),
                description = "lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.",
                completed = true,
                createdAt = 1746640559985,
                priority = "Medium",
                taskColor = "#FFB2FFFC" // Example color
            )
        )
    }
}
