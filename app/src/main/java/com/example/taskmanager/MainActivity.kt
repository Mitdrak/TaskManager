package com.example.taskmanager

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.taskmanager.presentation.common.theme.TaskManagerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TaskManagerTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    TaskAccept()
                }
            }
        }
    }
}

@Composable
fun TaskAccept() {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ImageCheck()
        Title(stringResource(R.string.title), Modifier.padding(top = 24.dp, bottom = 8.dp))
        /*Title(stringResource(R.string.subtitle))*/
        Subtitle(stringResource(R.string.subtitle)+" PERRO", Modifier.padding(top = 8.dp))
    }
}


@Composable
fun Title(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "$name",
        fontWeight = Bold,
        modifier = modifier
    )
}

@Composable
fun Subtitle(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "$name",
        modifier = modifier,
        fontSize = 16.sp
    )
}


@Composable
fun ImageCheck() {
    val image = painterResource(R.drawable.ic_task_completed)
    Image(painter = image, contentDescription = null)
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    TaskManagerTheme {
        TaskAccept()
    }
}