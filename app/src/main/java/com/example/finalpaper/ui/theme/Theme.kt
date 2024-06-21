package com.example.finalpaper.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val RegularColorScheme = lightColorScheme(
    primary = Green40,
    onPrimary = Color.White,
    primaryContainer = Green90,
    onPrimaryContainer = Green10,
    inversePrimary = Green80,
    secondary = Violet40,
    onSecondary = Color.White,
    secondaryContainer = Violet90,
    onSecondaryContainer = Violet10,
    error = Red40,
    onError = Color.White,
    errorContainer = Red90,
    onErrorContainer = Red10,
    background = Grey99,
    onBackground = Grey10,
    surface = GreenGrey90,
    onSurface = GreenGrey30,
    inverseSurface = Grey20,
    inverseOnSurface = Grey95,
    surfaceVariant = GreenGrey90,
    onSurfaceVariant = GreenGrey30,
    outline = GreenGrey50
)

private val ColorBlindColorScheme = lightColorScheme(
    primary = ColorBlindDarkPink,
    onPrimary = Color.White,
    primaryContainer = ColorBlindLightBlue,
    onPrimaryContainer = ColorBlindDarkBlue,
    secondary = ColorBlindBlue,
    onSecondary = Color.White,
    secondaryContainer = ColorBlindPink,
    onSecondaryContainer = ColorBlindRed,
    error = Red40,
    onError = Color.White,
    errorContainer = Red90,
    onErrorContainer = Red10,
    background = Grey99,
    onBackground = Grey10,
    surface = GreenGrey90,
    onSurface = GreenGrey30,
    inverseSurface = Grey20,
    inverseOnSurface = Grey95,
    surfaceVariant = GreenGrey90,
    onSurfaceVariant = GreenGrey30,
    outline = GreenGrey50
)

@Composable
fun FinalPaperTheme(
    isColorBlind: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (isColorBlind) {
        ColorBlindColorScheme
    } else {
        RegularColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !isColorBlind
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}