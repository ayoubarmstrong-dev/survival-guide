package com.antigravity.survival.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = TacticalGreen,
    secondary = HazardAmber,
    tertiary = TacticalCyan,
    background = AmoledBlack,
    surface = DarkSurface,
    surfaceVariant = DarkCard,
    onPrimary = AmoledBlack,
    onSecondary = AmoledBlack,
    onTertiary = AmoledBlack,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    outline = DarkBorder
)

@Composable
fun SurvivalTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        content = content
    )
}
