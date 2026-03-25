package com.eveningoutpost.dexdrip.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import android.os.Build

// MiniMed 780G brand-inspired teal palette
private val MjPrimaryLight = Color(0xFF006A6A)
private val MjOnPrimaryLight = Color(0xFFFFFFFF)
private val MjPrimaryContainerLight = Color(0xFF9DF0F0)
private val MjOnPrimaryContainerLight = Color(0xFF002020)
private val MjSecondaryLight = Color(0xFF4A6363)
private val MjOnSecondaryLight = Color(0xFFFFFFFF)
private val MjSecondaryContainerLight = Color(0xFFCCE8E8)
private val MjOnSecondaryContainerLight = Color(0xFF051F1F)
private val MjTertiaryLight = Color(0xFF4B607B)
private val MjOnTertiaryLight = Color(0xFFFFFFFF)
private val MjTertiaryContainerLight = Color(0xFFD3E4FF)
private val MjOnTertiaryContainerLight = Color(0xFF041C35)
private val MjErrorLight = Color(0xFFBA1A1A)
private val MjOnErrorLight = Color(0xFFFFFFFF)
private val MjErrorContainerLight = Color(0xFFFFDAD6)
private val MjOnErrorContainerLight = Color(0xFF410002)
private val MjBackgroundLight = Color(0xFFFAFDFD)
private val MjOnBackgroundLight = Color(0xFF191C1C)
private val MjSurfaceLight = Color(0xFFFAFDFD)
private val MjOnSurfaceLight = Color(0xFF191C1C)
private val MjSurfaceVariantLight = Color(0xFFDAE5E5)
private val MjOnSurfaceVariantLight = Color(0xFF3F4949)

private val MjPrimaryDark = Color(0xFF4FD8D8)
private val MjOnPrimaryDark = Color(0xFF003737)
private val MjPrimaryContainerDark = Color(0xFF004F4F)
private val MjOnPrimaryContainerDark = Color(0xFF9DF0F0)
private val MjSecondaryDark = Color(0xFFB0CCCC)
private val MjOnSecondaryDark = Color(0xFF1B3434)
private val MjSecondaryContainerDark = Color(0xFF324B4B)
private val MjOnSecondaryContainerDark = Color(0xFFCCE8E8)
private val MjTertiaryDark = Color(0xFFADC8E8)
private val MjOnTertiaryDark = Color(0xFF1A314C)
private val MjTertiaryContainerDark = Color(0xFF334862)
private val MjOnTertiaryContainerDark = Color(0xFFD3E4FF)
private val MjErrorDark = Color(0xFFFFB4AB)
private val MjOnErrorDark = Color(0xFF690005)
private val MjErrorContainerDark = Color(0xFF93000A)
private val MjOnErrorContainerDark = Color(0xFFFFDAD6)
private val MjBackgroundDark = Color(0xFF191C1C)
private val MjOnBackgroundDark = Color(0xFFE0E3E3)
private val MjSurfaceDark = Color(0xFF191C1C)
private val MjOnSurfaceDark = Color(0xFFE0E3E3)
private val MjSurfaceVariantDark = Color(0xFF3F4949)
private val MjOnSurfaceVariantDark = Color(0xFFBEC9C9)

private val LightColorScheme = lightColorScheme(
    primary = MjPrimaryLight,
    onPrimary = MjOnPrimaryLight,
    primaryContainer = MjPrimaryContainerLight,
    onPrimaryContainer = MjOnPrimaryContainerLight,
    secondary = MjSecondaryLight,
    onSecondary = MjOnSecondaryLight,
    secondaryContainer = MjSecondaryContainerLight,
    onSecondaryContainer = MjOnSecondaryContainerLight,
    tertiary = MjTertiaryLight,
    onTertiary = MjOnTertiaryLight,
    tertiaryContainer = MjTertiaryContainerLight,
    onTertiaryContainer = MjOnTertiaryContainerLight,
    error = MjErrorLight,
    errorContainer = MjErrorContainerLight,
    onError = MjOnErrorLight,
    onErrorContainer = MjOnErrorContainerLight,
    background = MjBackgroundLight,
    onBackground = MjOnBackgroundLight,
    surface = MjSurfaceLight,
    onSurface = MjOnSurfaceLight,
    surfaceVariant = MjSurfaceVariantLight,
    onSurfaceVariant = MjOnSurfaceVariantLight,
)

private val DarkColorScheme = darkColorScheme(
    primary = MjPrimaryDark,
    onPrimary = MjOnPrimaryDark,
    primaryContainer = MjPrimaryContainerDark,
    onPrimaryContainer = MjOnPrimaryContainerDark,
    secondary = MjSecondaryDark,
    onSecondary = MjOnSecondaryDark,
    secondaryContainer = MjSecondaryContainerDark,
    onSecondaryContainer = MjOnSecondaryContainerDark,
    tertiary = MjTertiaryDark,
    onTertiary = MjOnTertiaryDark,
    tertiaryContainer = MjTertiaryContainerDark,
    onTertiaryContainer = MjOnTertiaryContainerDark,
    error = MjErrorDark,
    errorContainer = MjErrorContainerDark,
    onError = MjOnErrorDark,
    onErrorContainer = MjOnErrorContainerDark,
    background = MjBackgroundDark,
    onBackground = MjOnBackgroundDark,
    surface = MjSurfaceDark,
    onSurface = MjOnSurfaceDark,
    surfaceVariant = MjSurfaceVariantDark,
    onSurfaceVariant = MjOnSurfaceVariantDark,
)

@Composable
fun MjAppTheme(
    darkTheme: Boolean = androidx.compose.foundation.isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit,
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
        content = content,
    )
}
