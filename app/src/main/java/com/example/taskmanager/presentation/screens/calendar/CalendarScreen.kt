package com.example.taskmanager.presentation.screens.calendar

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.taskmanager.domain.model.Task
import com.example.taskmanager.presentation.common.theme.TaskManagerTheme
import com.example.taskmanager.presentation.screens.calendar.state.CalendarUiEvent
import java.time.LocalDate


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
    val totalDaysInMonth = today.lengthOfMonth()

    // From today to end of month
    val remainingDays = (today.dayOfMonth..totalDaysInMonth).map { day ->
        today.withDayOfMonth(day)
    }
    var selectedDate by remember { mutableStateOf(today) }

    Scaffold(
        modifier = modifier,
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
                    text = "March 2023",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = Bold,
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.CenterVertically),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        },

        ) {
        Column(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .pointerInput(Unit) {
                    detectHorizontalDragGestures(
                        onDragEnd = {
                            if (offsetX > swipeThreshold) {
                                onSwipe()
                                println("Swipe right")
                            }
                            offsetX = 0f
                        },
                        onHorizontalDrag = { change, dragAmount ->
                            offsetX += dragAmount
                        })
                }) {
            LazyHorizontalGrid(
                rows = GridCells.Fixed(1),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
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
                            .width(80.dp)
                            .height(50.dp)
                            .background(
                                color = backgroundColor,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .clickable {
                                selectedDate = date
                                viewModel.onUiEvent(
                                    CalendarUiEvent.ChangeDate(date)
                                )
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Column {
                            Text(
                                text = date.dayOfWeek.name.take(3),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = Bold,
                                color = textColor,
                                modifier = Modifier.padding(
                                    start = 8.dp,
                                    end = 8.dp,
                                    top = 8.dp,
                                    bottom = 0.dp
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
                            topStart = 26.dp,
                            topEnd = 26.dp,
                            bottomStart = 0.dp,
                            bottomEnd = 0.dp
                        )
                    )
                    .background(
                        MaterialTheme.colorScheme.primary
                    ),
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Column(modifier = Modifier.padding(top = 16.dp)) {
                        Text(
                            text = "3 Meetings",
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = Bold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.5f),
                        )
                        Text(
                            text = "${selectedDate.dayOfWeek}",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = Bold,
                            fontSize = 40.sp,
                            color = MaterialTheme.colorScheme.onPrimary,
                        )
                    }

                    DailySchedule(events = taskState)

                }
            }
        }
    }

}


@SuppressLint(
    "UnusedBoxWithConstraintsScope",
    "DefaultLocale"
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
                        "%d:00 %s",
                        displayHour,
                        amPm
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
                        Divider(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp),
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                        )
                    }
                }
            }

            // Overlay events
            events.forEach { event ->
                val topOffset = hourHeightDp * (event.timeStart.toInt() - startHour)
                val eventHeight = hourHeightDp * (event.timeEnd.toInt() - event.timeStart.toInt())

                Box(
                    modifier = Modifier
                        .offset(y = topOffset)
                        .padding(
                            start = 100.dp,
                            end = 16.dp
                        )
                        .fillMaxWidth()
                        .height(eventHeight)
                        .clip(
                            if (event.timeStart.toInt() == event.timeEnd.toInt()) {
                                RoundedCornerShape(0.dp)
                            } else {
                                RoundedCornerShape(16.dp)
                            }
                        )
                        //RANDOM BACKGROUND COLOR
                        .background(
                            Color(
                                android.graphics.Color.argb(
                                    255,
                                    (0..255).random(),
                                    (0..255).random(),
                                    (0..255).random()
                                )
                            )
                        )
                        /*.background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.6f))*/
                        .padding(8.dp)
                ) {
                    Text(
                        text = event.title,
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontWeight = Bold
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
fun PreviewComp(modifier: Modifier = Modifier) {
    TaskManagerTheme {
        CalendarScreen(
            onSwipe = { },
            modifier = modifier
        )

    }
}
