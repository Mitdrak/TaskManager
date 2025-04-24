package com.example.taskmanager.presentation.screens.home

import android.content.res.Configuration
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.taskmanager.presentation.common.theme.TaskManagerTheme
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(onSwipe: () -> Unit, viewModel: HomeViewModel = hiltViewModel()) {
    val snackbarHostState = remember { SnackbarHostState() }
    val swipeThreshold = 300f
    var offsetX by remember { mutableFloatStateOf(0f) }


    Scaffold(
        floatingActionButton = {
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
                            viewModel.addNewTask(
                                "NUEVA",
                                "TAREAAAA"
                            )
                        },
                    )
            )
        },
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .background(
                    color = MaterialTheme.colorScheme.background
                )
                .pointerInput(Unit) {
                    detectHorizontalDragGestures(
                        onDragEnd = {
                            if (offsetX < -swipeThreshold) {
                                onSwipe()
                                println("Swipe left")
                            }
                            offsetX = 0f
                        },
                        onHorizontalDrag = { change, dragAmount ->
                            offsetX += dragAmount
                        })
                }) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .background(Color.LightGray.copy(alpha = 0.3f))
            ) {
                val progress = offsetX.coerceIn(
                    -300f,
                    0f
                ) / -300f // normalize from 0 to 1
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(progress)
                        .background(Color.Blue) // or MaterialTheme.colorScheme.primary
                )
            }

            Row(modifier = Modifier.padding(16.dp)) {
                Column(
                ) {
                    Text(
                        text = "Good",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = Bold,
                        modifier = Modifier.background(color = MaterialTheme.colorScheme.onPrimary)
                    )
                    Text(
                        text = "Morning",
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    imageVector = Icons.Outlined.DateRange,
                    contentDescription = "Add",
                    tint = MaterialTheme.colorScheme.onSecondary,
                    modifier = Modifier
                        .padding(8.dp)
                        .size(40.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(MaterialTheme.colorScheme.primary)
                        .padding(8.dp)
                        .clickable(
                            onClick = {
                                println("Date")
                                viewModel.getTasks()
                            },
                        )
                )
                Icon(
                    imageVector = Icons.Outlined.Notifications,
                    contentDescription = "Add",
                    tint = MaterialTheme.colorScheme.onSecondary,
                    modifier = Modifier
                        .padding(8.dp)
                        .size(40.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(MaterialTheme.colorScheme.primary)
                        .padding(8.dp)
                        .clickable(
                            onClick = { println("Date") },
                        )
                )
            }
            Box(
                modifier = Modifier
                    .padding(16.dp)
                    .clip(shape = MaterialTheme.shapes.extraLarge)
                    .background(
                        color = MaterialTheme.colorScheme.primary
                    )
                    .fillMaxWidth()
                    .height(350.dp)
            ) {
                Column {
                    val formatter = DateTimeFormatter.ofPattern(
                        "dd MMMM, E",
                        Locale.ENGLISH
                    )
                    val formattedDate = LocalDate.now().format(formatter)
                    Text(
                        text = formattedDate,
                        style = MaterialTheme.typography.headlineLarge,
                        fontSize = 20.sp,
                        fontWeight = Bold,
                        modifier = Modifier.padding(
                            top = 16.dp,
                            start = 16.dp
                        )
                    )
                    Text(
                        text = "Today's progress",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = Bold,
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = "10/12 Tasks",
                        style = MaterialTheme.typography.headlineLarge,
                        fontSize = 20.sp,
                        fontWeight = Bold,
                        modifier = Modifier.padding(
                            top = 16.dp,
                            start = 16.dp
                        )
                    )
                    Text(
                        text = "75%",
                        fontSize = 100.sp,
                        fontWeight = Bold,
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.padding(start = 16.dp)
                    )/*LinearProgressIndicator(
                        progress = 0.75f,
                        modifier = Modifier
                            .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                            .fillMaxWidth()
                            .height(30.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        trackColor = MaterialTheme.colorScheme.primaryContainer
                    )*/
                    GradientLinearProgressBar(
                        progress = 0.75f,
                        modifier = Modifier
                            .padding(
                                start = 16.dp,
                                end = 16.dp,
                                bottom = 16.dp
                            )
                            .fillMaxWidth()
                            .height(50.dp),
                        gradientColors = listOf(
                            MaterialTheme.colorScheme.primary,
                            Color(
                                red = 47,
                                green = 57,
                                blue = 255
                            ),
                        )
                    )
                }

            }
            Text(
                text = "Ongoing Tasks",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = Bold,
                modifier = Modifier.padding(16.dp)
            )
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .padding(
                        start = 16.dp,
                        bottom = 16.dp
                    )
            ) {
                items(10) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp)
                            .padding(16.dp)
                    ) {
                        Checkbox(
                            checked = false,
                            onCheckedChange = { /* TODO: Handle checkbox change */ },
                            modifier = Modifier.padding(end = 16.dp)
                        )
                        Column {
                            Text(
                                text = "Task $it",
                                fontSize = 20.sp,
                                style = MaterialTheme.typography.headlineLarge,
                                fontWeight = Bold,
                            )
                            Text(
                                text = "Description of task $it",
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                            )
                        }
                    }
                }
            }
            Text(
                text = "Completed Tasks",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = Bold,
                modifier = Modifier.padding(16.dp)
            )
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .padding(
                        start = 16.dp,
                        bottom = 16.dp
                    )
            ) {
                items(10) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Checkbox(
                            checked = true,
                            onCheckedChange = { /* TODO: Handle checkbox change */ },
                            modifier = Modifier.padding(end = 16.dp)
                        )
                        Column {
                            Text(
                                text = "Task $it",
                                fontSize = 20.sp,
                                style = MaterialTheme.typography.headlineLarge,
                                fontWeight = Bold,
                            )
                            Text(
                                text = "Description of task $it",
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

@Composable
fun GradientLinearProgressBar(
    progress: Float, // 0f to 1f
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .height(8.dp)
        .clip(RoundedCornerShape(50)), gradientColors: List<Color>
) {
    Canvas(modifier = modifier.background(Color.LightGray.copy(alpha = 0.3f))) {
        val width = size.width * progress
        drawRect(
            brush = Brush.horizontalGradient(colors = gradientColors),
            size = Size(
                width,
                size.height
            )
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun HomeScreenPreview() {
    TaskManagerTheme {
        HomeScreen(onSwipe = {})
    }
}