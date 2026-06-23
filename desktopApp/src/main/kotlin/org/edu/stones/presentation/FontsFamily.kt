package org.edu.stones.presentation

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.platform.Font

val MiFuentePersonalizada = FontFamily(
    Font(
        resource = "fonts/friz_quadrata.ttf",
        weight = FontWeight.Normal
    )
)

private val defaultTypography = Typography()

val AppTypography = Typography(
    displayLarge = defaultTypography.displayLarge.copy(fontFamily = MiFuentePersonalizada),
    headlineSmall = defaultTypography.headlineSmall.copy(fontFamily = MiFuentePersonalizada),
    titleLarge = defaultTypography.titleLarge.copy(fontFamily = MiFuentePersonalizada),
    bodyLarge = defaultTypography.bodyLarge.copy(fontFamily = MiFuentePersonalizada),
    bodyMedium = defaultTypography.bodyMedium.copy(fontFamily = MiFuentePersonalizada)
)
