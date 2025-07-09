@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.taskmanager.presentation.screens.taskDetail

import android.content.res.Configuration
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.DatePicker
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.core.graphics.toColorInt
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.taskmanager.domain.model.Task
import com.example.taskmanager.presentation.common.theme.TaskManagerTheme
import com.example.taskmanager.presentation.screens.taskDetail.state.TaskDetailState
import com.example.taskmanager.presentation.screens.taskDetail.state.TaskDetailUiEvent
import com.example.taskmanager.util.TimeUtils
import com.google.firebase.Timestamp
import timber.log.Timber

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDetailsScreen(
    modifier: Modifier = Modifier,
    navigateBack: () -> Unit,
    viewModel: TaskDetailsViewModel = hiltViewModel(),
) {

    val task = viewModel.task.collectAsStateWithLifecycle()
    val editingTask = viewModel.taskDetailState.collectAsStateWithLifecycle().value

    TaskDetailsContent(
        modifier = modifier, navigateBack = navigateBack, task = task.value, taskDetailState = editingTask, onTitleChange = {
            viewModel.onUiEvent(TaskDetailUiEvent.TaskTitleChanged(it))
        }, enableEdting = {
            viewModel.onUiEvent(TaskDetailUiEvent.ToggleEditMode)
        }, cancelEditing = {
            viewModel.onUiEvent(TaskDetailUiEvent.CancelEdit)
        }, onPriorityChange = { newPriority ->
            viewModel.onUiEvent(TaskDetailUiEvent.TaskPriorityChanged(newPriority))
        }, onDateChange = { newDate ->
            viewModel.onUiEvent(TaskDetailUiEvent.TaskDueDateChanged(newDate))
        })


}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDetailsContent(
    modifier: Modifier = Modifier,
    navigateBack: () -> Unit,
    task: Task,
    taskDetailState: TaskDetailState,
    onTitleChange: (String) -> Unit,
    onPriorityChange: (String) -> Unit,
    enableEdting: () -> Unit,
    cancelEditing: () -> Unit,
    onDateChange: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

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
            }
            Spacer(modifier = Modifier.weight(1f))
            IconButton(
                onClick = { if (taskDetailState.isEditing) cancelEditing() else enableEdting() },
                modifier = Modifier
                    .padding(8.dp)
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (taskDetailState.isEditing) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
            ) {
                if (taskDetailState.isEditing) {
                    Icon(
                        imageVector = Icons.Filled.Cancel, // Replace with actual icon for save changes
                        contentDescription = "Save Changes", tint = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Icon(
                        imageVector = Icons.Filled.Edit, // Replace with actual icon for more options
                        contentDescription = "Edit Task", tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
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
                TextField(
                    value = if (taskDetailState.isEditing) taskDetailState.taskTitle else task.title,
                    readOnly = !taskDetailState.isEditing,
                    onValueChange = { onTitleChange(it) },
                    placeholder = {
                        Text(
                            text = task.title,
                            color = if (Color(task.taskColor.toColorInt()).luminance() > 0.5) Color.Black.copy(0.5f) else MaterialTheme.colorScheme.onPrimary.copy(
                                0.5f
                            ),
                            style = MaterialTheme.typography.titleLarge
                        )
                    },
                    textStyle = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = Bold,
                        fontSize = 24.sp,
                        color = if (Color(task.taskColor.toColorInt()).luminance() > 0.5) Color.Black else MaterialTheme.colorScheme.onPrimary
                    ),
                    colors = TextFieldDefaults.colors().copy(
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedLabelColor = Color.Transparent,
                        unfocusedLabelColor = Color.Transparent,
                        cursorColor = if (Color(task.taskColor.toColorInt()).luminance() > 0.5) Color.Black else MaterialTheme.colorScheme.onPrimary
                    ),
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
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
                    if (!taskDetailState.isLoading) {
                        Box(
                            modifier = Modifier
                                .padding(16.dp)
                                .weight(1f)
                        ) {
                            Timber.d("TaskDetailContent: ${TimeUtils.convertTimestampToString(task.dateStart!!)}")
                            Timber.d("TaskDetailContent: ${TimeUtils.convertTimestampToString(taskDetailState.taskDueDate)}")

                            DatePickerDocked(
                                onDateChange = onDateChange,
                                date = if (taskDetailState.isEditing) TimeUtils.convertTimestampToString(taskDetailState.taskDueDate)
                                else TimeUtils.convertTimestampToString(task.dateStart!!),
                                taskColor = task.taskColor
                            )
                        }
                    }

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
                    if (taskDetailState.isEditing) {
                        Box(
                            modifier = Modifier
                                .padding(16.dp)
                                .weight(1f),
                        ) {
                            Text(
                                text = taskDetailState.taskPriority,
                                color = if (Color(task.taskColor.toColorInt()).luminance() > 0.5) Color.Black else MaterialTheme.colorScheme.onPrimary,
                                style = MaterialTheme.typography.bodyLarge,
                                fontSize = 18.sp,
                                modifier = Modifier
                                    .clickable(
                                        onClick = { expanded = !expanded })
                                    .background(
                                        when (taskDetailState.taskPriority) {
                                            "High" -> Color.Red.copy(0.5f)
                                            "Medium" -> Color.Yellow.copy(0.5f)
                                            else -> Color.Green.copy(0.5f)
                                        }, shape = RoundedCornerShape(16.dp)
                                    )
                                    .padding(8.dp)
                            )
                            DropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false },
                            ) {
                                DropdownMenuItem(
                                    text = { Text("High") }, onClick = {
                                        onPriorityChange("High")
                                        expanded = false
                                    }, modifier = Modifier.background(Color.Red.copy(0.5f))
                                )
                                DropdownMenuItem(
                                    text = { Text("Medium") }, onClick = {
                                        onPriorityChange("Medium")
                                        expanded = false
                                    },

                                    modifier = Modifier.background(Color.Yellow.copy(0.5f))
                                )
                                DropdownMenuItem(
                                    text = { Text("Low") }, onClick = {
                                        onPriorityChange("Low")
                                        expanded = false
                                    }, modifier = Modifier.background(Color.Green.copy(0.5f))
                                )
                            }
                        }
                    } else {
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


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DatePickerDocked(onDateChange: (String) -> Unit, date: String, taskColor: String) {
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = TimeUtils.convertStringToMillis(date),

    )
    val selectedDate = datePickerState.selectedDateMillis?.let {
        TimeUtils.convertMillisToDate(it)
    } ?: "Select Date"

    Box(
    ) {
        OutlinedTextField(
            value = selectedDate, onValueChange = { onDateChange(it) }, label = {
                Text(
                    text = "",
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (Color(taskColor.toColorInt()).luminance() > 0.5) Color.Black else MaterialTheme.colorScheme.onPrimary
                )
            }, readOnly = true, trailingIcon = {
                IconButton(onClick = { showDatePicker = !showDatePicker }) {
                    Icon(
                        tint = if (Color(taskColor.toColorInt()).luminance() > 0.5) Color.Black else MaterialTheme.colorScheme.onPrimary,
                        imageVector = Icons.Default.DateRange, contentDescription = "Select date"
                    )
                }
            }, modifier = Modifier
                .height(64.dp),
            colors = TextFieldDefaults.colors().copy(
                unfocusedTextColor = if (Color(taskColor.toColorInt()).luminance() > 0.5) Color.Black else MaterialTheme.colorScheme.onPrimary,
                focusedTextColor = if (Color(taskColor.toColorInt()).luminance() > 0.5) Color.Black else MaterialTheme.colorScheme.onPrimary,
                unfocusedContainerColor = Color.Transparent,
                focusedContainerColor = Color.Transparent,
                unfocusedIndicatorColor = if (Color(taskColor.toColorInt()).luminance() > 0.5) Color.Black else MaterialTheme.colorScheme.onPrimary,
                focusedIndicatorColor = if (Color(taskColor.toColorInt()).luminance() > 0.5) Color.Black else MaterialTheme.colorScheme.onPrimary
            )
        )

        if (showDatePicker) {
            Popup(
                onDismissRequest = { showDatePicker = false }, alignment = Alignment.TopStart
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset(y = 64.dp)
                        .shadow(elevation = 4.dp)
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(16.dp)
                ) {
                    DatePicker(
                        state = datePickerState, showModeToggle = false,
                    )
                }
            }
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun TaskDetailContentPreview() {
    TaskManagerTheme {
        TaskDetailsContent(
            navigateBack = {},
            task = Task(
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
            ),
            taskDetailState = TaskDetailState(
                taskId = "1",
                taskTitle = "Nueva tarea de prueba",
                taskDescription = "lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.",
                taskDueDate = Timestamp.now(),
                taskPriority = "Medium",
                isCompleted = true,
                isEditing = true
            ),
            onTitleChange = {}, // Replace with actual event handling if needed
            onPriorityChange = {}, enableEdting = {}, cancelEditing = {}, onDateChange = {}
        )
    }
}
