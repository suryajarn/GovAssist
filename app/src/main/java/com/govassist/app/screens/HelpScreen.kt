package com.govassist.app.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.govassist.app.components.PlaceholderNotice
import com.govassist.app.components.ScreenHeader
import com.govassist.app.ui.theme.SurfaceWhite

@Composable
fun HelpScreen(onBack: () -> Unit) {
    Scaffold(containerColor = SurfaceWhite) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
        ) {
            ScreenHeader(title = "Help & Accessibility", onBack = onBack)

            Text(
                text = "Help and accessibility settings will appear here.",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 20.dp)
            )

            PlaceholderNotice(
                text = "This screen is a placeholder for future help content and accessibility " +
                    "controls, such as adjustable text size, high contrast mode, and guidance " +
                    "on using GovAssist with TalkBack."
            )
        }
    }
}
