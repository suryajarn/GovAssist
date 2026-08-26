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
fun AboutScreen(onBack: () -> Unit) {
    Scaffold(containerColor = SurfaceWhite) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
        ) {
            ScreenHeader(title = "About GovAssist", onBack = onBack)

            Text(
                text = "GovAssist is an accessibility-focused prototype designed to help " +
                    "users navigate government services.",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 20.dp)
            )

            PlaceholderNotice(
                text = "GovAssist is a fictional service built to demonstrate an accessible, " +
                    "voice-first way of completing government forms. It is not affiliated " +
                    "with any real government department."
            )
        }
    }
}
