package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val LightColorScheme = lightColorScheme(
    primary = MenuKuGreenPrimary,
    onPrimary = Color.White,
    primaryContainer = MenuKuGreenContainer,
    onPrimaryContainer = MenuKuGreenOnContainer,
    inversePrimary = MenuKuGreenLight,
    
    secondary = MenuKuOrange,
    onSecondary = Color.White,
    secondaryContainer = MenuKuOrangeContainer,
    onSecondaryContainer = MenuKuOrangeOnContainer,
    
    tertiary = MenuKuTertiary,
    onTertiary = Color.White,
    tertiaryContainer = MenuKuTertiaryContainer,
    onTertiaryContainer = MenuKuTertiaryOnContainer,
    
    background = MenuKuBackground,
    onBackground = MenuKuOnSurface,
    
    surface = MenuKuSurface,
    onSurface = MenuKuOnSurface,
    surfaceVariant = MenuKuSurfaceContainerHighest,
    onSurfaceVariant = MenuKuOnSurfaceVariant,
    
    surfaceContainerLowest = MenuKuSurfaceContainerLowest,
    surfaceContainerLow = MenuKuSurfaceContainerLow,
    surfaceContainer = MenuKuSurfaceContainer,
    surfaceContainerHigh = MenuKuSurfaceContainerHigh,
    surfaceContainerHighest = MenuKuSurfaceContainerHighest,
    surfaceBright = MenuKuSurfaceBright,
    surfaceDim = MenuKuSurfaceDim,
    
    outline = MenuKuOutline,
    outlineVariant = MenuKuOutlineVariant,
    
    error = MenuKuError,
    onError = MenuKuOnError,
    errorContainer = MenuKuErrorContainer,
    onErrorContainer = MenuKuOnErrorContainer
)

private val DarkColorScheme = darkColorScheme(
    primary = MenuKuGreenLight,
    onPrimary = Color(0xFF00390A),
    primaryContainer = MenuKuGreenContainer,
    onPrimaryContainer = MenuKuGreenOnContainer,
    
    secondary = MenuKuOrangeLight,
    onSecondary = Color(0xFF4E1600),
    secondaryContainer = MenuKuOrangeContainer,
    onSecondaryContainer = MenuKuOrangeLight,
    
    background = Color(0xFF1A1C19),
    onBackground = Color(0xFFE3E3DE),
    surface = Color(0xFF1A1C19),
    onSurface = Color(0xFFE3E3DE),
    surfaceVariant = Color(0xFF40493D),
    onSurfaceVariant = Color(0xFFBFCABA)
)

val MenuKuShapes = Shapes(
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(32.dp)
)

@Composable
fun MenuKuTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = MenuKuTypography,
        shapes = MenuKuShapes,
        content = content
    )
}
