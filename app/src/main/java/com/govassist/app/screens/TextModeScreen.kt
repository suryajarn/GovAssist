package com.govassist.app.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
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
fun TextModeScreen(onBack: () -> Unit) {
    Scaffold(containerColor = SurfaceWhite) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
        ) {
            ScreenHeader(title = "Text Assistant", onBack = onBack)

            Text(
                text = "Text-based assistance will be available here.",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 20.dp)
            )

            PlaceholderNotice(
                text = "This screen is a placeholder. In a future version, you will be able to " +
                    "type questions and requests here instead of speaking, and get the same " +
                    "help from the GovAssist assistant."
            )

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}
