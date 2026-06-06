package com.pluton.orbitscanner.core.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = AccentPurple,
    onPrimary = Color(0xFF030511),
    background = AppBackground,
    onBackground = PrimaryText,
    surface = TopNavigation,
    onSurface = PrimaryText,
    surfaceVariant = CardBackground,
    onSurfaceVariant = MutedText,
    outline = BorderPurple
)

@Composable
fun OrbitScannerTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        content = content
    )
}