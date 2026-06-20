package org.edu.stones.presentation.home.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.ImageShader
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.res.loadImageBitmap
import androidx.compose.ui.res.useResource

@Composable
fun GraphBackground(
    imagePath: String = "images/background.png",
    alpha: Float = 1f
) {

    val imageBitmap = remember(imagePath) {
        useResource(imagePath) { inputStream ->
            loadImageBitmap(inputStream)
        }
    }

    val shaderBrush = ShaderBrush(
        ImageShader(
            image = imageBitmap,
            tileModeX = TileMode.Repeated,
            tileModeY = TileMode.Repeated
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .alpha(alpha)

            .drawBehind {
                drawRect(brush = shaderBrush)
            }
    )
}