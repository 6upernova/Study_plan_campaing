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
import androidx.compose.runtime.remember
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
    uiState: DetailViewModel.DetailUiState
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp),
        verticalAlignment = Alignment.Top
    ) {
        PropertiesTable(
            subject = subject,
            modifier = Modifier.weight(2f).padding(horizontal = 8.dp)
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
                    modifier = modifier,
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
                    modifier = modifier
                )
            }

            else -> {
                Image(
                    painter = painterResource("images/detailScreen/fotoBase.png"),
                    contentDescription = null,
                    contentScale = ContentScale.FillBounds,
                    modifier = modifier
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
            modifier = Modifier
                .matchParentSize()
        )
        Column(
            modifier = modifier.fillMaxSize()
        ) {
            PropertyRow("Código", subject.codigo)
            PropertyRow("Nombre", subject.nombre)
            PropertyRow("Año", subject.anio.toString())
            PropertyRow("Período", subject.periodo)
            PropertyRow("Año de recopilación", subject.anioDeRecopilacion.toString())
            PropertyRow("Notas promedio", "%.2f".format(subject.notasPromedio))
            PropertyRow("Inscriptos", subject.inscriptos.toString())
            PropertyRow("Presencialidad", subject.presencialidad)
            PropertyRow("Cant. aprobados", subject.cantAprobados.toString())
        }
    }
}

@Composable
private fun PropertyRow(
    label: String,
    value: String
) {
    val borderColor = Color(0xFF8C7445)
    Box(
        Modifier.border(
            width = 1.dp,
            color = borderColor
        ))
    {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
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
            Text(
                text = value,
                modifier = Modifier.weight(1f).absolutePadding(3.dp)
            )
        }
    }
}