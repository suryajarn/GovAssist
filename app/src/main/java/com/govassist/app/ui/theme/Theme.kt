package com.govassist.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.TextUnit

// Standard, restrained light colour scheme.
private val StandardColorScheme = lightColorScheme(
    primary = NavyPrimary,
    onPrimary = SurfaceWhite,
    primaryContainer = NavyPrimaryLight,
    onPrimaryContainer = SurfaceWhite,
    secondary = AccentOrange,
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

// A slightly brighter red reads better than ErrorRed on a pure black background.
private val ErrorOnHighContrast = Color(0xFFFF6B5E)

// Pure black/white, higher-contrast scheme used when "High contrast" is enabled
// in Help & Accessibility. Kept as a single fixed alternative rather than a
// dynamic calculation, so contrast stays predictable.
private val HighContrastColorScheme = lightColorScheme(
    primary = HighContrastBackground,
    onPrimary = HighContrastText,
    primaryContainer = HighContrastBackground,
    onPrimaryContainer = HighContrastText,
    secondary = HighContrastAccent,
    onSecondary = HighContrastBackground,
    background = HighContrastBackground,
    onBackground = HighContrastText,
    surface = HighContrastSurface,
    onSurface = HighContrastText,
    surfaceVariant = HighContrastBackground,
    onSurfaceVariant = HighContrastText,
    outline = HighContrastBorder,
    error = ErrorOnHighContrast,
    onError = HighContrastText
)

/**
 * Scales every font size in [base] by [scale]. Used to implement the
 * "Larger text" accessibility option without maintaining two full
 * Typography definitions by hand.
 */
private fun scaledTypography(base: Typography, scale: Float): Typography {
    fun TextStyle.scaled(): TextStyle {
        val size = fontSize
        val newSize: TextUnit = if (size.isSp) size * scale else size
        return copy(fontSize = newSize)
    }
    return base.copy(
        headlineLarge = base.headlineLarge.scaled(),
        headlineMedium = base.headlineMedium.scaled(),
        titleLarge = base.titleLarge.scaled(),
        bodyLarge = base.bodyLarge.scaled(),
        bodyMedium = base.bodyMedium.scaled(),
        labelLarge = base.labelLarge.scaled()
    )
}

/**
 * App-wide theme.
 *
 * @param highContrast when true, swaps to a fixed pure black/white palette.
 * @param largeText when true, scales all text up by ~20% for easier reading.
 * Both are controlled from the Help & Accessibility screen and held in
 * [com.govassist.app.settings.AppSettings], so this composable stays a pure
 * function of those two flags.
 */
@Composable
fun GovAssistTheme(
    highContrast: Boolean = false,
    largeText: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (highContrast) HighContrastColorScheme else StandardColorScheme
    val typography = scaledTypography(GovAssistTypography, if (largeText) 1.2f else 1.0f)

    MaterialTheme(
        colorScheme = colorScheme,
        typography = typography,
        content = content
    )
}
