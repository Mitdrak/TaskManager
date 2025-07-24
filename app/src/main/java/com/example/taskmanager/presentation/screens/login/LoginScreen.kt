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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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

    val loginState by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.snackbarEvent.collect { message ->
            Timber.d("Snackbar message: $message")
            snackbarHostState.showSnackbar(
                message = message,
                withDismissAction = false
            )
            viewModel.onUiEvent(LoginUiEvent.SnackbarDismissed)
        }
    }
    Scaffold(
        snackbarHost = {
            SnackbarHost(snackbarHostState) {
                Snackbar(
                    snackbarData = it,
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer,
                    dismissActionContentColor = MaterialTheme.colorScheme.onErrorContainer,
                )
            }
        }
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
                fontStyle = MaterialTheme.typography.titleLarge.fontStyle,
                fontSize = 34.sp,
                fontWeight = Bold,
                modifier = Modifier.padding(start = 16.dp)
            )
            Text(
                text = "Welcome back! Please enter your details.",
                fontStyle = MaterialTheme.typography.titleMedium.fontStyle,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.padding(start = 16.dp, top = 8.dp)
            )
            Text(
                text = "Email",
                fontSize = 14.sp,
                modifier = Modifier.padding(
                    start = 16.dp,
                    top = 16.dp
                )
            )
            val focusManager = LocalFocusManager.current

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
                value = loginState.emailOrMobile,
                onValueChange = { viewModel.onUiEvent(LoginUiEvent.EmailOrMobileChanged(it)) },
                placeholder = {
                    Text(
                        text = "Enter your email",
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Email,
                        contentDescription = "Email Icon",
                    )
                },
                modifier = Modifier
                    .padding(
                        start = 16.dp,
                        top = 8.dp,
                        end = 16.dp
                    )
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.inverseOnSurface)
                    .focusRequester(focusRequester1),
                shape = RoundedCornerShape(15.dp)

            )
            Text(
                text = "Password",
                fontSize = 14.sp,
                modifier = Modifier.padding(
                    start = 16.dp,
                    top = 16.dp
                )
            )

            val keyBoardController = LocalSoftwareKeyboardController.current
            OutlinedTextField(
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = {
                        /*focusManager.clearFocus()*/
                        keyBoardController?.hide()
                        viewModel.onUiEvent(LoginUiEvent.Submit)
                    }
                ),
                maxLines = 1,
                value = loginState.password,
                onValueChange = { viewModel.onUiEvent(LoginUiEvent.PasswordChanged(it)) },
                placeholder = {
                    Text(
                        text = "Enter your password",
                        fontSize = 18.sp,
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Lock,
                        contentDescription = "Password Icon",
                    )
                },
                visualTransformation = if (loginState.showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    Icon(
                        modifier = Modifier.clickable(
                            onClick = {
                                viewModel.onUiEvent(LoginUiEvent.ShowPassword)
                            }
                        ),
                        imageVector = Icons.Outlined.Visibility,
                        contentDescription = "Password Icon",
                    )
                },
                modifier = Modifier
                    .padding(
                        start = 16.dp,
                        top = 8.dp,
                        end = 16.dp
                    )
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.inverseOnSurface)
                    .focusRequester(focusRequester2),
                shape = RoundedCornerShape(15.dp)

            )
            Row(
                modifier = Modifier
                    .padding(
                        start = 16.dp,
                        top = 16.dp,
                        end = 16.dp
                    )
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
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.primary,
                            shape = RoundedCornerShape(15.dp)
                        )
                        .padding(10.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            GradientOutlinedButton(
                enabled = !loginState.isLoading,
                onClick = {
                    viewModel.onUiEvent(LoginUiEvent.Submit)
                },
                modifier = Modifier.padding(
                    horizontal = 16.dp,
                ),
                gradientColors = listOf(
                    MaterialTheme.colorScheme.primary,
                    MaterialTheme.colorScheme.secondary
                ),
                text = "Log in",
                borderColor = MaterialTheme.colorScheme.primary,
            )
            Spacer(modifier = Modifier.height(8.dp))
            GoogleLoginButton(
                onClick = { /*TODO*/ }, enabled = !loginState.isLoading,
            )
            Spacer(modifier = Modifier.height(8.dp))
            FacebookLoginButton(
                onClick = { /*TODO*/ }, enabled = !loginState.isLoading,
            )
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
fun LoginButton(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onClick: () -> Unit
) {

    GradientOutlinedButton(
        enabled = enabled,
        onClick = { onClick },
        modifier = Modifier.padding(
            horizontal = 16.dp,
        ),
        gradientColors = listOf(
            MaterialTheme.colorScheme.primary,
            MaterialTheme.colorScheme.secondary
        ),
        text = "Log in",
    )

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
                text = "Log in with Google",
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
                text = "Log in with Facebook",
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

    }
}
