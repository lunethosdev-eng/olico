package com.music.pokke.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import com.music.pokke.data.settings.AppSettings

private val Dark = darkColorScheme(
    primary = Color.White,
    onPrimary = Color.Black,
    background = Color(0xFF000000),
    surface = Color(0xFF121212),
    onBackground = Color.White,
    onSurface = Color.White,
    surfaceVariant = Color(0xFF1C1C1C),
)

private val Light = lightColorScheme(
    primary = Color.Black,
    onPrimary = Color.White,
    background = Color(0xFFF5F5F5),
    surface = Color.White,
    onBackground = Color.Black,
    onSurface = Color.Black,
)

@Composable
fun PokkeTheme(content: @Composable () -> Unit) {
    val darkPref by AppSettings.darkTheme.collectAsState()
    val dark = if (darkPref) true else isSystemInDarkTheme() && false // explicit: use setting
    MaterialTheme(
        colorScheme = if (darkPref) Dark else Light,
        content = content,
    )
}
