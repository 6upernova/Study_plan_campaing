package org.edu.stones.presentation.home.config

object GraphConfigDefaults {
    val Default: GraphConfig = GraphConfig()

    val Desktop: GraphConfig = GraphConfig(
        headerHeightPercent = 0.12f,
        headerHeightMinDp = 80f,
        headerHeightMaxDp = 140f,
        spriteSizeFactor = 0.65f,
    )

    val Tablet: GraphConfig = GraphConfig(
        headerHeightPercent = 0.1f,
        headerHeightMinDp = 70f,
        headerHeightMaxDp = 120f,
        spriteSizeFactor = 0.7f,
        layerSpacingPercent = 0.12f,
        fontSizeFactor = 0.12f,
    )

    val Compact: GraphConfig = GraphConfig(
        headerHeightPercent = 0.08f,
        headerHeightMinDp = 60f,
        headerHeightMaxDp = 100f,
        spriteSizeFactor = 0.55f,
        layerSpacingPercent = 0.1f,
        fontSizeMinDp = 7f,
        fontSizeMaxDp = 12f,
    )
}