package com.example.taskmanager.presentation.screens.signUp

import android.content.res.Configuration
import androidx.compose.foundation.background
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.taskmanager.R
import com.example.taskmanager.presentation.common.theme.TaskManagerTheme
import com.example.taskmanager.presentation.screens.signUp.state.SignUpUiEvent
import timber.log.Timber

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(
    onRegisterSuccess: () -> Unit = {},
    onLoginClick: () -> Unit = {},
    viewModel: SignUpViewModel = hiltViewModel()
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val signUpState by viewModel.state.collectAsState()

    LaunchedEffect(signUpState.isSignUpSuccessful) {
        if (signUpState.isSignUpSuccessful) {
            onRegisterSuccess()
        }
    }
    LaunchedEffect(Unit) {
        viewModel.snackbarEvent.collect { message ->
            Timber.d("Snackbar message: $message")
            snackbarHostState.showSnackbar(
                message = message,
            )
            viewModel.onUiEvent(SignUpUiEvent.SnackbarDismissed)
        }
    }


    Scaffold(
        snackbarHost = {
            SnackbarHost(snackbarHostState) {
                Snackbar(
                    snackbarData = it,
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer,
                    dismissActionContentColor = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .padding(horizontal = 8.dp),
        ) {
            Spacer(modifier = Modifier.height(100.dp))
            Text(
                text = "Create an account✨",
                fontSize = 35.sp,
                fontWeight = Bold,
                modifier = Modifier.padding(start = 16.dp)
            )
            Text(
                text = "Please enter your details.",
                fontSize = 25.sp,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.padding(start = 16.dp)
            )
            Text(
                text = "Email",
                fontSize = 18.sp,
                modifier = Modifier.padding(start = 16.dp, top = 16.dp)
            )
            val focusRequester1 = remember { FocusRequester() }
            val focusRequester2 = remember { FocusRequester() }
            OutlinedTextField(
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(
                    onNext = {
                        focusRequester2.requestFocus()
                    }
                ),
                maxLines = 1,
                value = signUpState.emailOrMobile,
                onValueChange = { viewModel.onUiEvent(SignUpUiEvent.EmailOrMobileChanged(it)) },
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
                    .background(MaterialTheme.colorScheme.inverseOnSurface)
                    .focusRequester(focusRequester1),
                shape = RoundedCornerShape(15.dp)

            )
            Text(
                text = "Password",
                fontSize = 18.sp,
                modifier = Modifier.padding(start = 16.dp, top = 16.dp)
            )
            val keyBoardController = LocalSoftwareKeyboardController.current

            OutlinedTextField(
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = {
                        keyBoardController?.hide()
                        viewModel.onUiEvent(SignUpUiEvent.SignUp)
                    }
                ),
                maxLines = 1,
                value = signUpState.password,
                onValueChange = { viewModel.onUiEvent(SignUpUiEvent.PasswordChanged(it)) },
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
                visualTransformation = if (signUpState.showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    Icon(
                        modifier = Modifier.clickable(
                            onClick = {
                                viewModel.onUiEvent(SignUpUiEvent.ShowPassword)
                            }
                        ),
                        imageVector = Icons.Outlined.Visibility,
                        contentDescription = "Password Icon",
                    )
                },
                modifier = Modifier
                    .padding(start = 16.dp, top = 8.dp, end = 16.dp)
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.inverseOnSurface)
                    .focusRequester(focusRequester2),
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
                        text = "Must be at least 8 characters",
                        fontSize = 18.sp,
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            GradientOutlinedButton(
                enabled = !signUpState.isLoading,
                onClick = {
                    viewModel.onUiEvent(SignUpUiEvent.SignUp)
                },
                modifier = Modifier.padding(
                    horizontal = 16.dp,
                ),
                gradientColors = listOf(
                    MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.secondary
                ),
                text = "Sign Up",
                borderColor = MaterialTheme.colorScheme.primary,
            )
            Spacer(modifier = Modifier.height(8.dp))
            GoogleLoginButton(enabled = !signUpState.isLoading, onClick = { /*TODO*/ })
            Spacer(modifier = Modifier.height(8.dp))
            FacebookLoginButton(enabled = !signUpState.isLoading, onClick = { /*TODO*/ })
            Spacer(modifier = Modifier.weight(1f))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = "Already have an account?",
                    fontSize = 18.sp,
                    fontWeight = Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .padding(bottom = 16.dp)
                        .clickable { /*TODO*/ })
                Text(
                    text = "Log in",
                    fontSize = 18.sp,
                    fontWeight = Bold,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier
                        .padding(bottom = 16.dp)
                        .clickable { onLoginClick() })
            }
        }
    }
}

@Composable
fun GradientOutlinedButton(
    onClick: () -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
    text: String,
    gradientColors: List<Color>,
    borderColor: Color = Color.White,
    textColor: Color = MaterialTheme.colorScheme.onPrimary,
) {
    val disabledGradientColor = listOf<Color>(
        Color(0xFF6C6C6C),
        Color(0xFF6C6C6C),
    )// Gray color for disabled state

    Box(
        modifier = modifier
            .clickable(onClick = onClick, enabled = enabled)
            .clip(RoundedCornerShape(12.dp)) // Rounded corners
            .fillMaxWidth()
            .height(50.dp) // Height of the button
            .background(if (enabled) Brush.linearGradient(gradientColors) else Brush.linearGradient(disabledGradientColor)) // Gradient background
            .padding(
                horizontal = 16.dp,
                vertical = 8.dp
            ), // Padding inside the button
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
fun GoogleLoginButton(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    OutlinedButton(
        enabled = enabled,
        onClick = {},
        shape = RoundedCornerShape(15.dp),
        modifier = Modifier
            .padding(
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
                modifier = Modifier.size(30.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Sign Up with Google",
                fontSize = 18.sp,
                color = if (enabled) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(0.5f) // Dimmed text color when disabled
            )
        }
    }
}

@Composable
fun FacebookLoginButton(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    OutlinedButton(
        enabled = enabled,
        onClick = {},
        shape = RoundedCornerShape(15.dp),
        modifier = Modifier
            .padding(horizontal = 16.dp)
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
                modifier = Modifier.size(30.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Sign Up with Facebook",
                fontSize = 18.sp,
                color = if (enabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primary.copy(0.5f)
            )
        }
    }

}


@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun LoginScreenPreview() {
    TaskManagerTheme {
        SignUpScreen()
    }
}
