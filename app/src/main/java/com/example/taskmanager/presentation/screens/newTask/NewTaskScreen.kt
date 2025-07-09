package com.example.taskmanager.presentation.screens.newTask

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.taskmanager.presentation.screens.newTask.state.NewTaskUiEvent
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Calendar


@SuppressLint("DefaultLocale")
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewTaskScreen(
    navigateToHome: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: NewTaskViewModel = hiltViewModel()
) {
    val uiState = viewModel.newTaskState.collectAsStateWithLifecycle()


    val context = LocalContext.current
    val calendar = Calendar.getInstance()


    // State to hold selected date and time
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
    var selectedTime by remember { mutableStateOf<LocalTime?>(null) }
    var selectedTimeEnd by remember { mutableStateOf<LocalTime?>(null) }

    // Format date and time
    val formattedDate =
        selectedDate?.format(DateTimeFormatter.ofPattern("dd MMM yyyy")) ?: "Select date"
    val formattedTime =
        selectedTime?.format(DateTimeFormatter.ofPattern("hh:mm a")) ?: "Select time"
    val formattedTimeEnd =
        selectedTimeEnd?.format(DateTimeFormatter.ofPattern("hh:mm a")) ?: "Select time"

    val isFieldsNotEmpty = viewModel.isFieldsNotEmpty.collectAsStateWithLifecycle()

    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            selectedDate = LocalDate.of(
                year,
                month + 1,
                dayOfMonth
            )
            viewModel.setDateAndTime(
                LocalDate.of(
                    year,
                    month + 1,
                    dayOfMonth
                ),
                LocalTime.of(
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE)
                )
            )


        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    val timePickerDialog = TimePickerDialog(
        context,
        { _, hourOfDay, minute ->
            selectedTime = LocalTime.of(
                hourOfDay,
                minute
            )
            if( selectedTimeEnd != null && selectedTimeEnd!! < selectedTime!!) {
                viewModel.onUiEvent(NewTaskUiEvent.ShowSnackbar("Start time cannot be after end time"))
                return@TimePickerDialog
            }
            viewModel.onUiEvent(
                NewTaskUiEvent.TimeStartChanged(
                    String.format(
                        "%02d",
                        hourOfDay
                    ) + ":" + String.format(
                        "%02d",
                        minute
                    )
                )
            )
        },
        calendar.get(Calendar.HOUR_OF_DAY),
        calendar.get(Calendar.MINUTE),
        true // 24-hour format
    )
    val timePickerDialogEnd = TimePickerDialog(
        context,
        { _, hourOfDay, minute ->
            selectedTimeEnd = LocalTime.of(
                hourOfDay,
                minute
            )
            if( selectedTime != null && selectedTimeEnd != null && selectedTimeEnd!! < selectedTime!!) {
                viewModel.onUiEvent(NewTaskUiEvent.ShowSnackbar("End time cannot be before start time"))
                return@TimePickerDialog
            }
            viewModel.onUiEvent(
                NewTaskUiEvent.TimeEndChanged(
                    String.format(
                        "%02d",
                        hourOfDay
                    ) + ":" + String.format(
                        "%02d",
                        minute
                    )
                )
            )
        },
        calendar.get(Calendar.HOUR_OF_DAY),
        calendar.get(Calendar.MINUTE),
        true // 24-hour format
    )
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    BackHandler {
        navigateToHome()
    }
    LaunchedEffect(uiState.value.snackBarMessage) {
        uiState.value.snackBarMessage.let { message ->
            if (message.isEmpty()) return@let
            snackbarHostState.showSnackbar(message)
            viewModel.snackbarMessageShown()
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(
                snackbarHostState,
            )
        },
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = Icons.Outlined.ArrowBack,
                    contentDescription = "Add",
                    tint = MaterialTheme.colorScheme.onSecondary,
                    modifier = Modifier
                        .padding(8.dp)
                        .size(50.dp)
                        .clip(RoundedCornerShape(50.dp))
                        .background(MaterialTheme.colorScheme.primary)
                        .padding(8.dp)
                        .clickable(
                            onClick = {
                                navigateToHome()
                            },
                        )
                )
                Text(
                    text = "New Task",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = Bold,
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.CenterVertically),
                    color = MaterialTheme.colorScheme.primary
                )
            }

        }) {
        Column(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
        ) {
            Text(
                text = "Title",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 8.dp)
            )
            Box(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.primary)
            ) {
                TextField(
                    value = uiState.value.title,
                    onValueChange = { viewModel.onUiEvent(NewTaskUiEvent.TitleChanged(it)) },
                    placeholder = {
                        Text(
                            text = "Add your task here",
                            style = MaterialTheme.typography.bodyLarge,
                            fontSize = 20.sp,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.primary),
                    textStyle = TextStyle(
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontSize = 20.sp,
                    ),
                    colors = TextFieldDefaults.colors().copy(
                        unfocusedTextColor = MaterialTheme.colorScheme.onPrimary,
                        unfocusedContainerColor = MaterialTheme.colorScheme.primary,
                        unfocusedIndicatorColor = Color.Transparent,
                    )
                )
            }
            Text(
                text = "Description",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 8.dp)
            )
            Box(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth()
                    .height(150.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.primary)
            ) {
                TextField(
                    value = uiState.value.description,
                    onValueChange = { viewModel.onUiEvent(NewTaskUiEvent.DescriptionChanged(it)) },
                    placeholder = {
                        Text(
                            text = "Enter task description",
                            style = MaterialTheme.typography.bodyLarge,
                            fontSize = 20.sp,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.primary),
                    textStyle = TextStyle(
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontSize = 20.sp,
                    ),
                    colors = TextFieldDefaults.colors().copy(
                        unfocusedTextColor = MaterialTheme.colorScheme.onPrimary,
                        unfocusedContainerColor = MaterialTheme.colorScheme.primary,
                        unfocusedIndicatorColor = Color.Transparent,
                    )
                )
            }
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Due Date",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Button(
                        onClick = { datePickerDialog.show() },
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(
                            horizontal = 16.dp,
                            vertical = 12.dp
                        )
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = uiState.value.date,
                                fontSize = 20.sp,
                                color = MaterialTheme.colorScheme.onPrimary
                            )

                            Icon(
                                imageVector = Icons.Outlined.DateRange,
                                contentDescription = "Select date",
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                }
                Spacer(modifier = Modifier.size(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Start Time",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Button(
                        onClick = { timePickerDialog.show() },
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(
                            horizontal = 16.dp,
                            vertical = 12.dp
                        )
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = uiState.value.timeStart,
                                fontSize = 20.sp,
                                color = MaterialTheme.colorScheme.onPrimary
                            )

                            Icon(
                                imageVector = Icons.Outlined.AccessTime,
                                contentDescription = "Select date",
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.size(8.dp))
                    Text(
                        text = "End Time",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Button(
                        onClick = { timePickerDialogEnd.show() },
                        enabled = selectedTime != null,
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(
                            horizontal = 16.dp,
                            vertical = 12.dp
                        )
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = uiState.value.timeEnd,
                                fontSize = 20.sp,
                                color = MaterialTheme.colorScheme.onPrimary
                            )

                            Icon(
                                imageVector = Icons.Outlined.AccessTime,
                                contentDescription = "Select date",
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }


                }
            }
            Spacer(modifier = Modifier.size(16.dp))
            Text(
                text = "Priority",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                //Priorities with color
                val priorities = listOf(
                    "High" to Color.Red.copy(0.5f),
                    "Medium" to Color.Yellow.copy(0.5f),
                    "Low" to Color.Green.copy(0.5f)
                )
                priorities.forEach { priority ->
                    val onClick = {
                        viewModel.onUiEvent(NewTaskUiEvent.PriorityChanged(priority.first))
                    }
                    Text(
                        text = priority.first,
                        style = MaterialTheme.typography.titleMedium,
                        color = if (uiState.value.priority == priority.first) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier
                            .padding(8.dp)
                            .clip(RoundedCornerShape(50.dp))
                            .background(
                                if (uiState.value.priority == priority.first) priority.second
                                else Color.LightGray
                            )
                            .clickable(onClick = onClick)
                            .padding(16.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.size(16.dp))
            Text(
                text = "Task Color",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            var color by remember { mutableStateOf(Color.Unspecified) }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val colors = listOf(
                    Color(0xFFE6DAC3),
                    Color(0xFFECB7B7),
                    Color(0xFFC9E4CA),
                    Color(0xFFB2FFFC),
                    Color(0xFFD7BDE2),
                    Color(0xFFF7D2C4),
                    Color(0xFFE4D6F5),
                    Color(0xFFB3E5FC),
                    Color(0xFFC5CAE9),
                    Color(0xFFF48FB1),
                    Color(0xFFE1BEE7),
                    Color(0xFFBCAAA4)
                )
                colors.forEach { colorete ->
                    val onClick = {
                        color = colorete
                        viewModel.onUiEvent(
                            NewTaskUiEvent.TaskColorChanged(
                                // OUTPUT FORMAT 0xFFRRGGBB
                                String.format("#%08X", colorete.toArgb())
                            )
                        )
                    }
                    val modifier = Modifier
                        .size(32.dp)
                        .clip(shape = RoundedCornerShape(50))
                        .background(colorete)
                        .clickable(onClick = onClick)
                        .border(
                            width = if (colorete == color) 2.dp else 0.dp,
                            color = MaterialTheme.colorScheme.onPrimary,
                            shape = RoundedCornerShape(50.dp)
                        )
                    Box(modifier)
                }
            }
            Spacer(modifier = Modifier.weight(1f))
            //Button to save task
            Button(
                onClick = {
                    viewModel.onUiEvent(NewTaskUiEvent.AddTask)
                },
                enabled = isFieldsNotEmpty.value,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFEE4C1),
                    contentColor = Color.Black
                )
            ) {
                Text(
                    text = "Create Task",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.Black
                )
            }
        }
    }
}
