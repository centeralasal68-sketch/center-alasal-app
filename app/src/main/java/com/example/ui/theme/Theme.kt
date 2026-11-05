package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = SkyBlue,
    onPrimary = SurfaceLight,
    primaryContainer = SkyBlueLight,
    onPrimaryContainer = SkyBlue,
    secondary = ChampagneGold,
    onSecondary = SurfaceLight,
    secondaryContainer = ChampagneGoldLight,
    onSecondaryContainer = ChampagneGoldDark,
    tertiary = SageGreen,
    onTertiary = SurfaceLight,
    tertiaryContainer = SageGreenLight,
    onTertiaryContainer = SageGreen,
    background = IvoryWhite,
    onBackground = TextPrimary,
    surface = CardBackground,
    onSurface = TextPrimary,
    surfaceVariant = WarmOffWhite,
    onSurfaceVariant = TextSecondary,
    outline = BorderSubtle,
    outlineVariant = FrostBlue
)

@Composable
fun CenterAlAssalTheme(
    content: @Composable () -> Unit
) {
    // Center Al Assal is strictly modern luxury light theme per requirements
    val colorScheme = LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = IvoryWhite.toArgb()
            window.navigationBarColor = IvoryWhite.toArgb()
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = true
                isAppearanceLightNavigationBars = true
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}
