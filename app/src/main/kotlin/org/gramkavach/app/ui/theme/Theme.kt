package org.gramkavach.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = SecurityBlue,
    secondary = BlueSecondary,
    tertiary = BlueTertiary,
    error = CriticalRed
)

private val LightColorScheme = lightColorScheme(
    primary = SecurityBlue,
    secondary = BlueSecondary,
    tertiary = BlueTertiary,
    background = Background,
    surface = Surface,
    error = CriticalRed
)

@Composable
fun GramKavachTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
