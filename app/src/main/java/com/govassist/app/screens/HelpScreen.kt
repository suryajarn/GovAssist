package com.govassist.app.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.govassist.app.components.PlaceholderNotice
import com.govassist.app.components.ScreenHeader
import com.govassist.app.settings.LocalAppSettings
import com.govassist.app.ui.theme.AccentOrange
import com.govassist.app.ui.theme.NavyPrimary
import com.govassist.app.ui.theme.SurfaceLightGrey
import com.govassist.app.ui.theme.SurfaceWhite
import com.govassist.app.ui.theme.TextSecondary

@Composable
fun HelpScreen(onBack: () -> Unit) {
    val settings = LocalAppSettings.current

    Scaffold(containerColor = SurfaceWhite) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            ScreenHeader(title = "Help & Accessibility", onBack = onBack)

            Text(
                text = "These display options apply across the whole app.",
                style = MaterialTheme.typography.bodyLarge,
                color = TextSecondary,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            AccessibilityToggleRow(
                title = "High contrast",
                description = "Switch to a pure black and white colour scheme.",
                checked = settings.highContrast,
                onCheckedChange = { settings.highContrast = it }
            )

            Spacer(modifier = Modifier.height(12.dp))

            AccessibilityToggleRow(
                title = "Larger text",
                description = "Increase text size throughout the app by about 20%.",
                checked = settings.largeText,
                onCheckedChange = { settings.largeText = it }
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Using GovAssist with TalkBack",
                style = MaterialTheme.typography.titleLarge,
                color = NavyPrimary,
                modifier = Modifier.padding(bottom = 10.dp)
            )

            Text(
                text = "Every button and icon in GovAssist has a spoken label. Swipe " +
                    "left or right to move between items, and double-tap to activate " +
                    "the selected item. The microphone button on the Home screen is " +
                    "announced as \"Start voice assistant\".",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 20.dp)
            )

            PlaceholderNotice(
                text = "More accessibility settings — such as adjustable speech rate, " +
                    "vibration feedback, and a dedicated screen-reader tutorial — will " +
                    "be added here in a future version."
            )

            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
private fun AccessibilityToggleRow(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceLightGrey),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(checkedTrackColor = AccentOrange),
                modifier = Modifier.semantics {
                    contentDescription = if (checked) "$title, on" else "$title, off"
                }
            )
        }
    }
}
