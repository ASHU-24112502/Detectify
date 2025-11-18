package com.example.detectify.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

private val DetectifyColorScheme = darkColorScheme(
    primary = ElectricCyan,
    onPrimary = Midnight,
    secondary = ElectricViolet,
    onSecondary = Midnight,
    tertiary = AuroraPink,
    background = Midnight,
    surface = DeepSpace,
    onBackground = MistWhite,
    onSurface = MistWhite,
    outline = OutlineGlow,
    outlineVariant = OutlineGlow
)

private val DetectifyShapes = Shapes(
    small = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
    medium = androidx.compose.foundation.shape.RoundedCornerShape(20.dp),
    large = androidx.compose.foundation.shape.RoundedCornerShape(28.dp)
)

@Composable
fun DetectifyTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = DetectifyColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = DetectifyTypography,
        shapes = DetectifyShapes,
        content = content
    )
}
