package com.govassist.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import com.govassist.app.navigation.AppNavigation
import com.govassist.app.settings.LocalAppSettings
import com.govassist.app.settings.rememberAppSettings
import com.govassist.app.ui.theme.GovAssistTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GovAssistApp()
        }
    }
}

@Composable
fun GovAssistApp() {
    val appSettings = rememberAppSettings()

    CompositionLocalProvider(LocalAppSettings provides appSettings) {
        GovAssistTheme(
            highContrast = appSettings.highContrast,
            largeText = appSettings.largeText
        ) {
            Surface(modifier = Modifier.fillMaxSize()) {
                AppNavigation()
            }
        }
    }
}
