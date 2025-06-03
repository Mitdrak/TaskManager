package com.example.taskmanager.presentation.common.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

sealed class DrawerScreen(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object Profile : DrawerScreen(
        "profile",
        "Profile",
        Icons.Default.Person
    )

    object Settings : DrawerScreen(
        "settings",
        "Settings",
        Icons.Default.Settings
    )

    object Logout : DrawerScreen(
        "logout",
        "Logout",
        Icons.Default.ExitToApp
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrawerContent(
    onItemSelected: (DrawerScreen) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp)
    ) {
        // Header
        Text(
            text = "Menu",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        Divider()

        // Menu Items
        DrawerItem(
            screen = DrawerScreen.Profile,
            onItemClick = onItemSelected
        )

        DrawerItem(
            screen = DrawerScreen.Settings,
            onItemClick = onItemSelected
        )

        Spacer(modifier = Modifier.weight(1f))

        // Logout Item
        Divider()
        DrawerItem(
            screen = DrawerScreen.Logout,
            onItemClick = onItemSelected,
            textColor = MaterialTheme.colorScheme.error,
            iconTint = MaterialTheme.colorScheme.error
        )
    }
}

@Composable
private fun DrawerItem(
    screen: DrawerScreen,
    onItemClick: (DrawerScreen) -> Unit,
    textColor: Color = MaterialTheme.colorScheme.onSurface,
    iconTint: Color = MaterialTheme.colorScheme.onSurface
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onItemClick(screen) }
            .padding(
                vertical = 12.dp,
                horizontal = 8.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = screen.icon,
            contentDescription = screen.title,
            tint = iconTint,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = screen.title,
            color = textColor,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}
