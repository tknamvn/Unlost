package com.dev.unlost.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = UnlostMintAccent,
    onPrimary = Slate900,
    primaryContainer = UnlostTealDark,
    onPrimaryContainer = UnlostMintContainer,
    secondary = UnlostMintLight,
    onSecondary = Slate900,
    background = Slate900,
    onBackground = Slate50,
    surface = WidgetDarkBg,
    onSurface = Slate50,
    surfaceVariant = Slate700,
    onSurfaceVariant = Slate200,
    outline = Slate600,
    outlineVariant = Slate700,
    error = ErrorRedLight,
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = UnlostTealPrimary,
    onPrimary = Color.White,
    primaryContainer = UnlostMintContainer,
    onPrimaryContainer = UnlostTealPrimary,
    secondary = UnlostTealLight,
    onSecondary = Color.White,
    secondaryContainer = UnlostMintBg,
    onSecondaryContainer = UnlostTealPrimary,
    background = Slate50,
    onBackground = Slate900,
    surface = Color.White,
    onSurface = Slate900,
    surfaceVariant = Slate100,
    onSurfaceVariant = Slate500,
    outline = Slate200,
    outlineVariant = Slate100,
    error = ErrorRed,
    onError = Color.White
)

@Composable
fun UnlostTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}