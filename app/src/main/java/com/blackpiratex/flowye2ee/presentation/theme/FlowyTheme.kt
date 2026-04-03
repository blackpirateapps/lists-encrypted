package com.blackpiratex.flowye2ee.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColors = lightColorScheme(
    primary = Color(0xFF2B6F6A),
    secondary = Color(0xFF596D62),
    tertiary = Color(0xFF9A6A3A),
    background = Color(0xFFF7F6F2),
    surface = Color(0xFFFCFBF7),
    onPrimary = Color(0xFFFFFFFF),
    onSecondary = Color(0xFFFFFFFF),
    onBackground = Color(0xFF1E1E1C),
    onSurface = Color(0xFF1E1E1C)
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF7ED0C8),
    secondary = Color(0xFFBCCBBF),
    tertiary = Color(0xFFE5B27A),
    background = Color(0xFF101413),
    surface = Color(0xFF171C1A),
    onPrimary = Color(0xFF0C2020),
    onSecondary = Color(0xFF101413),
    onBackground = Color(0xFFE9E9E2),
    onSurface = Color(0xFFE9E9E2)
)

@Composable
fun FlowyTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColors else LightColors
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as android.app.Activity).window
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = !darkTheme
        }
    }
    MaterialTheme(
        colorScheme = colors,
        typography = FlowyTypography,
        content = content
    )
}
