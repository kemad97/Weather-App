package com.example.weatherapp.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    secondary = PurpleGrey80,
    tertiary = Pink80,
    background = Color.Transparent,
    surface = Color(0x66000000),
    surfaceVariant = Color(0x66000000),
    onSurface = Color.White,
    onSurfaceVariant = Color.White,
    onBackground = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40,

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
    background = Color.Transparent,
    surface = Color(0x66FFFFFF),
    surfaceVariant = Color(0x66FFFFFF),
    onSurface = Color.White,
    onSurfaceVariant = Color.White,
    onBackground = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White

)
//
//@Composable
//fun WeatherAppTheme(
//    darkTheme: Boolean = true, // Force dark theme
//    dynamicColor: Boolean = false, // Disable dynamic color
//    content: @Composable () -> Unit
//) {
////    val colorScheme = when {
////        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
////            val context = LocalContext.current
////            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
////        }
////
////        darkTheme -> DarkColorScheme
////        else -> LightColorScheme
////    }
//    val colorScheme = DarkColorScheme.copy( // Force dark color scheme with white text
//        background = Color.Transparent,
//        surface = Color(0x66000000),
//        surfaceVariant = Color(0x66000000),
//        onSurface = Color.White,
//        onSurfaceVariant = Color.White,
//        onBackground = Color.White,
//        onPrimary = Color.White,
//        onSecondary = Color.White,
//        onTertiary = Color.White
//    )
//
//    //add view
//    val view = LocalView.current
//    if (!view.isInEditMode) {
//        SideEffect {
//            val window = (view.context as Activity).window
//            window.statusBarColor = Color.Transparent.toArgb()
//            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
//        }
//    }
//
//    MaterialTheme(
//        colorScheme = colorScheme,
//        typography = Typography,
//        content = content
//    )
//}


@Composable
fun WeatherAppTheme(
    darkTheme: Boolean = true, // Force dark theme
    dynamicColor: Boolean = true, // Disable dynamic color
    content: @Composable () -> Unit
) {
    val colorScheme = DarkColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = false
            }
            window.statusBarColor = Color.Transparent.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}