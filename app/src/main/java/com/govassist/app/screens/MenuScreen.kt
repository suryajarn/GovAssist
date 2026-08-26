package com.govassist.app.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Accessibility
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.govassist.app.components.AccessibleMenuItem
import com.govassist.app.components.ScreenHeader
import com.govassist.app.ui.theme.SurfaceWhite

private data class MenuEntry(
    val label: String,
    val icon: ImageVector,
    val onClick: () -> Unit
)

@Composable
fun MenuScreen(
    onBack: () -> Unit,
    onHomeClick: () -> Unit,
    onServicesClick: () -> Unit,
    onApplicationsClick: () -> Unit,
    onHelpClick: () -> Unit,
    onAboutClick: () -> Unit
) {
    val menuEntries = listOf(
        MenuEntry("Home", Icons.Filled.Home, onHomeClick),
        MenuEntry("Government Services", Icons.Filled.AccountBalance, onServicesClick),
        MenuEntry("My Applications", Icons.Filled.Assignment, onApplicationsClick),
        MenuEntry("Help & Accessibility", Icons.Filled.Accessibility, onHelpClick),
        MenuEntry("About GovAssist", Icons.Filled.Info, onAboutClick)
    )

    Scaffold(containerColor = SurfaceWhite) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            item {
                ScreenHeader(title = "Menu", onBack = onBack)
            }
            items(menuEntries) { entry ->
                AccessibleMenuItem(
                    text = entry.label,
                    icon = entry.icon,
                    onClick = entry.onClick
                )
            }
        }
    }
}
