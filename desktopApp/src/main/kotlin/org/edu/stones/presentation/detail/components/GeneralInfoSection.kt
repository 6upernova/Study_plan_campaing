@file:Suppress("FunctionName")

package org.edu.stones.presentation.detail.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.edu.stones.domain.entity.Subject
import org.edu.stones.presentation.AppTypography
import org.edu.stones.presentation.detail.DetailViewModel.DetailUiState
import org.edu.stones.presentation.detail.DetailViewModel
import org.jetbrains.skia.Image as SkiaImage


@Composable
fun GeneralInfoSection(
    subject: Subject,
    uiState: DetailViewModel.DetailUiState,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = Modifier.fillMaxSize(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        PropertiesTable(
            subject = subject,
            modifier = Modifier
                .weight(2f)
                .fillMaxHeight()
                .padding(vertical = 4.dp)
        )

        Spacer(Modifier.width(16.dp))

        SubjectImageBox(
            imageBytes = uiState.imageBytes,
            isImageLoading = uiState.isImageLoading,
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
        )
    }
}

@Composable
private fun SubjectImageBox(
    imageBytes: ByteArray?,
    isImageLoading: Boolean,
    modifier: Modifier = Modifier
) {
    val overlayPainter = painterResource("images/detailScreen/SubjectBorderIcon.png")

    val bitmap: ImageBitmap? = remember(imageBytes) {
        imageBytes?.let { decodeImage(it) }
    }
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        when {
            isImageLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            bitmap != null -> {
                Image(
                    bitmap = bitmap,
                    contentDescription = null,
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier.fillMaxSize()
                )
            }

            else -> {
                Image(
                    painter = painterResource("images/detailScreen/fotoBase.png"),
                    contentDescription = null,
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
        Image(
            painter = overlayPainter,
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
                .matchParentSize()
                .graphicsLayer(
                    scaleX = 1.05f,
                    scaleY = 1.05f
                )
        )
    }
}

private fun decodeImage(bytes: ByteArray): ImageBitmap? =
    runCatching { SkiaImage.makeFromEncoded(bytes).toComposeImageBitmap() }.getOrNull()

@Composable
private fun PropertiesTable(
    subject: Subject,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.border(
            width = 1.dp,
            color = Color.Transparent
        ),
        contentAlignment = Alignment.Center){
        Image(
            painter = painterResource("images/detailScreen/dataBackground.png"),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.matchParentSize()

        )
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            PropertyRow("Código", subject.codigo, Modifier.weight(1f), autoSize = false)
            PropertyRow("Nombre", subject.nombre, Modifier.weight(1.5f), autoSize = true)
            PropertyRow("Año", subject.anio.toString(), Modifier.weight(1f), autoSize = false)
            PropertyRow("Período", subject.periodo, Modifier.weight(1f), autoSize = false)
            PropertyRow("Año de recopilación", subject.anioDeRecopilacion.toString(), Modifier.weight(1f), autoSize = false)
            PropertyRow("Notas promedio", "%.2f".format(subject.notasPromedio), Modifier.weight(1f), autoSize = false)
            PropertyRow("Inscriptos", subject.inscriptos.toString(), Modifier.weight(1f), autoSize = false)
            PropertyRow("Presencialidad", subject.presencialidad, Modifier.weight(1f), autoSize = false)
            PropertyRow("Cant. aprobados", subject.cantAprobados.toString(), Modifier.weight(1f), autoSize = false)
        }
    }
}

@Composable
private fun PropertyRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    autoSize: Boolean = false
) {
    val borderColor = Color(0xFF8C7445)
    Box(
        modifier = modifier
            .fillMaxWidth()
            .border(width = 1.dp, color = borderColor),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f).absolutePadding(3.dp),
            )
            VerticalDivider(
                modifier = Modifier.fillMaxHeight(),
                color = borderColor,
                thickness = 1.dp
            )
            if (autoSize) {
                // Solo reduce el tamaño si es estrictamente necesario (Ej: nombres kilométricos)
                AutoResizingText(
                    text = value,
                    modifier = Modifier
                        .weight(1.5f)
                        .padding(horizontal = 4.dp),
                    color = Color.Black
                )
            } else {
                Text(
                    text = value,
                    fontSize = 13.sp,
                    modifier = Modifier
                        .weight(1.5f)
                        .padding(horizontal = 4.dp),
                    color = Color.Black,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun AutoResizingText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    maxLines: Int = 1
) {
    val defaultFontSize = 14.sp
    val minFontSize = 8.sp

    var fontSize by remember(text) { mutableStateOf(defaultFontSize) }
    var readyToDraw by remember(text) { mutableStateOf(false) }

    Text(
        text = text,
        color = color,
        modifier = modifier.graphicsLayer {
            alpha = if (readyToDraw) 1f else 0f
        },
        fontSize = fontSize,
        maxLines = maxLines,
        overflow = TextOverflow.Clip,
        onTextLayout = { textLayoutResult ->

            if (textLayoutResult.hasVisualOverflow && fontSize > minFontSize) {
                fontSize = (fontSize.value - 1f).sp
            } else {
                readyToDraw = true
            }
        }
    )
}