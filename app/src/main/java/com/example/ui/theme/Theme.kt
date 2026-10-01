package com.example.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

private val PocketLedgerColorScheme = lightColorScheme(
    primary = PocketPrimary,
    onPrimary = PocketWhite,
    primaryContainer = PocketPrimaryContainer,
    onPrimaryContainer = PocketOnPrimaryContainer,
    secondary = PocketSecondary,
    onSecondary = PocketWhite,
    secondaryContainer = PocketSecondaryContainer,
    onSecondaryContainer = PocketOnSecondaryContainer,
    background = PocketBackground,
    onBackground = PocketTextDarkNavy,
    surface = PocketSurface,
    onSurface = PocketTextDarkNavy,
    surfaceVariant = PocketSurfaceVariant,
    onSurfaceVariant = PocketTextBlueGray,
    outline = PocketBorder,
    outlineVariant = PocketBorderSubtle
)

val PocketShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(20.dp),
    extraLarge = RoundedCornerShape(28.dp)
)

@Composable
fun PocketLedgerTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = PocketLedgerColorScheme,
        typography = Typography,
        shapes = PocketShapes,
        content = content
    )
}
