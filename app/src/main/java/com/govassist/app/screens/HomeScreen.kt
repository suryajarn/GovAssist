package com.govassist.app.screens

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.govassist.app.components.GovAssistButton
import com.govassist.app.components.GovAssistOutlinedButton
import com.govassist.app.ui.theme.AccentTeal
import com.govassist.app.ui.theme.NavyPrimary
import com.govassist.app.ui.theme.SurfaceWhite
import com.govassist.app.ui.theme.TextSecondary
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    onMenuClick: () -> Unit,
    onAccountClick: () -> Unit,
    onContinueClick: () -> Unit,
    onTextModeClick: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = SurfaceWhite
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            HomeHeader(onMenuClick = onMenuClick, onAccountClick = onAccountClick)

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                MicrophoneButton(
                    onTap = {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Voice assistant coming soon.")
                        }
                    }
                )

                Spacer(modifier = Modifier.height(40.dp))

                GovAssistButton(
                    text = "Continue where you left off",
                    onClick = onContinueClick,
                    icon = Icons.Filled.History,
                    contentDescription = "Continue where you left off. Resume your previous application."
                )

                Spacer(modifier = Modifier.height(16.dp))

                GovAssistOutlinedButton(
                    text = "Switch to text",
                    onClick = onTextModeClick,
                    icon = Icons.Filled.Keyboard,
                    contentDescription = "Switch to text. Use typing instead of speaking."
                )

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun HomeHeader(
    onMenuClick: () -> Unit,
    onAccountClick: () -> Unit
) {
    Surface(color = NavyPrimary) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(
                onClick = onMenuClick,
                modifier = Modifier
                    .size(52.dp)
                    .semantics { contentDescription = "Open menu" }
            ) {
                Icon(
                    imageVector = Icons.Filled.Menu,
                    contentDescription = null,
                    tint = SurfaceWhite,
                    modifier = Modifier.size(30.dp)
                )
            }

            Text(
                text = "GovAssist",
                style = MaterialTheme.typography.headlineMedium,
                color = SurfaceWhite,
                modifier = Modifier.semantics {
                    heading()
                    contentDescription = "GovAssist home screen"
                }
            )

            IconButton(
                onClick = onAccountClick,
                modifier = Modifier
                    .size(52.dp)
                    .semantics { contentDescription = "Account and sign in" }
            ) {
                Icon(
                    imageVector = Icons.Filled.AccountCircle,
                    contentDescription = null,
                    tint = SurfaceWhite,
                    modifier = Modifier.size(30.dp)
                )
            }
        }
    }
}

/**
 * The large, prominent voice-assistant trigger.
 * Deliberately the single most visible element on the Home screen.
 */
@Composable
private fun MicrophoneButton(onTap: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Surface(
            onClick = onTap,
            shape = CircleShape,
            color = AccentTeal,
            shadowElevation = 6.dp,
            modifier = Modifier
                .size(160.dp)
                .semantics { contentDescription = "Start voice assistant" }
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Filled.Mic,
                    contentDescription = null,
                    tint = SurfaceWhite,
                    modifier = Modifier.size(72.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Tap to speak",
            style = MaterialTheme.typography.titleLarge,
            color = TextSecondary
        )
    }
}
