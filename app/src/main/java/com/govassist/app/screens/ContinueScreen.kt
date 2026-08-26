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
fun ContinueScreen(onBack: () -> Unit) {
    Scaffold(containerColor = SurfaceWhite) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
        ) {
            ScreenHeader(title = "Continue Application", onBack = onBack)

            Text(
                text = "Your previous application will appear here.",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 20.dp)
            )

            PlaceholderNotice(
                text = "This screen is a placeholder. In a future version, GovAssist will " +
                    "remember any government form you were partway through completing, and " +
                    "let you pick up exactly where you left off."
            )

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}
