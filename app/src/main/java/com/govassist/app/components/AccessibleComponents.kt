package com.govassist.app.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.govassist.app.ui.theme.AccentOrange
import com.govassist.app.ui.theme.BorderGrey
import com.govassist.app.ui.theme.NavyPrimary
import com.govassist.app.ui.theme.SurfaceLightGrey
import com.govassist.app.ui.theme.SurfaceWhite
import com.govassist.app.ui.theme.TextSecondary

/**
 * Standard header used at the top of secondary screens.
 * Provides a large, clearly labelled back button plus a page title.
 * The title is marked as a heading for screen reader navigation.
 */
@Composable
fun ScreenHeader(
    title: String,
    onBack: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (onBack != null) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .size(48.dp)
                    .semantics { contentDescription = "Go back" }
            ) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = null,
                    tint = NavyPrimary
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
        }
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            color = NavyPrimary,
            modifier = Modifier.semantics { heading() }
        )
    }
}

/**
 * A large, high-contrast primary button with an optional icon.
 * Used for the most important action on a screen (e.g. "Sign In").
 */
@Composable
fun GovAssistButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    contentDescription: String? = null,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 56.dp)
            .semantics {
                this.contentDescription = contentDescription ?: text
                role = Role.Button
            },
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = NavyPrimary
        )
    ) {
        if (icon != null) {
            Icon(imageVector = icon, contentDescription = null)
            Spacer(modifier = Modifier.width(10.dp))
        }
        Text(text = text, style = MaterialTheme.typography.labelLarge)
    }
}

/**
 * A large secondary/outlined button — used for actions that matter,
 * but shouldn't visually compete with the main call to action.
 */
@Composable
fun GovAssistOutlinedButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    contentDescription: String? = null
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 56.dp)
            .semantics {
                this.contentDescription = contentDescription ?: text
                role = Role.Button
            },
        shape = RoundedCornerShape(10.dp),
        border = ButtonDefaults.outlinedButtonBorder,
        colors = ButtonDefaults.outlinedButtonColors(contentColor = NavyPrimary)
    ) {
        if (icon != null) {
            Icon(imageVector = icon, contentDescription = null)
            Spacer(modifier = Modifier.width(10.dp))
        }
        Text(text = text, style = MaterialTheme.typography.labelLarge)
    }
}

/**
 * Large tappable menu row used on the Menu screen and similar list-style screens.
 * Icon + text together, generous touch target, chevron indicates navigation.
 */
@Composable
fun AccessibleMenuItem(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit,
    description: String? = null
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 64.dp)
            .semantics {
                contentDescription = description ?: text
                role = Role.Button
            },
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceLightGrey),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderGrey)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = icon, contentDescription = null, tint = NavyPrimary)
                Spacer(modifier = Modifier.width(16.dp))
                Text(text = text, style = MaterialTheme.typography.titleLarge)
            }
            Icon(
                imageVector = Icons.Filled.ChevronRight,
                contentDescription = null,
                tint = TextSecondary
            )
        }
    }
}

/**
 * A large service category card used on the Government Services screen.
 */
@Composable
fun ServiceCard(
    title: String,
    description: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .semantics {
                contentDescription = "$title. $description"
                role = Role.Button
            },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceLightGrey)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .padding(4.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = NavyPrimary,
                    modifier = Modifier.size(36.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(text = title, style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            }
        }
    }
}

/**
 * A large tappable card representing one choice in a single-select question
 * (e.g. household type, or an Australian state). Highlights when selected.
 */
@Composable
fun SelectableOptionCard(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) AccentOrange else SurfaceLightGrey
        ),
        modifier = Modifier
            .fillMaxWidth()
            .semantics {
                contentDescription = if (selected) "$label, selected" else label
            }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (selected) {
                Icon(
                    imageVector = Icons.Filled.CheckCircle,
                    contentDescription = null,
                    tint = SurfaceWhite,
                    modifier = Modifier.padding(end = 12.dp)
                )
            }
            Text(
                text = label,
                style = MaterialTheme.typography.titleLarge,
                color = if (selected) SurfaceWhite else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

/** Simple full-width card used for placeholder explanatory text on stub screens. */
@Composable
fun PlaceholderNotice(text: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceLightGrey)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            color = TextSecondary,
            modifier = Modifier.padding(20.dp)
        )
    }
}
