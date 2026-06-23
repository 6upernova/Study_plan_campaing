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
fun RequirementsSection(
    subject: Subject,
    viewModel: DetailViewModel,
    uiState: DetailUiState,
    modifier: Modifier = Modifier
) {

    LaunchedEffect(Unit){
        viewModel.getAprovNames(subject.correlativasAprobadas)
        viewModel.getCourseNames(subject.correlativasCursadas)
    }

    Box(
        modifier = modifier.fillMaxSize().border(
            width = 1.dp,
            color = Color.Transparent
        ),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource("images/detailScreen/dataBackground.png"),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
                .matchParentSize()
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Text(
                text = "Requisitos para cursar/aprobar:",
                style = MaterialTheme.typography.titleLarge.copy(color = Color.Black)
            )

            Spacer(Modifier.weight(0.3f))

            Row(
                modifier = Modifier.fillMaxWidth().weight(2f),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Text(
                    text = "Cursadas: ${loadingCourseNames(uiState)}",
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .padding(end = 8.dp),
                    style = MaterialTheme.typography.bodyLarge.copy(color = Color.Black),
                    textAlign = TextAlign.Start,
                    softWrap = true,
                    overflow = TextOverflow.Visible
                )

                Text(
                    text = "Aprobadas: ${loadingAproveNames(uiState)}",
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .padding(start = 8.dp),
                    style = MaterialTheme.typography.bodyLarge.copy(color = Color.Black),
                    textAlign = TextAlign.Start,
                    softWrap = true,
                    overflow = TextOverflow.Visible
                )
            }
        }
    }
}

private fun loadingCourseNames(
    uiState: DetailUiState
): String?
{
    val finalText : String?
    if (uiState.loadingcourseNames)
        finalText = "Buscando..."
    else
        finalText = uiState.courseNames

    return  finalText
}

private fun loadingAproveNames(
    uiState: DetailUiState
): String?
{
    val finalText : String?
    if (uiState.loadingAprovNames)
        finalText = "Buscando..."
    else
        finalText = uiState.aprovNames

    return  finalText
}