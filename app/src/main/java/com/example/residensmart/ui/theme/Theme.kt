package com.example.residensmart.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = BrandBlue,
    onPrimary = EditorialBg,
    primaryContainer = EditorialHighlight,
    onPrimaryContainer = BrandBlue,
    secondary = BrandTeal,
    onSecondary = EditorialBg,
    secondaryContainer = EditorialHighlight,
    onSecondaryContainer = BrandTeal,
    background = EditorialBg,
    onBackground = EditorialText,
    surface = CardSurface,
    onSurface = EditorialText,
    surfaceVariant = EditorialHighlight,
    onSurfaceVariant = EditorialMuted,
    outline = EditorialBorder
)

@Composable
fun ResidentSmartTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}
