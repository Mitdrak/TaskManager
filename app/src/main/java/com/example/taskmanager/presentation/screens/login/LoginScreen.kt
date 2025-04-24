package com.example.taskmanager.presentation.screens.login

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.taskmanager.R
import com.example.taskmanager.presentation.common.theme.TaskManagerTheme
import com.example.taskmanager.presentation.screens.login.state.LoginUiEvent
import timber.log.Timber

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onRegisterClick: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val loginState by viewModel.state.collectAsState()

    LaunchedEffect(loginState.isLoginSuccessful) {
        if (loginState.isLoginSuccessful) {
            Timber.d("Login successful")
            onLoginSuccess()
        }
    }
    Scaffold(
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 8.dp),
        ) {
            Spacer(modifier = Modifier.height(100.dp))
            Text(
                text = "Log in to your account✨",
                fontSize = 35.sp,
                fontWeight = Bold,
                modifier = Modifier.padding(start = 16.dp)
            )
            Text(
                text = "Welcome back! Please enter your details.",
                fontSize = 25.sp,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.padding(start = 16.dp)
            )
            Text(
                text = "Email",
                fontSize = 18.sp,
                modifier = Modifier.padding(start = 16.dp, top = 16.dp)
            )
            OutlinedTextField(
                value = loginState.emailOrMobile,
                onValueChange = { viewModel.onUiEvent(LoginUiEvent.EmailOrMobileChanged(it)) },
                placeholder = { Text(text = "Enter your email", fontSize = 18.sp) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Email,
                        contentDescription = "Email Icon",
                    )
                },
                modifier = Modifier
                    .padding(start = 16.dp, top = 8.dp, end = 16.dp)
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.inverseOnSurface),
                shape = RoundedCornerShape(15.dp)

            )
            Text(
                text = "Password",
                fontSize = 18.sp,
                modifier = Modifier.padding(start = 16.dp, top = 16.dp)
            )
            OutlinedTextField(
                value = loginState.password,
                onValueChange = { viewModel.onUiEvent(LoginUiEvent.PasswordChanged(it)) },
                placeholder = {
                    Text(
                        text = "Enter your password", fontSize = 18.sp,
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Lock,
                        contentDescription = "Password Icon",
                    )
                },
                visualTransformation = PasswordVisualTransformation(),
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Visibility,
                        contentDescription = "Password Icon",
                    )
                },
                modifier = Modifier
                    .padding(start = 16.dp, top = 8.dp, end = 16.dp)
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.inverseOnSurface),
                shape = RoundedCornerShape(15.dp)

            )
            Row(
                modifier = Modifier
                    .padding(start = 16.dp, top = 16.dp, end = 16.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = false,
                        onCheckedChange = {},
                    )
                    Text(
                        text = "Remember me",
                        fontSize = 18.sp,
                    )
                }
                Text(
                    text = "Forgot Password?",
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.primary,
                            shape = RoundedCornerShape(15.dp)
                        )
                        .padding(8.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            GradientOutlinedButton(
                onClick = {
                    viewModel.onUiEvent(LoginUiEvent.Submit)
                },
                modifier = Modifier.padding(
                    horizontal = 16.dp,
                ),
                gradientColors = listOf(
                    MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.secondary
                ),
                text = "Log in",
                borderColor = MaterialTheme.colorScheme.primary,
            )
            Spacer(modifier = Modifier.height(8.dp))
            GoogleLoginButton(onClick = { /*TODO*/ })
            Spacer(modifier = Modifier.height(8.dp))
            FacebookLoginButton(onClick = { /*TODO*/ })
            Spacer(modifier = Modifier.weight(1f))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = "Don't have an account?",
                    fontSize = 18.sp,
                    fontWeight = Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .padding(bottom = 16.dp)
                        .clickable { /*TODO*/ })
                Text(
                    text = " Sign up",
                    fontSize = 18.sp,
                    fontWeight = Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier
                        .padding(bottom = 16.dp)
                        .clickable { onRegisterClick() })
            }
        }
    }
}

@Composable
fun GradientOutlinedButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    text: String,
    gradientColors: List<Color>,
    borderColor: Color = Color.White,
    textColor: Color = MaterialTheme.colorScheme.onPrimary,
) {
    Box(
        modifier = modifier
            .clickable(onClick = onClick)
            .clip(RoundedCornerShape(12.dp)) // Rounded corners
            .fillMaxWidth()
            .height(50.dp) // Height of the button
            .background(Brush.linearGradient(gradientColors)) // Gradient background
            .border(
                width = 1.dp, color = borderColor, shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 16.dp, vertical = 8.dp), // Padding inside the button
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 18.sp,
            color = textColor,
            style = MaterialTheme.typography.labelLarge
        )
    }
}


@Composable
fun LoginButton(modifier: Modifier = Modifier, onClick: () -> Unit) {

    GradientOutlinedButton(
        onClick = { onClick },
        modifier = Modifier.padding(
            horizontal = 16.dp,
        ),
        gradientColors = listOf(
            MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.secondary
        ),
        text = "Log in",
    )

}

@Composable
fun GoogleLoginButton(modifier: Modifier = Modifier, onClick: () -> Unit) {
    OutlinedButton(
        onClick = {}, shape = RoundedCornerShape(15.dp), modifier = Modifier.padding(
            horizontal = 16.dp
        )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.icons8_google),
                contentDescription = "Google Icon",
                tint = Color.Unspecified,
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Log in with Google",
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun FacebookLoginButton(modifier: Modifier = Modifier, onClick: () -> Unit) {
    OutlinedButton(
        onClick = {},
        shape = RoundedCornerShape(15.dp),
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.facebook_svgrepo_com),
                contentDescription = "Facebook Icon",
                tint = Color.Unspecified,
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Log in with Facebook",
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }

}


@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun LoginScreenPreview() {
    TaskManagerTheme {

    }
}