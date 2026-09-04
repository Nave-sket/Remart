package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = PinkPrimary,
    onPrimary = Color.White,
    primaryContainer = PinkLight,
    onPrimaryContainer = PinkDark,
    secondary = CharcoalDark,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFF3F4F6),
    onSecondaryContainer = CharcoalDark,
    tertiary = GreenSuccess,
    onTertiary = Color.White,
    background = GraySurface,
    onBackground = CharcoalDark,
    surface = Color.White,
    onSurface = CharcoalDark,
    surfaceVariant = Color(0xFFF3F4F6),
    onSurfaceVariant = GrayText,
    outline = GrayBorder
)

private val DarkColorScheme = darkColorScheme(
    primary = PinkPrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFF4A102A),
    onPrimaryContainer = Color(0xFFFFD8E6),
    secondary = Color(0xFFE5E7EB),
    onSecondary = CharcoalDark,
    background = Color(0xFF121212),
    onBackground = Color(0xFFEEEEEE),
    surface = Color(0xFF1E1E1E),
    onSurface = Color(0xFFEEEEEE),
    surfaceVariant = Color(0xFF2C2C2C),
    onSurfaceVariant = Color(0xFFB0B0B0),
    outline = Color(0xFF3E3E3E)
)

@Composable
fun ReMartTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
