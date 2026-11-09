package com.example.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

enum class AppearanceMode(val label: String) {
    GLASS_DEFAULT("Glass (Default)"),
    OLED_BLACK("Deep OLED Black"),
    MIDNIGHT_NEBULA("Midnight Nebula")
}

private val GlassColorScheme = darkColorScheme(
    primary = ElectricViolet,
    onPrimary = Color.White,
    primaryContainer = Color(0xFF2E1D59),
    onPrimaryContainer = SoftLavender,
    secondary = LuminousPurple,
    onSecondary = Color.White,
    secondaryContainer = GlassCardHighlight,
    onSecondaryContainer = TextPrimary,
    tertiary = OrchidPink,
    onTertiary = Color.White,
    background = MidnightNavy,
    onBackground = TextPrimary,
    surface = DeepSpaceSurface,
    onSurface = TextPrimary,
    surfaceVariant = GlassCardSurface,
    onSurfaceVariant = TextSecondary,
    outline = SubtleBorderGlow,
    error = CoralWarning
)

private val OledColorScheme = GlassColorScheme.copy(
    background = Color(0xFF000000),
    surface = Color(0xFF090A10),
    surfaceVariant = Color(0xFF121420)
)

private val NebulaColorScheme = GlassColorScheme.copy(
    background = Color(0xFF0E0B20),
    surface = Color(0xFF171332),
    surfaceVariant = Color(0xFF221C44)
)

val AppShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(22.dp),
    extraLarge = RoundedCornerShape(28.dp)
)

@Composable
fun AnimeFlixTheme(
    appearanceMode: AppearanceMode = AppearanceMode.GLASS_DEFAULT,
    content: @Composable () -> Unit
) {
    val colorScheme = when (appearanceMode) {
        AppearanceMode.GLASS_DEFAULT -> GlassColorScheme
        AppearanceMode.OLED_BLACK -> OledColorScheme
        AppearanceMode.MIDNIGHT_NEBULA -> NebulaColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = AppShapes,
        content = content
    )
}
