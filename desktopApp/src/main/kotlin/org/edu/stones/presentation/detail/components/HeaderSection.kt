@file:Suppress("FunctionName")

package org.edu.stones.presentation.detail.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HeaderSection(
    title: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource("images/detailScreen/SubjectBorderName.png"),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.matchParentSize()
        )
        AutoResizingTitle(
            text = title,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 8.dp),
            color = Color.White
        )
    }
}
@Composable
private fun AutoResizingTitle(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified
) {

    val defaultFontSize = 22.sp
    val minFontSize = 12.sp

    var fontSize by remember(text) { mutableStateOf(defaultFontSize) }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = color,
            fontSize = fontSize,
            maxLines = 2,
            overflow = TextOverflow.Clip,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
            onTextLayout = { textLayoutResult ->
                if ((textLayoutResult.didOverflowWidth || textLayoutResult.didOverflowHeight) && fontSize > minFontSize) {
                    fontSize = (fontSize.value - 0.5f).sp
                } else if (!textLayoutResult.didOverflowWidth && !textLayoutResult.didOverflowHeight && fontSize < defaultFontSize) {
                    fontSize = (fontSize.value + 0.5f).sp
                }
            }
        )
    }
}
