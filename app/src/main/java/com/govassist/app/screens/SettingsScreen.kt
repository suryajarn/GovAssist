package com.govassist.app.screens

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.govassist.app.components.ScreenHeader
import com.govassist.app.overlay.OverlayService
import com.govassist.app.settings.AppSettings
import com.govassist.app.settings.LocalAppSettings
import com.govassist.app.ui.theme.AccentOrange
import com.govassist.app.ui.theme.NavyPrimary
import com.govassist.app.ui.theme.SurfaceLightGrey
import com.govassist.app.ui.theme.SurfaceWhite
import com.govassist.app.ui.theme.TextSecondary
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(onBack: () -> Unit) {
    val settings = LocalAppSettings.current
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    fun showMessage(message: String) {
        coroutineScope.launch { snackbarHostState.showSnackbar(message) }
    }

    // Launched when the user grants/denies the "draw over other apps" permission.
    val overlayPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M || Settings.canDrawOverlays(context)) {
            settings.floatingAssistantEnabled = true
            context.startForegroundService(Intent(context, OverlayService::class.java))
        } else {
            showMessage("Permission to show over other apps was not granted.")
        }
    }

    // Launched for the Android 13+ notification permission the foreground service needs.
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted || Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            proceedWithOverlayPermission(context, overlayPermissionLauncher, settings)
        } else {
            showMessage("Notification permission is needed to keep the floating assistant running.")
        }
    }

    fun onFloatingAssistantToggled(enabled: Boolean) {
        if (!enabled) {
            settings.floatingAssistantEnabled = false
            context.stopService(Intent(context, OverlayService::class.java))
            return
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notificationPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
        } else {
            proceedWithOverlayPermission(context, overlayPermissionLauncher, settings)
        }
    }

    Scaffold(
        containerColor = SurfaceWhite,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            ScreenHeader(title = "Settings", onBack = onBack)

            SettingsSectionTitle("Assistant")

            SettingsToggleRow(
                title = "Floating assistant bubble",
                description = "Keep a small GovAssist icon in the top-left corner while " +
                    "you use other apps, such as myGov.",
                checked = settings.floatingAssistantEnabled,
                onCheckedChange = { onFloatingAssistantToggled(it) }
            )

            Spacer(modifier = Modifier.height(24.dp))

            SettingsSectionTitle("Account")

            SettingsLinkRow("Manage account") { showMessage("Account management is coming soon.") }
            SettingsLinkRow("Notification preferences") { showMessage("Notification settings are coming soon.") }
            SettingsLinkRow("Language") { showMessage("Additional languages are coming soon.") }

            Spacer(modifier = Modifier.height(24.dp))

            SettingsSectionTitle("Legal")

            SettingsLinkRow("Privacy policy") { showMessage("The privacy policy will be available here.") }
            SettingsLinkRow("Terms of use") { showMessage("Terms of use will be available here.") }

            Spacer(modifier = Modifier.height(24.dp))

            SettingsSectionTitle("Support")

            SettingsLinkRow("Send feedback") { showMessage("Feedback submission is coming soon.") }
            SettingsLinkRow("Sign out") { showMessage("Sign-out is coming soon.") }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "GovAssist prototype — version 0.1",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

/** Opens the system "draw over other apps" settings screen for this app, if not already granted. */
private fun proceedWithOverlayPermission(
    context: android.content.Context,
    launcher: androidx.activity.result.ActivityResultLauncher<Intent>,
    settings: AppSettings
) {
    val canDraw = Build.VERSION.SDK_INT < Build.VERSION_CODES.M || Settings.canDrawOverlays(context)
    if (canDraw) {
        settings.floatingAssistantEnabled = true
        context.startForegroundService(Intent(context, OverlayService::class.java))
    } else {
        val intent = Intent(
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            Uri.parse("package:${context.packageName}")
        )
        launcher.launch(intent)
    }
}

@Composable
private fun SettingsSectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        color = NavyPrimary,
        modifier = Modifier.padding(bottom = 10.dp, top = 4.dp)
    )
}

@Composable
private fun SettingsToggleRow(
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

@Composable
private fun SettingsLinkRow(title: String, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceLightGrey),
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = title, style = MaterialTheme.typography.bodyLarge)
        }
    }
}
