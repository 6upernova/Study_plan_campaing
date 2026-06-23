package org.edu.stones.presentation.home.config

import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Stable
data class GraphConfig(
    // Layout percentages (0.0 - 1.0)
    val layerSpacingPercent: Float = 0.12f,
    val baseRowSpacingPercent: Float = 0.20f,
    val minVerticalSpacingPercent: Float = 0.1f,
    val paddingLeftPercent: Float = 0.03f,
    val paddingTopPercent: Float = 0.02f,
    val bundleSpacingPercent: Float = 0.03f,

    // Sprite sizing
    val spriteSizeFactor: Float = 0.7f,
    val spriteSizeMinPercent: Float = 0.06f,
    val spriteSizeMaxPercent: Float = 0.15f,

    // Stroke & typography
    val strokeWidthFactor: Float = 0.08f,
    val strokeWidthMinDp: Float = 2f,
    val strokeWidthMaxDp: Float = 8f,
    val fontSizeFactor: Float = 0.1f,
    val fontSizeMinDp: Float = 8f,
    val fontSizeMaxDp: Float = 14f,

    // Header
    val headerHeightPercent: Float = 0.15f,
    val headerHeightMinDp: Float = 80f,
    val headerHeightMaxDp: Float = 140f,
    val headerTopPaddingFactor: Float = 0.1f,

    // Title sign
    val signAspectRatio: Float = 2f,
    val signWidthMinDp: Float = 100f,
    val signWidthMaxDp: Float = 1000f,
    val titleFontSizeFactor: Float = 0.2f,
    val titleVerticalOffsetFactor: Float = 0.15f,
    val titleLetterSpacing: Float = 0.1f,
    // Title sign positioning & text alignment (relative 0.0-1.0 within sign)
    val signHorizontalAlign: Float = 0.0f,      // 0=left, 0.5=center, 1=right
    val signVerticalAlign: Float = 0.0f,        // 0=top, 0.5=center, 1=bottom
    val signHorizontalOffset: Float = 0.0f,     // additional offset relative to sign width
    val signVerticalOffset: Float = 0.0f,       // additional offset relative to sign height
    val titleTextAlign: Float = 0.0f,           // 0=left, 0.5=center, 1=right (within sign)
    val titleTextVerticalAlign: Float = 0.10f,   // 0=top, 0.5=center, 1=bottom (within sign)
    val titleTextOffsetX: Float = 0.0f,         // extra X offset relative to sign width
    val titleTextOffsetY: Float = 0.0f,         // extra Y offset relative to sign height

    // Canvas rendering
    val canvasExtraWidth: Float = 200f,
    val canvasMinWidth: Float = 800f,
    val scrollWheelMultiplier: Float = -0.05f,

    // Path rendering
    val catmullTension: Float = 0.5f,
    val curveControlPointFactor: Float = 0.4f,
    val directPathLenMultiplier: Float = 1.2f,
    val routedPathLenMultiplier: Float = 1.15f,

    // Path stroke layers
    val strokeShadowMultiplier: Float = 4.5f,
    val strokeDarkMultiplier: Float = 2.5f,
    val strokeMidMultiplier: Float = 3.0f,
    val strokeLightMultiplier: Float = 2.0f,

    // Texture
    val textureStepDistance: Float = 15f,
    val pebbleRadiusMin: Int = 1,
    val pebbleRadiusMax: Int = 3,
    val pebbleJitterRange: Int = 1,
    val pebbleAlpha: Float = 0.3f,

    // Colors (Compose Color)
    val colorDirtShadow: Color = Color(0x403D2B1E),
    val colorDirtDark: Color = Color(0xFF5D4037),
    val colorDirtMid: Color = Color(0xFF8D6E63),
    val colorDirtLight: Color = Color(0xFFA1887F),
    val colorTitleText: Color = Color(0xFFF5E6C8),
    val backgroundAlpha: Float = 0.8f,

    // Highlight (on node hover)
    val colorDirtHighlight: Color = Color(0xFFFFD54F),
    val colorDirtHighlightShadow: Color = Color(0x80FFD54F),
    val highlightStrokeMultiplier: Float = 5.0f,
    val dimmedAlpha: Float = 0.25f,

    // Window
    val windowWidthDp: Int = 900,
    val windowHeightDp: Int = 850,

    // Algorithm
    val maxCrossingIterations: Int = 6,
    val domainLayerPeriodWeight: Int = 2,
)