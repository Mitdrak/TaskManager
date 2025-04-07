package com.example.taskmanager.presentation.screens.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LoginScreen() {
    Column(modifier = Modifier.fillMaxSize(), Arrangement.Center) {
        Text(
            text = "Login Screen",
            fontSize = 24.sp,
            fontWeight = Bold,
            modifier = Modifier.padding(16.dp)
        )
    }
}