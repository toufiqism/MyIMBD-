package com.tofiq.myimdb.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Color(0xFFC1121F),
    onPrimary = Color.White,
    primaryContainer = Color(0xFF780000),
    onPrimaryContainer = Color.White,
    secondary = Color(0xFF669BBC),
    onSecondary = Color.White,
    background = Color(0xFFFDF0D5),
    onBackground = Color(0xFF003049),
    surface = Color(0xFFFDF0D5),
    onSurface = Color(0xFF003049),
    error = Color(0xFF780000),
    onError = Color.White,
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFFC1121F),
    onPrimary = Color.Black,
    primaryContainer = Color(0xFF780000),
    onPrimaryContainer = Color.White,
    secondary = Color(0xFF669BBC),
    onSecondary = Color.Black,
    background = Color(0xFF003049),
    onBackground = Color(0xFFFDF0D5),
    surface = Color(0xFF003049),
    onSurface = Color(0xFFFDF0D5),
    error = Color(0xFF780000),
    onError = Color.White,
)

@Composable
fun MyIMDBTheme(
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (useDarkTheme) DarkColors else LightColors

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography(), // You can customize this
        shapes = Shapes(),         // And this if needed
        content = content
    )
}
