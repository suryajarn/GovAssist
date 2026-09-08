package com.govassist.app.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.govassist.app.components.PlaceholderNotice
import com.govassist.app.components.ScreenHeader
import com.govassist.app.ui.theme.NavyPrimary
import com.govassist.app.ui.theme.SurfaceWhite

@Composable
fun AboutScreen(onBack: () -> Unit) {
    Scaffold(containerColor = SurfaceWhite) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            ScreenHeader(title = "About GovAssist", onBack = onBack)

            Text(
                text = "GovAssist is an accessibility-focused prototype designed to help " +
                    "users navigate government services.",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 20.dp)
            )

            SectionHeading("What GovAssist does")
            BodyText(
                "GovAssist helps elderly citizens — including people who are blind or " +
                    "have low vision — check their eligibility for, and apply for, the " +
                    "Commonwealth Seniors Health Care Card (CSHC), by voice or text. It " +
                    "is aimed at self-funded retirees past Age Pension age who don't " +
                    "already receive an income-support payment, and can also be used by " +
                    "a guardian or carer helping someone with their application."
            )

            SectionHeading("How it works")
            BodyText(
                "Rather than logging in and completing the application on the user's " +
                    "behalf, GovAssist prepares a valid draft and then talks the user " +
                    "through entering or confirming it in their own myGov session — " +
                    "reading on-screen content aloud and describing what to do next. " +
                    "Every step that submits or changes information waits for the " +
                    "user's explicit confirmation, and a full summary is shown before " +
                    "anything is submitted."
            )

            SectionHeading("Why not automate it directly?")
            BodyText(
                "Centrelink's terms of use prohibit automated or bot-like access to " +
                    "myGov and Centrelink online services. GovAssist is built around " +
                    "that constraint: it guides a human through their own session " +
                    "rather than acting as that person online."
            )

            SectionHeading("Privacy")
            BodyText(
                "Sensitive details such as tax file numbers, Centrelink reference " +
                    "numbers, and bank details are designed to be detected and redacted " +
                    "before any information reaches an external AI model."
            )

            Spacer(modifier = Modifier.height(8.dp))

            PlaceholderNotice(
                text = "This is an early prototype. GovAssist is a fictional service, " +
                    "not affiliated with any real government department, and none of " +
                    "the features described above are functional yet in this build."
            )

            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
private fun SectionHeading(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleLarge,
        color = NavyPrimary,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
private fun BodyText(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyLarge,
        modifier = Modifier.padding(bottom = 20.dp)
    )
}
