package com.matttax.youtubedownloader.core.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorPalette = darkColorScheme(
    primary = YouTubeRed,
    secondary = LeafGreen,
    background = Color.Black,
    surface = Color.Black,
    onPrimary = Color.White,
    onSecondary = Color.LightGray,
    onBackground = Color.Black,
    onSurface = Color.Black,
)

private val LightColorPalette = lightColorScheme(
    primary = YouTubeRed,
    secondary = LeafGreen,
    background = Color.White,
    surface = Color.White,
    onPrimary = Color.Black,
    onSecondary = Color.Gray,
    onBackground = Color.White,
    onSurface = Color.LightGray.copy(alpha = 0.2f),
)

@Composable
fun YouTubeDownloaderTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colorScheme = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}