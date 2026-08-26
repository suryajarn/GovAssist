package com.govassist.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

// A single, fixed light colour scheme is used deliberately.
// High, predictable contrast matters more here than dynamic/dark theming,
// though dark theme support can be added later without restructuring the app.
private val GovAssistColorScheme = lightColorScheme(
    primary = NavyPrimary,
    onPrimary = SurfaceWhite,
    primaryContainer = NavyPrimaryLight,
    onPrimaryContainer = SurfaceWhite,
    secondary = AccentTeal,
    onSecondary = SurfaceWhite,
    background = SurfaceWhite,
    onBackground = TextPrimary,
    surface = SurfaceWhite,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceLightGrey,
    onSurfaceVariant = TextSecondary,
    outline = BorderGrey,
    error = ErrorRed,
    onError = SurfaceWhite
)

@Composable
fun GovAssistTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = GovAssistColorScheme,
        typography = GovAssistTypography,
        content = content
    )
}
