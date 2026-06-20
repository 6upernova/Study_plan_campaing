@file:Suppress("FunctionName")

package org.edu.stones.presentation.detail

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*

import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.edu.stones.domain.entity.Subject

import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.runtime.remember
import org.jetbrains.skia.Image as SkiaImage


@Composable
fun SubjectDetailScreen(
    uiState: DetailViewModel.DetailUiState,
    modifier: Modifier = Modifier
) {
    MaterialTheme {
        Surface {
            when {
                uiState.isLoading -> {
                    Box(
                        modifier = modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Cargando materia..."
                        )
                    }
                }

                uiState.subject != null -> {
                    val subject = uiState.subject

                    Column(
                        modifier = modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {

                        HeaderSection(subject.nombre)

                        Spacer(Modifier.height(8.dp))

                        Box(
                            modifier = Modifier.weight(1f)
                        ) {
                            GeneralInfoSection(subject, uiState)
                        }

                        Spacer(Modifier.height(8.dp))

                        Box(
                            modifier = Modifier.weight(1f)
                        ) {
                            StatisticsSection(subject)
                        }

                        Spacer(Modifier.height(8.dp))

                        LegendSection(subject.abreviatura)
                    }
                }
                else -> {
                    if(uiState.isLoading == false && uiState.subject == null)
                        Text("Terminó de cargar y no hay datos")
                    else
                        Text("Cargando...")
                }
            }
        }
    }
}

@Composable
private fun HeaderSection(
    title: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall
        )
    }
}

@Composable
private fun GeneralInfoSection(
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
            modifier = Modifier.weight(2f)
        )

        Spacer(Modifier.width(16.dp))

        SubjectImageBox(
            imageBytes = uiState.imageBytes,
            isImageLoading = uiState.isImageLoading,
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .border(1.dp, Color.Gray)
        )
    }
}

@Composable
private fun SubjectImageBox(
    imageBytes: ByteArray?,
    isImageLoading: Boolean,
    modifier: Modifier = Modifier
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
        imageBytes != null -> {
            val bitmap: ImageBitmap = remember(imageBytes) {
                SkiaImage.makeFromEncoded(imageBytes).toComposeImageBitmap()
            }
            Image(
                bitmap = bitmap,
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = modifier
            )
        }
        else -> {
            Image(
                painter = painterResource("images/fotoBase.png"),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = modifier
            )
        }
    }
}

@Composable
private fun PropertiesTable(
    subject: Subject,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.border(
            width = 1.dp,
            color = Color.Gray
        )
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

@Composable
private fun PropertyRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {

        Text(
            text = label,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f)
        )

        Text(
            text = value,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun StatisticsSection(
    subject: Subject
) {
    val approvalPercentage = if (subject.inscriptos > 0) {
        subject.cantAprobados.toFloat() / subject.inscriptos.toFloat()
    } else {
        0f
    }

    Row(
        modifier = Modifier.fillMaxWidth()
    ) {

        ApprovalCard(
            percentage = approvalPercentage,
            approved = subject.cantAprobados,
            enrolled = subject.inscriptos,
            modifier = Modifier.weight(1f)
        )

        Spacer(Modifier.width(12.dp))

        AverageCard(
            average = subject.notasPromedio,
            modifier = Modifier.weight(1f)
        )

        Spacer(Modifier.width(12.dp))

        AttendanceCard(
            modality = subject.presencialidad,
            percentage = if (subject.presencialidad.equals("Presencial", true)) 1f else 0f,
            modifier = Modifier.weight(1f)
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
    Card(modifier) {

        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text("% DE APROBACIÓN")

            Spacer(Modifier.height(12.dp))

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(100.dp)
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

                Text("${(percentage * 100).toInt()}%")
            }

            Spacer(Modifier.height(8.dp))

            Text("$approved / $enrolled")
        }
    }
}

@Composable
private fun AverageCard(
    average: Double,
    modifier: Modifier = Modifier
) {
    Card(modifier) {

        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text("PROMEDIO DE NOTAS")

            Spacer(Modifier.height(12.dp))

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(
                    width = 180.dp,
                    height = 110.dp
                )
            ) {

                Canvas(
                    modifier = Modifier.fillMaxSize()
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

                Text("%.2f".format(average))
            }

            Row(
                modifier = Modifier.fillMaxWidth()
            ) {

                Text("0")

                Spacer(Modifier.weight(1f))

                Text("10")
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
    Card(modifier) {

        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text("PRESENCIALIDAD")

            Spacer(Modifier.height(16.dp))

            Icon(
                imageVector = Icons.Filled.Person,
                contentDescription = null,
                modifier = Modifier.size(48.dp)
            )

            Spacer(Modifier.height(8.dp))

            Text(modality)

            Spacer(Modifier.height(8.dp))

            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(16.dp)
            ) {

                drawLine(
                    color = Color.LightGray,
                    start = Offset(0f, size.height / 2),
                    end = Offset(size.width, size.height / 2),
                    strokeWidth = 8f
                )

                drawLine(
                    color = Color(0xFF795548),
                    start = Offset(0f, size.height / 2),
                    end = Offset(size.width * percentage, size.height / 2),
                    strokeWidth = 8f
                )
            }

            Spacer(Modifier.height(8.dp))

            Text("${(percentage * 100).toInt()}%")
        }
    }
}

@Composable
private fun LegendSection(
    abbreviation: String
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {

        Text(
            text = "LEYENDA",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(Modifier.height(8.dp))

        Text(abbreviation)
    }
}