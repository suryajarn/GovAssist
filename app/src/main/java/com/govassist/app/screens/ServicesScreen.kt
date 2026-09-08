package com.govassist.app.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.govassist.app.components.ScreenHeader
import com.govassist.app.components.ServiceCard
import com.govassist.app.ui.theme.SurfaceWhite
import com.govassist.app.ui.theme.TextSecondary
import kotlinx.coroutines.launch

private data class ServiceEntry(
    val title: String,
    val description: String,
    val icon: ImageVector
)

private val serviceEntries = listOf(
    ServiceEntry(
        "Centrelink Services",
        "Payments and support services (prototype category).",
        Icons.Filled.Groups
    ),
    ServiceEntry(
        "Medicare Services",
        "Health-related services (prototype category).",
        Icons.Filled.LocalHospital
    ),
    ServiceEntry(
        "Tax & Financial Services",
        "Tax and financial matters (prototype category).",
        Icons.Filled.Savings
    ),
    ServiceEntry(
        "Housing Services",
        "Housing and accommodation support (prototype category).",
        Icons.Filled.Apartment
    ),
    ServiceEntry(
        "Other Government Services",
        "All other service categories (prototype category).",
        Icons.Filled.MoreHoriz
    )
)

@Composable
fun ServicesScreen(onBack: () -> Unit) {
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        containerColor = SurfaceWhite,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            item {
                ScreenHeader(title = "Other Government Services", onBack = onBack)
                Text(
                    text = "The Commonwealth Seniors Health Care Card has its own guided " +
                        "flow — see \"Check CSHC Eligibility\" in the menu. The categories " +
                        "below are other prototype placeholders.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
            items(serviceEntries) { entry ->
                ServiceCard(
                    title = entry.title,
                    description = entry.description,
                    icon = entry.icon,
                    onClick = {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar(
                                "This service is currently part of the prototype."
                            )
                        }
                    }
                )
            }
        }
    }
}
