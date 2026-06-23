@file:Suppress("FunctionName")

package org.edu.stones.presentation.detail.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
fun LegendSection(
    legend: String?,
    isLegendLoading: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = Modifier.fillMaxSize()
            .border(
                width = 1.dp,
                color = Color.Transparent
            ),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource("images/detailScreen/dataBackground.png"),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.matchParentSize()
        )
        Column(
            modifier = Modifier
                .fillMaxSize().padding(top = 3.dp, bottom = 0.dp, start = 3.dp, end = 3.dp)
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "LEYENDA",
                style = MaterialTheme.typography.titleMedium.copy(color = Color.Black)
            )

            Spacer(Modifier.weight(0.2f))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(2f),
                contentAlignment = Alignment.TopCenter
            ) {
                when {
                    isLegendLoading -> CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                    legend != null -> Text(
                        text = legend,
                        style = MaterialTheme.typography.bodyMedium.copy(color = Color.Black),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                        softWrap = true,
                        overflow = TextOverflow.Visible
                    )
                    else -> Text(
                        text = "No hay leyendas encontradas, puede que ningún aventurero haya vuelto...",
                        style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}
