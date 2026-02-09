package com.aethertv.ui.theme

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Surface
import androidx.tv.material3.darkColorScheme

private val AetherTvColorScheme = darkColorScheme(
    primary = PrimaryDark,
    onPrimary = OnPrimaryDark,
    primaryContainer = PrimaryContainerDark,
    onPrimaryContainer = OnPrimaryContainerDark,
    secondary = SecondaryDark,
    onSecondary = OnSecondaryDark,
    secondaryContainer = SecondaryContainerDark,
    onSecondaryContainer = OnSecondaryContainerDark,
    tertiary = TertiaryDark,
    onTertiary = OnTertiaryDark,
    tertiaryContainer = TertiaryContainerDark,
    error = ErrorDark,
    onError = OnErrorDark,
    errorContainer = ErrorContainerDark,
    surface = SurfaceDark,
    onSurface = OnSurfaceDark,
    onSurfaceVariant = OnSurfaceVariantDark,
)

private val HighContrastColorScheme = darkColorScheme(
    primary = Color(0xFFFFFFFF),
    onPrimary = Color(0xFF000000),
    primaryContainer = Color(0xFF0066CC),
    onPrimaryContainer = Color(0xFFFFFFFF),
    secondary = Color(0xFFFFFFFF),
    onSecondary = Color(0xFF000000),
    secondaryContainer = Color(0xFF444444),
    onSecondaryContainer = Color(0xFFFFFFFF),
    tertiary = Color(0xFFFFFFFF),
    onTertiary = Color(0xFF000000),
    tertiaryContainer = Color(0xFF444444),
    error = Color(0xFFFF5555),
    onError = Color(0xFF000000),
    errorContainer = Color(0xFFCC0000),
    surface = Color(0xFF000000),
    onSurface = Color(0xFFFFFFFF),
    onSurfaceVariant = Color(0xFFEEEEEE),
)

// Composition local for high contrast mode
val LocalHighContrast = staticCompositionLocalOf { false }

@Composable
fun AetherTvTheme(
    highContrast: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (highContrast) HighContrastColorScheme else AetherTvColorScheme
    
    CompositionLocalProvider(LocalHighContrast provides highContrast) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = AetherTvTypography,
        ) {
            Surface(
                modifier = Modifier.fillMaxSize(),
            ) {
                content()
            }
        }
    }
}
