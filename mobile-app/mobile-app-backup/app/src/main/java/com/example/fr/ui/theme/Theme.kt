package com.example.fr.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Enforcing the FloodRescue dark theme regardless of system setting
private val AppColorScheme = darkColorScheme(
    primary = RescuePrimary,
    secondary = RescuePrimary, // Using accent for secondary elements too
    background = RescueDarkBackground,
    surface = RescueSurface,
    onPrimary = RescueOnPrimary,
    onSecondary = RescueOnPrimary,
    onBackground = RescueTextPrimary,
    onSurface = RescueTextPrimary,
    onSurfaceVariant = RescueTextSecondary
)

@Composable
fun FrTheme(
    // The darkTheme parameter is kept, but we always apply the same theme
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    // Always use the custom AppColorScheme for the FloodRescue theme
    val colorScheme = AppColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
