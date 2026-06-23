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
fun StatisticsSection(
    subject: Subject
) {

    Row(
        modifier = Modifier.fillMaxSize()
    ) {
        ApprovalCard(
            percentage = approvalPercentage(subject),
            approved = subject.cantAprobados,
            enrolled = subject.inscriptos,
            modifier = Modifier.weight(1f).fillMaxHeight()
        )

        Spacer(Modifier.width(12.dp))

        AverageCard(
            average = subject.notasPromedio,
            modifier = Modifier.weight(1f).fillMaxHeight()
        )

        Spacer(Modifier.width(12.dp))

        AttendanceCard(
            modality = subject.presencialidad,
            percentage = if (subject.presencialidad.equals("Presencial", true)) 1f else 0f,
            modifier = Modifier.weight(1f).fillMaxHeight()
        )
    }
}

@Composable
private fun ApprovalCard(
    percentage: Float,
    approved: Int,
    enrolled: Int,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.border(
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
        Card(
            modifier = Modifier.fillMaxSize(),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "% DE APROBACIÓN",
                    style = MaterialTheme.typography.bodyMedium.copy(color = Color.Black)
                )

                Spacer(Modifier.weight(0.5f))

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.weight(2f).aspectRatio(1f)
                ) {
                    Canvas(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        drawArc(
                            color = Color.LightGray,
                            startAngle = 0f,
                            sweepAngle = 360f,
                            useCenter = false,
                            style = Stroke(
                                width = 28f,
                                cap = StrokeCap.Round
                            )
                        )
                        drawArc(
                            color = Color(0xFF4CAF50),
                            startAngle = -90f,
                            sweepAngle = percentage * 360f,
                            useCenter = false,
                            style = Stroke(
                                width = 28f,
                                cap = StrokeCap.Round
                            )
                        )
                    }
                    Text(
                        text = "${(percentage * 100).toInt()}%",
                        style = MaterialTheme.typography.bodyMedium.copy(color = Color.Black)
                    )
                }
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "$approved / $enrolled",
                    style = MaterialTheme.typography.bodyMedium.copy(color = Color.Black)
                )
            }
        }
    }
}

@Composable
private fun AverageCard(
    average: Double,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.border(
            width = 3.dp,
            color = Color.Transparent
        ),contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource("images/detailScreen/dataBackground.png"),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
                .matchParentSize()
        )
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "PROMEDIO DE NOTAS",
                    style = MaterialTheme.typography.bodyMedium.copy(color = Color.Black)
                )

                Spacer(Modifier.weight(0.5f))

                Box(
                    modifier = Modifier.weight(2f).aspectRatio(1.8f)
                ) {
                    Canvas(
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        drawArc(
                            color = Color.LightGray,
                            startAngle = 180f,
                            sweepAngle = 180f,
                            useCenter = false,
                            style = Stroke(width = 24f)
                        )

                        drawArc(
                            color = Color(0xFF1976D2),
                            startAngle = 180f,
                            sweepAngle = ((average / 10f) * 180f).toFloat(),
                            useCenter = false,
                            style = Stroke(width = 24f)
                        )
                    }

                    Text(
                        text = "%.2f".format(average),
                        modifier = Modifier.align(Alignment.Center),
                        style = MaterialTheme.typography.bodyMedium.copy(color = Color.Black)
                    )

                    Text(
                        text = "0",
                        modifier = Modifier
                            .align(Alignment.BottomStart).offset(y = (-30).dp),
                        style = MaterialTheme.typography.bodyMedium.copy(color = Color.Black)
                    )

                    Text(
                        text = "10",
                        modifier = Modifier.align(Alignment.BottomEnd).offset(y = (-30).dp),
                        style = MaterialTheme.typography.bodyMedium.copy(color = Color.Black)
                    )
                }
            }
        }
    }
}

@Composable
private fun AttendanceCard(
    modality: String,
    percentage: Float,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.border(
            width = 1.dp,
            color = Color.Transparent
        ), contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource("images/detailScreen/dataBackground.png"),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
                .matchParentSize()
        )
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "PRESENCIALIDAD",
                    style = MaterialTheme.typography.bodyMedium.copy(color = Color.Black)
                )

                Spacer(Modifier.weight(0.3f))

                Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = null,
                    modifier = Modifier.weight(1f).aspectRatio(1f),
                    tint = Color.White
                )

                Spacer(Modifier.weight(0.2f))

                Text(
                    text = modality,
                    style = MaterialTheme.typography.bodyMedium.copy(color = Color.Black)
                )

                Spacer(Modifier.weight(0.2f))

                Canvas(
                    modifier = Modifier.fillMaxWidth().height(8.dp)
                ) {
                    drawLine(
                        color = Color.LightGray,
                        start = Offset(0f, size.height / 2),
                        end = Offset(size.width, size.height / 2),
                        strokeWidth = size.height
                    )
                    drawLine(
                        color = Color(0xFF795548),
                        start = Offset(0f, size.height / 2),
                        end = Offset(size.width * percentage, size.height / 2),
                        strokeWidth = size.height
                    )
                }

                Spacer(Modifier.weight(0.2f))

                Text(
                    text = "${(percentage * 100).toInt()}%",
                    style = MaterialTheme.typography.bodyMedium.copy(color = Color.Black)
                )
            }
        }
    }
}

private fun approvalPercentage(
    subject: Subject
): Float {
    val approvalPercentage = if (subject.inscriptos > 0) {
    subject.cantAprobados.toFloat() / subject.inscriptos.toFloat()
    } else {
        0f
    }
    return approvalPercentage
}