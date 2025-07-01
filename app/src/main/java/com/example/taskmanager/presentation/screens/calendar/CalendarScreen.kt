package com.example.taskmanager.presentation.screens.calendar

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.graphics.luminance
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
import com.example.taskmanager.presentation.common.theme.TaskManagerTheme
import com.example.taskmanager.presentation.screens.calendar.state.CalendarUiEvent
import com.google.firebase.Timestamp
import java.time.LocalDate
import java.time.LocalTime


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    onSwipe: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CalendarViewModel = hiltViewModel()
) {
    val taskState = viewModel.tasks.collectAsState().value
    val swipeThreshold = 100f
    var offsetX by remember { mutableStateOf(0f) }

    val today = LocalDate.now()
    var selectedDate by remember { mutableStateOf(today) }
    val totalDaysInMonth by remember { mutableStateOf(today.lengthOfMonth()) }/*val month = today.month
    val year = today.year*/
    val gridState =
        rememberLazyGridState(initialFirstVisibleItemIndex = selectedDate.dayOfMonth - 1)


    // From today to end of month
    // All the days of the month
    val remainingDays = (1..totalDaysInMonth).map { day ->
        selectedDate.withDayOfMonth(day)
    }

    val remainingDaysFromToday = (selectedDate.dayOfMonth..totalDaysInMonth).map { day ->
        today.withDayOfMonth(day)
    }
    var visible by remember {
        mutableStateOf(false)
    }
    MonthPicker(
        visible = visible,
        currentMonth = selectedDate.month.value,
        currentYear = selectedDate.year,
        confirmButtonCLicked = { month_, year_ ->
            selectedDate = selectedDate.withMonth(month_).withYear(year_)
            visible = false
        },
        cancelClicked = {
            visible = false
        })

    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.onPrimary,
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
                                visible = true
                            }),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }, floatingActionButton = {
            Icon(
                imageVector = Icons.Outlined.Add,
                contentDescription = "Add",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .padding(8.dp)
                    .size(50.dp)
                    .clip(RoundedCornerShape(50.dp))
                    .background(MaterialTheme.colorScheme.onSecondary)
                    .padding(8.dp)
                    .clickable(
                        onClick = {
                            println("Add clicked")
                        },
                    )
            )
        }

    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.onPrimary)
                .pointerInput(Unit) {
                    detectHorizontalDragGestures(onDragEnd = {
                        if (offsetX > swipeThreshold) {
                            onSwipe()
                            println("Swipe right")
                        }
                        offsetX = 0f
                    }, onHorizontalDrag = { change, dragAmount ->
                        offsetX += dragAmount
                    })
                }) {
            LazyHorizontalGrid(
                rows = GridCells.Fixed(1),
                state = gridState,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
            ) {
                items(remainingDays.size) { index ->
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
                                selectedDate = date
                                viewModel.onUiEvent(
                                    CalendarUiEvent.ChangeDate(date)
                                )
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
                                fontSize = 50.sp,
                                color = textColor,
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )
                        }
                    }
                }
            }

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
                            text = "${taskState.size} Tasks",
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
//                    DailySchedule(events = taskState)
                    Daily(events = taskState)

                }
            }
        }
    }

}


@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint(
    "UnusedBoxWithConstraintsScope", "DefaultLocale"
)
@Composable
fun DailySchedule(events: List<Task>) {
    val startHour = 1
    val endHour = 24
    val hourHeightDp = 60.dp

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        val totalHours = endHour - startHour
        val totalHeight = hourHeightDp * totalHours

        Box(modifier = Modifier.height(totalHeight)) {
            // Background time labels
            Column {
                repeat(totalHours) { index ->
                    val hour = startHour + index
                    val amPm = if (hour < 12) "AM" else "PM"
                    val displayHour = if (hour == 12 || hour == 0) 12 else hour % 12
                    val label = String.format(
                        "%d:00 %s", displayHour, amPm
                    )

                    Row(
                        verticalAlignment = Alignment.Top,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(hourHeightDp)
                            .padding(start = 16.dp)
                    ) {
                        Text(
                            text = label,
                            modifier = Modifier.width(80.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontWeight = Bold
                        )

                    }
                    Divider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp),
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.5f),
                    )
                }
            }

            // Overlay events

            events.forEach { event ->
                val timeStart = LocalTime.parse(event.timeStart) // e.g., "14:30"
                val timeEnd = LocalTime.parse(event.timeEnd)     // e.g., "16:00"
                val startDecimal = timeStart.hour + timeStart.minute / 60f
                val endDecimal = timeEnd.hour + timeEnd.minute / 60f
                val topOffset = hourHeightDp * (startDecimal - startHour)
                val eventHeight = hourHeightDp * (endDecimal - startDecimal)


                Box(
                    modifier = Modifier
                        .offset(y = topOffset)
                        .padding(
                            start = 100.dp, end = 16.dp
                        )
                        .fillMaxWidth()
                        .height(eventHeight)
                        .clip(
                            if (startDecimal == endDecimal) {
                                RoundedCornerShape(0.dp)
                            } else {
                                RoundedCornerShape(16.dp)
                            }
                        )
                        .border(
                            width = 1.dp, color = MaterialTheme.colorScheme.onPrimary.copy(
                                alpha = 0.5f
                            ), shape = if (startDecimal == endDecimal) {
                                RoundedCornerShape(0.dp)
                            } else {
                                RoundedCornerShape(16.dp)
                            }
                        )
                        //RANDOM BACKGROUND COLOR
                        .background(
                            Color(event.taskColor.toColorInt())
                        )
                        .padding(8.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp)
                    ) {
                        Text(
                            text = event.title,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = Bold,
                            color = if (Color(event.taskColor.toColorInt()).luminance() > 0.5) Color.Black else MaterialTheme.colorScheme.onPrimary
                        )
                        Text(
                            text = event.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (Color(event.taskColor.toColorInt()).luminance() > 0.5) Color.Black else MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun Daily(events: List<Task>, modifier: Modifier = Modifier) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        var endHourTitleInt = 0
        LazyColumn(modifier = modifier.fillMaxSize()) {
            itemsIndexed(events) { index, task ->
                val previusTaskHourEnd = events.getOrNull(index - 1)?.timeStart
                val previusTaskHourEndInt = previusTaskHourEnd?.let {
                    val hourAndMinute = it.split(":")
                    hourAndMinute[0].toInt() * 60 + hourAndMinute[1].toInt()
                }
                val taskHourEndInt =
                    task.timeEnd.split(":")[0].toInt() * 60 + task.timeEnd.split(":")[1].toInt()

                val taskHourStartInt =
                    task.timeStart.split(":")[0].toInt() * 60 + task.timeStart.split(":")[1].toInt()


                var isSameRangeTime = true

                if (previusTaskHourEndInt != null) {
                    if (taskHourEndInt <= endHourTitleInt) {
                        isSameRangeTime = false
                    } else {
                        endHourTitleInt = taskHourStartInt + 300
                        isSameRangeTime = true
                    }
                } else {
                    endHourTitleInt = taskHourStartInt + 300
                    isSameRangeTime = true
                }
                val endHourStringFormatted =
                    "%02d:%02d".format(endHourTitleInt / 60, endHourTitleInt % 60)

                println("task title ${task.title} task title $taskHourEndInt task end $endHourStringFormatted task title $taskHourEndInt")
                println("")
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    /*Text(
                        text = task.dateStart?.toDate().toString().substring(8, 10),
                        fontSize = 24.sp,
                        color = MaterialTheme.colorScheme.primary
                    )*/

                    Column(modifier = Modifier.fillMaxSize()) {
                        if (isSameRangeTime) {
                            Text(
                                text = "${task.timeStart} ${if (task.timeStart <= "12:00") "AM" else "PM"} - $endHourStringFormatted" + " ${if (endHourStringFormatted <= "12:00") "AM" else "PM"}",
                                fontSize = 24.sp,
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
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.9f)
                                .background(
                                    MaterialTheme.colorScheme.surfaceColorAtElevation(
                                        4
                                            .dp
                                    )
                                )
                                .drawBehind { // Usamos drawBehind para dibujar el borde
                                    // Dibujamos una línea rectangular en el lado izquierdo
                                    drawRect(
                                        color = Color(task.taskColor.toColorInt()),
                                        topLeft = Offset.Zero, // Empieza en la esquina superior izquierda
                                        size = Size(
                                            4.dp.toPx(),
                                            size.height
                                        ) // Ancho del borde, altura total del Box
                                    )
                                }
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp)
                            ) {
                                Text(
                                    text = "${task.timeStart} ${if (task.timeStart <= "12:00") "AM" else "PM"} - ${task.timeEnd}" + " ${if (task.timeEnd <= "12:00") "AM" else "PM"}",
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                                    fontSize = 16.sp
                                )
                                Text(
                                    text = task.title,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    modifier = Modifier.padding(top = 8.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

    }

}


@RequiresApi(Build.VERSION_CODES.O)
@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewComp(modifier: Modifier = Modifier) {/*DailySchedule(
        events = listOf(
            Task(
                taskId = "1",
                title = "Task 1",
                description = "Description for Task 1",
                dateStart = com.google.firebase.Timestamp.now(),
                timeStart = "08:00",
                timeEnd = "10:00",
                completed = false,
                taskColor = "#FF428FFC"
            ), Task(
                taskId = "2",
                title = "Task 2",
                description = "Description for Task 2",
                dateStart = com.google.firebase.Timestamp.now(),
                timeStart = "10:30",
                timeEnd = "12:00",
                completed = true,
                taskColor = "#FFB54FFC"
            ), Task(
                taskId = "3",
                title = "Task 3",
                description = "Description for Task 3",
                dateStart = com.google.firebase.Timestamp.now(),
                timeStart = "13:00",
                timeEnd = "15:00",
                completed = false,
                taskColor = "#FFB2FFFC"
            )
        )
    )*/
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
                ),
                Task(
                    taskId = "2",
                    title = "Task 2",
                    description = "Description for Task 2",
                    dateStart = Timestamp.now(),
                    timeStart = "10:30",
                    timeEnd = "12:00",
                    completed = true,
                    taskColor = "#FFB54FFC"
                ),
                Task(
                    taskId = "3",
                    title = "Task 3",
                    description = "Description for Task 3",
                    dateStart = Timestamp.now(),
                    timeStart = "12:00",
                    timeEnd = "14:00",
                    completed = false,
                    taskColor = "#FFB2FFFC"
                ),
                Task(
                    taskId = "3",
                    title = "Task 3",
                    description = "Description for Task 3",
                    dateStart = Timestamp.now(),
                    timeStart = "13:00",
                    timeEnd = "17:00",
                    completed = false,
                    taskColor = "#FFB2FFFC"
                ),
                Task(
                    taskId = "4",
                    title = "Task 4",
                    description = "Description for Task 4",
                    dateStart = Timestamp.now(),
                    timeStart = "16:00",
                    timeEnd = "18:00",
                    completed = false,
                    taskColor = "#FFB2FFFC"
                ),
                Task(
                    taskId = "5",
                    title = "Task 5",
                    description = "Description for Task 5",
                    dateStart = Timestamp.now(),
                    timeStart = "19:00",
                    timeEnd = "21:00",
                    completed = false,
                    taskColor = "#FFB2FFFC"
                )
            )
        )
    }


}
