@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.taskmanager.presentation.screens.calendar

import android.content.res.Configuration
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.taskmanager.domain.model.Task
import com.example.taskmanager.presentation.common.components.MonthPicker
import com.example.taskmanager.presentation.common.theme.MontserratFamily
import com.example.taskmanager.presentation.common.theme.TaskManagerTheme
import com.example.taskmanager.presentation.screens.calendar.state.CalendarUiEvent
import com.example.taskmanager.util.TimeBlockInfo
import com.example.taskmanager.util.TimeUtils
import com.google.firebase.Timestamp
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    onSwipe: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CalendarViewModel = hiltViewModel(),
    navigateToTaskDetails: (String) -> Unit = {},
    navigateToNewTask: () -> Unit,
) {
    val collectedTasks = viewModel.tasks.collectAsState().value
    val selectedDate by viewModel.selectedDate.collectAsState()

    CalendarScreenContent(
        modifier = modifier,
        taskList = collectedTasks,
        selectedDate = selectedDate,
        onSwipe = onSwipe,
        onChangeDate = { newDate ->
            viewModel.onUiEvent(CalendarUiEvent.ChangeDate(newDate))
        },
        navigateToNewTask = navigateToNewTask,
        navigateToTaskDetails = navigateToTaskDetails
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CalendarScreenContent(
    modifier: Modifier,
    taskList: List<Task>,
    onSwipe: () -> Unit,
    onChangeDate: (LocalDate) -> Unit = {},
    navigateToNewTask: () -> Unit,
    navigateToTaskDetails: (String) -> Unit,
    selectedDate: LocalDate
) {
    val swipeThreshold = 100f
    var offsetX by remember { mutableFloatStateOf(0f) }
    val today = LocalDate.now()
    var maxDay by remember { mutableIntStateOf(selectedDate.lengthOfMonth()) }
    val safeSelectedDate = selectedDate.withDayOfMonth(minOf(selectedDate.dayOfMonth, maxDay))
    val remainingDays = (1..maxDay).map { day ->
        safeSelectedDate.withDayOfMonth(day)
    }
    var showMonthPicker by remember {
        mutableStateOf(false)
    }
    MonthPicker(
        visible = showMonthPicker,
        currentMonth = selectedDate.month.value,
        currentYear = selectedDate.year,
        confirmButtonCLicked = { month, year ->
            val newDate = selectedDate.withMonth(month).withYear(year)
            onChangeDate(newDate)
            maxDay = newDate.lengthOfMonth()
            showMonthPicker = false
        },
        cancelClicked = {
            showMonthPicker = false
        })
    Scaffold(modifier = modifier, containerColor = MaterialTheme.colorScheme.onPrimary, topBar = {
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
                            onSwipe()
                        },
                    )
            )
            Text(
                text = selectedDate.month.name,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = Bold,
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.CenterVertically)
                    .clickable(
                        onClick = {
                            showMonthPicker = true
                        }),
                color = MaterialTheme.colorScheme.primary
            )
        }
    }, floatingActionButton = {
        Icon(
            imageVector = Icons.Outlined.Add,
            contentDescription = "Add",
            tint = MaterialTheme.colorScheme.onSecondary,
            modifier = Modifier
                .padding(8.dp)
                .size(60.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(MaterialTheme.colorScheme.primary)
                .padding(8.dp)
                .clickable(
                    onClick = {
                        println("Add")
                        navigateToNewTask()
                    },
                )
        )
    }) {
        Column(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.onPrimary)
                .pointerInput(Unit) {
                    detectHorizontalDragGestures(onDragEnd = {
                        if (offsetX > swipeThreshold) {
                            onSwipe()
                        }
                        offsetX = 0f
                    }, onHorizontalDrag = { change, dragAmount ->
                        offsetX += dragAmount
                    })
                }) {
            DateGrid(
                modifier = Modifier.fillMaxWidth(),
                remainingDays = remainingDays,
                selectedDate = selectedDate,
                today = today,
                onChangeDate = { newDate ->
                    onChangeDate(newDate)
                })
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(
                        RoundedCornerShape(
                            topStart = 26.dp, topEnd = 26.dp, bottomStart = 0.dp, bottomEnd = 0.dp
                        )
                    )
                    .background(
                        MaterialTheme.colorScheme.surface
                    ),
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Column(modifier = Modifier.padding(top = 16.dp)) {
                        Text(
                            text = "${taskList.size} Tasks",
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = Bold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                        )
                        Text(
                            text = "${selectedDate.dayOfWeek}",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = Bold,
                            fontSize = 40.sp,
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }
                    Daily(events = taskList, navigateToTaskDetails)

                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DateGrid(
    modifier: Modifier = Modifier,
    remainingDays: List<LocalDate>,
    selectedDate: LocalDate,
    today: LocalDate,
    onChangeDate: (LocalDate) -> Unit
) {
    val gridState =
        rememberLazyGridState(initialFirstVisibleItemIndex = selectedDate.dayOfMonth - 1)
    LazyHorizontalGrid(
        rows = GridCells.Fixed(1),
        state = gridState,
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
    ) {
        items(remainingDays.size) { index ->
            DateItem(
                modifier = Modifier.padding(horizontal = 8.dp),
                index = index,
                remainingDays = remainingDays,
                selectedDate = selectedDate,
                today = today,
                onChangeDate = onChangeDate
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DateItem(
    modifier: Modifier = Modifier,
    index: Int,
    remainingDays: List<LocalDate>,
    selectedDate: LocalDate,
    today: LocalDate,
    onChangeDate: (LocalDate) -> Unit
) {
    val date = remainingDays[index]
    val isToday = date == today
    val isSelected = date == selectedDate
    val backgroundColor = when {
        isSelected -> MaterialTheme.colorScheme.primary
        isToday -> Color.Transparent
        else -> MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f)
    }

    val textColor = when {
        isSelected -> MaterialTheme.colorScheme.onPrimary
        isToday -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.onPrimary
    }
    Box(
        modifier = Modifier
            .padding(8.dp)
            .width(70.dp)
            .height(50.dp)
            .background(
                color = backgroundColor, shape = RoundedCornerShape(16.dp)
            )
            .clickable {
                onChangeDate(date)
            }, contentAlignment = Alignment.Center
    ) {
        Column {
            Text(
                text = date.dayOfWeek.name.take(3),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = Bold,
                color = textColor,
                modifier = Modifier.padding(
                    start = 8.dp, end = 8.dp, top = 8.dp, bottom = 0.dp
                )
            )

            Text(
                text = date.dayOfMonth.toString(),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = Bold,
                fontSize = 45.sp,
                color = textColor,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }
    }
}

@Composable
fun Daily(
    events: List<Task>, navigateToTaskDetails: (String) -> Unit, modifier: Modifier = Modifier
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        val timeBlockInfoList = remember(events) {
            var endHourTitle = 0
            events.mapIndexed { index, task ->
                val info = TimeUtils.calculateTimeBlockInfo(events, index, endHourTitle)
                endHourTitle = info.timeHeader
                info
            }
        }
        LazyColumn(modifier = modifier.fillMaxSize()) {
            itemsIndexed(events) { index, task ->
                val timeBlockInfo = timeBlockInfoList[index]
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Column(modifier = Modifier.fillMaxSize()) {
                        if (timeBlockInfo.showTimeHeader) {
                            Text(
                                text = timeBlockInfo.startTime + " - " + timeBlockInfo.timeHeaderformatted,
                                fontSize = 24.sp,
                                fontFamily = MontserratFamily,
                                fontWeight = FontWeight.Normal,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Divider(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 16.dp)
                                    .height(1.dp),
                                color = MaterialTheme.colorScheme.primary,
                            )
                        }
                        TaskItem(
                            modifier = Modifier.padding(vertical = 8.dp),
                            timeBlockInfo = timeBlockInfo,
                            task = task,
                            navigateToTaskDetails = navigateToTaskDetails
                        )

                    }
                }
            }
        }

    }

}

@Composable
fun TaskItem(
    modifier: Modifier = Modifier, timeBlockInfo: TimeBlockInfo, task: Task, navigateToTaskDetails: (String) -> Unit
) {
    val lineColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)
    Box(
        modifier = Modifier
            .fillMaxWidth(0.95f)
            .clickable(
                onClick = {
                    navigateToTaskDetails(task.taskId)
                })
            .background(
                MaterialTheme.colorScheme.surfaceColorAtElevation(
                    4.dp
                )
            )
            .drawBehind {
                drawDiagonalLines(lineColor)
                drawRect(
                    color = Color(task.taskColor.toColorInt()),
                    topLeft = Offset.Zero,
                    size = Size(
                        4.dp.toPx(), size.height
                    )
                )
            }) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = timeBlockInfo.startTime + " - " + timeBlockInfo.endTime,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                style = MaterialTheme.typography.headlineLarge.copy(fontFamily = MontserratFamily),
                fontSize = 16.sp
            )
            Text(
                text = task.title,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = Bold,
                fontSize = 18.sp,
                modifier = Modifier.padding(top = 8.dp)
            )
            if (task.description.isNotEmpty()) {
                Text(
                    text = task.description,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontSize = 14.sp,
                    maxLines = 5,
                    modifier = Modifier.padding(top = 12.dp)
                )
            }
        }
    }
}



private fun DrawScope.drawDiagonalLines(lineColor: Color) {
    val canvasWidth = size.width
    val canvasHeight = size.height

    /*val lineColor = Color.White.copy(alpha = 0.05f) // Very subtle white lines*/
    val strokeWidth = 1.dp.toPx()
    val lineSpacing = 8.dp.toPx()

    // --- Improved Logic for Diagonal Lines ---
    // The trick is to cover the entire canvas without drawing "outside" its theoretical boundaries
    // by considering the maximum diagonal extent.

    // Calculate the number of lines needed across the longest dimension (width + height)
    // This ensures coverage for all diagonals starting from different points.
    val totalDiagonalLength = canvasWidth + canvasHeight
    val numberOfLines = (totalDiagonalLength / lineSpacing).toInt()  // +2 for buffer

    // Start drawing lines. The 'x' coordinate will span from -canvasHeight to canvasWidth + canvasHeight
    // to ensure lines originating from outside the top-left fully cross the box.
    for (i in 0 until numberOfLines) {
        // Calculate the starting X coordinate for each diagonal line.
        // We start from negative `canvasHeight` to cover the top-left corner properly
        // and extend beyond `canvasWidth` to cover the bottom-right.
        var startX = (i * lineSpacing) - canvasHeight
        var startY = 0f // Adjust startY to ensure the line starts from the top
        var endX = startX + canvasHeight
        var endY = canvasHeight
        if (startX <= 0) {
            startY = -startX
            startX = 0f // Ensure startX is not negative
        }
        if (endX > canvasWidth) {
            endY = canvasHeight - (endX - canvasWidth)
            endX = canvasWidth
        }

        // Draw the line from top-left direction to bottom-right direction
        drawLine(
            color = lineColor,
            start = Offset(x = startX, y = startY),
            end = Offset(x = endX, y = endY),
            strokeWidth = strokeWidth
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewComp(modifier: Modifier = Modifier) {
    TaskManagerTheme {
        Daily(
            events = listOf(
                Task(
                    taskId = "1",
                    title = "Saloo App Wireframe",
                    description = "Description for Task 1",
                    dateStart = Timestamp.now(),
                    timeStart = "08:00",
                    timeEnd = "11:00",
                    completed = false,
                    taskColor = "#FF428FFC"
                ), Task(
                    taskId = "2",
                    title = "Task 2",
                    description = "Description for Task 2",
                    dateStart = Timestamp.now(),
                    timeStart = "10:30",
                    timeEnd = "12:00",
                    completed = true,
                    taskColor = "#FFB54FFC"
                ), Task(
                    taskId = "3",
                    title = "Task 3",
                    description = "Description for Task 3",
                    dateStart = Timestamp.now(),
                    timeStart = "12:00",
                    timeEnd = "14:00",
                    completed = false,
                    taskColor = "#FFB2FFFC"
                ), Task(
                    taskId = "3",
                    title = "Task 3",
                    description = "Description for Task 3",
                    dateStart = Timestamp.now(),
                    timeStart = "13:00",
                    timeEnd = "17:00",
                    completed = false,
                    taskColor = "#FFB2FFFC"
                ), Task(
                    taskId = "4",
                    title = "Task 4",
                    description = "Description for Task 4",
                    dateStart = Timestamp.now(),
                    timeStart = "16:00",
                    timeEnd = "18:00",
                    completed = false,
                    taskColor = "#FFB2FFFC"
                ), Task(
                    taskId = "5",
                    title = "Task 5",
                    description = "Description for Task 5",
                    dateStart = Timestamp.now(),
                    timeStart = "19:00",
                    timeEnd = "21:00",
                    completed = false,
                    taskColor = "#FFB2FFFC"
                )
            ),
            navigateToTaskDetails = { taskId ->
                println("Navigate to task details with id: $taskId")
            },
        )
    }


}
