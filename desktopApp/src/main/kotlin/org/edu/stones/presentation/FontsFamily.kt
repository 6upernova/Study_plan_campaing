package org.edu.stones.presentation

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.platform.Font // <-- Asegúrate de importar este paquete para Desktop

val MiFuentePersonalizada = FontFamily(
    Font(
        resource = "fonts/friz_quadrata.ttf", // Usamos resource en lugar de path
        weight = FontWeight.Normal
    )
)

// 1. Creamos una base con la tipografía por defecto (no requiere @Composable)
private val defaultTypography = Typography()

// 2. Definimos la tipografía de la app copiando los estilos de la base
val AppTypography = Typography(
    displayLarge = defaultTypography.displayLarge.copy(fontFamily = MiFuentePersonalizada),
    headlineSmall = defaultTypography.headlineSmall.copy(fontFamily = MiFuentePersonalizada),
    titleLarge = defaultTypography.titleLarge.copy(fontFamily = MiFuentePersonalizada),
    bodyLarge = defaultTypography.bodyLarge.copy(fontFamily = MiFuentePersonalizada),
    bodyMedium = defaultTypography.bodyMedium.copy(fontFamily = MiFuentePersonalizada)
)