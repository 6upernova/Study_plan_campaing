@file:Suppress("FunctionName")

package org.edu.stones.presentation.detail

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
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
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

data class SubjectDetailUi(
    val code: String,
    val name: String,
    val year: Int,
    val period: String,
    val collectionYear: Int,
    val averageGrade: Double,
    val enrolled: Int,
    val approved: Int,
    val modality: String,
    val description: String
) {
    val approvalPercentage: Float
        get() = approved.toFloat() / enrolled.toFloat()

    val attendancePercentage: Float
        get() = if (modality.equals("Presencial", true)) 1f else 0f
}

@Composable
fun SubjectDetailScreen(
    subject: SubjectDetailUi,
    illustration: Painter,
    modifier: Modifier = Modifier
) {
    MaterialTheme {
        Surface {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {

                HeaderSection(subject.name)

                Spacer(Modifier.height(16.dp))

                InformationSection(
                    subject = subject,
                    illustration = illustration
                )

                Spacer(Modifier.height(16.dp))

                StatisticsSection(subject)

                Spacer(Modifier.height(16.dp))

                LegendSection(subject.description)
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
private fun InformationSection(
    subject: SubjectDetailUi,
    illustration: Painter
) {
    Row(
        modifier = Modifier.fillMaxWidth()
    ) {

        PropertiesTable(
            subject = subject,
            modifier = Modifier.weight(1f)
        )

        Spacer(Modifier.width(16.dp))

        IllustrationSection(
            painter = illustration,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun PropertiesTable(
    subject: SubjectDetailUi,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.border(
            width = 1.dp,
            color = Color.Gray
        )
    ) {

        PropertyRow("Código", subject.code)
        PropertyRow("Nombre", subject.name)
        PropertyRow("Año", subject.year.toString())
        PropertyRow("Período", subject.period)
        PropertyRow("Año de recopilación", subject.collectionYear.toString())
        PropertyRow("Notas promedio", "%.2f".format(subject.averageGrade))
        PropertyRow("Inscriptos", subject.enrolled.toString())
        PropertyRow("Presencialidad", subject.modality)
        PropertyRow("Cant. aprobados", subject.approved.toString())
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
private fun IllustrationSection(
    painter: Painter,
    modifier: Modifier = Modifier
) {
    Image(
        painter = painter,
        contentDescription = null,
        modifier = modifier.aspectRatio(1f)
    )
}

@Composable
private fun StatisticsSection(
    subject: SubjectDetailUi
) {
    Row(
        modifier = Modifier.fillMaxWidth()
    ) {

        ApprovalCard(
            percentage = subject.approvalPercentage,
            approved = subject.approved,
            enrolled = subject.enrolled,
            modifier = Modifier.weight(1f)
        )

        Spacer(Modifier.width(12.dp))

        AverageCard(
            average = subject.averageGrade,
            modifier = Modifier.weight(1f)
        )

        Spacer(Modifier.width(12.dp))

        AttendanceCard(
            modality = subject.modality,
            percentage = subject.attendancePercentage,
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
                modifier = Modifier.size(150.dp)
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
    description: String
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {

        Text(
            text = "LEYENDA",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(Modifier.height(8.dp))

        Text(description)
    }
}