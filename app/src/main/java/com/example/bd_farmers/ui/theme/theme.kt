package com.example.bd_farmers.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkFarmersColorScheme = darkColorScheme(
    primary          = Color(0xFF81C784),
    onPrimary        = Color(0xFF003300),
    primaryContainer = Color(0xFF1B5E20),
    secondary        = Color(0xFF66BB6A),
    onSecondary      = Color.Black,
    background       = Color(0xFF121212),
    surface          = Color(0xFF1E1E1E),
    onBackground     = Color(0xFFE0E0E0),
    onSurface        = Color(0xFFE0E0E0),
    error            = Color(0xFFCF6679)
)

private val LightFarmersColorScheme = lightColorScheme(
    primary          = Color(0xFF2E7D32),
    onPrimary        = Color.White,
    primaryContainer = Color(0xFFE8F5E9),
    secondary        = Color(0xFF66BB6A),
    onSecondary      = Color.White,
    background       = Color(0xFFF9FBF7),
    surface          = Color.White,
    onBackground     = Color(0xFF212121),
    onSurface        = Color(0xFF212121),
    error            = Color(0xFFE53935)
)

@Composable
fun BdFarmersTheme(
    darkTheme: Boolean = ThemeManager.isDarkTheme,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkFarmersColorScheme else LightFarmersColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        content     = content
    )
}
