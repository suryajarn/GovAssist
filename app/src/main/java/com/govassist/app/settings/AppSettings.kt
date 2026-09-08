package com.govassist.app.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

/**
 * Holds the small set of accessibility/display preferences the prototype
 * supports so far. This is deliberately in-memory only (it resets when the
 * app restarts) — wiring it up to DataStore/SharedPreferences for real
 * persistence is a natural next step, not part of this skeleton.
 */
class AppSettings {
    var highContrast by mutableStateOf(false)
    var largeText by mutableStateOf(false)
    var floatingAssistantEnabled by mutableStateOf(false)
}

/** Makes the current [AppSettings] available to any composable without prop-drilling. */
val LocalAppSettings = compositionLocalOf { AppSettings() }

@Composable
fun rememberAppSettings(): AppSettings {
    return androidx.compose.runtime.remember { AppSettings() }
}
