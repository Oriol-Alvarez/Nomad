package com.example.app.ui.theme

import android.app.Activity
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
    primary = Color(0xFF4FC3F7),    // Azul brillante para destacar

    background = DarkBg,            // #121212
    surface = DarkSurf,             // #1E1E1E
    surfaceVariant = DarkSurf,
    tertiary = Color(0xFF2C2C2E),
    // Textos y elementos sobre fondo
    onPrimary = Color.White,
    inversePrimary = Color.Black,
    onBackground = Color.White,
    onSurface = Color.White,
    onSurfaceVariant = Color(0xFFB0BEC5), // Gris azulado suave para etiquetas
    outline = Color(0xFF3D3D3D),
    surfaceContainer = Color(0xFF4FC3F7)

)

private val LightColorScheme = lightColorScheme(
    primary = NomadBlue,
    background = LightBg,
    surface = LightSurf,
    surfaceVariant = NomadBlueDark,
    tertiary = Color(0xFFF5F5F7),

    // Textos y elementos sobre fondo
    onPrimary = Color.Black,
    inversePrimary = Color.White,
    onBackground = Color(0xFF0F172A), // Negro azulado muy elegante
    onSurface = Color(0xFF0F172A),
    onSurfaceVariant = NomadBlueDark, // Azul oscuro para etiquetas/subtítulos
    outline = Color(0xFFCBD5E1),
    surfaceContainer = NomadBlueDark

)

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
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