@file:Suppress("FunctionName")

package org.edu.stones.presentation.detail

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.edu.stones.domain.entity.Subject
import org.edu.stones.presentation.AppTypography
import org.edu.stones.presentation.detail.DetailViewModel.DetailUiState
import org.edu.stones.presentation.detail.DetailViewModel
import org.edu.stones.presentation.detail.components.GeneralInfoSection
import org.edu.stones.presentation.detail.components.HeaderSection
import org.edu.stones.presentation.detail.components.LegendSection
import org.edu.stones.presentation.detail.components.RequirementsSection
import org.edu.stones.presentation.detail.components.StatisticsSection
import org.jetbrains.skia.Image as SkiaImage


@Composable
fun SubjectDetailScreen(
    uiState: DetailViewModel.DetailUiState,
    modifier: Modifier = Modifier,
    viewModel: DetailViewModel
) {

    MaterialTheme(
        typography = AppTypography
    ) {
        Box(
            modifier = modifier.fillMaxSize()
        ) {
     backGround()
            Surface(
                color = Color.Transparent,
                modifier = Modifier.fillMaxSize()
                    .padding(horizontal = 24.dp)
            ) {
                when {
                    uiState.isLoading -> {
                        showText("Cargando Materia...",modifier)
                    }

                    uiState.subject != null -> {
                        val subject = uiState.subject

                        Column(
                            modifier = modifier
                                .fillMaxSize()
                                .padding(16.dp)
                        ) {

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(0.12f),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                HeaderSection(subject.nombre,modifier)
                            }
                            Spacer(Modifier.height(8.dp))

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(0.30f)
                            ) {
                                GeneralInfoSection(subject, uiState,modifier)
                            }

                            Spacer(Modifier.height(8.dp))

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(0.18f)
                            ) {
                                RequirementsSection(subject, viewModel, uiState,modifier)
                            }
                            Spacer(Modifier.height(8.dp))

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(0.20f)
                            ) {
                                StatisticsSection(subject)
                            }

                            Spacer(Modifier.height(8.dp))

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(0.17f)
                            ) {
                                LegendSection(uiState.legend, uiState.isLegendLoading)
                            }
                        }
                    }

                    else -> {
                        if (uiState.isLoading == false && uiState.subject == null)
                            showText("Terminó de cargar y no hay datos")
                        else
                            showText("Cargando...")
                    }
                }
            }
        }
    }
}

@Composable
private fun showText(
    text: String,
    modifier: Modifier = Modifier
){
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            fontSize = 24.sp
        )
    }
}

@Composable
private fun backGround(){
    Image(
        painter = painterResource("images/detailScreen/paperBackground.png"),
        contentDescription = null,
        contentScale = ContentScale.FillBounds,
        modifier = Modifier.fillMaxSize()
    )
}