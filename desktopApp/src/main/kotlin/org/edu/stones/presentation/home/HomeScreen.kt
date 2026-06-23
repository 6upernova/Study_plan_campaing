@file:Suppress("FunctionName")

package org.edu.stones.presentation.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.rememberWindowState
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.edu.stones.di.SubjectDependencyInjector
import org.edu.stones.presentation.AppTypography
import org.edu.stones.presentation.detail.DetailViewModel
import org.edu.stones.presentation.home.components.DirectedGraphCanvas
import org.edu.stones.presentation.home.components.GraphBackground
import org.edu.stones.presentation.detail.SubjectDetailScreen
import org.edu.stones.presentation.home.config.GraphConfig
import org.edu.stones.presentation.home.config.GraphConfigDefaults
import java.awt.Dimension

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    careerName: String = "Lic Ciencias de la Computación",
    config: GraphConfig = GraphConfigDefaults.Default
) {
    val uiState by viewModel.homeStateFlow.collectAsStateWithLifecycle()

    MaterialTheme(
        typography = AppTypography
    ) {
        val openedWindows = remember { mutableStateOf<List<String>>(emptyList()) }

        LaunchedEffect(Unit) {
            viewModel.getAllSubjects()
        }

        if (uiState.isLoading) {
            CircularProgressIndicator()
        } else {
            val layoutData = uiState.graphLayoutData

            Surface {
                BoxWithConstraints {
                    val density = LocalDensity.current.density
                    val headerHeightDp = (maxWidth.value * config.headerHeightPercent)
                        .coerceIn(config.headerHeightMinDp, config.headerHeightMaxDp)
                    val availableWidthPx = with(LocalDensity.current) { maxWidth.toPx() }
                    val availableHeightPx = with(LocalDensity.current) { (maxHeight.value - headerHeightDp).dp.toPx() }

                    Box(modifier = Modifier.fillMaxSize()) {
                        GraphBackground(alpha = config.backgroundAlpha, config = config)

                        Column(modifier = Modifier.fillMaxSize()) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(headerHeightDp.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                TitleHeader(
                                    careerName = careerName,
                                    heightDp = headerHeightDp,
                                    config = config
                                )
                            }

                            if (layoutData != null) {
                                LaunchedEffect(availableWidthPx, availableHeightPx, density) {
                                    viewModel.updateLayoutConstraints(
                                        availableWidthPx,
                                        availableHeightPx,
                                        density
                                    )
                                }

                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(top = (headerHeightDp * config.headerTopPaddingFactor).dp)
                                ) {
                                    DirectedGraphCanvas(
                                        modifier = Modifier.fillMaxSize(),
                                        nodes = layoutData.nodes,
                                        edges = layoutData.edges,
                                        onNodeClick = { id ->
                                            openedWindows.value += id
                                        },
                                        config = config,


                                    )
                                }
                            }
                        }
                    }

                    openedWindows.value.forEachIndexed { index, selectedSubject ->
                        SubjectDetailWindow(
                            subjectCode = selectedSubject,
                            onClose = {
                                openedWindows.value = openedWindows.value.filterIndexed { i, _ -> i != index }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TitleHeader(
    careerName: String,
    heightDp: Float,
    config: GraphConfig
) {
    val signWidthDp = (heightDp * config.signAspectRatio)
        .coerceIn(config.signWidthMinDp, config.signWidthMaxDp)

    Box(
        modifier = Modifier
            .size(signWidthDp.dp, heightDp.dp)
    ) {
        Image(
            painter = painterResource("images/homeScreen/TitleSign.png"),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.matchParentSize()
        )
        Text(
            text = careerName,
            color = config.colorTitleText,
            fontSize = (heightDp * config.titleFontSizeFactor).sp,
            fontWeight = FontWeight.Bold,
            fontFamily = AppTypography.displayLarge.fontFamily,
            letterSpacing = config.titleLetterSpacing.sp,
            textAlign = TextAlign.Start,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 20.dp, top = 60.dp)
        )
    }
}

@Composable
fun SubjectDetailWindow(
    subjectCode: String,
    onClose: () -> Unit,
    config: GraphConfig = GraphConfigDefaults.Default
) {

    Window(
        onCloseRequest = onClose,
        title = subjectCode,
        state = rememberWindowState(width = config.windowWidthDp.dp, height = config.windowHeightDp.dp)
    ) {
        LaunchedEffect(window) {
            window.minimumSize = Dimension(config.windowWidthDp, config.windowHeightDp)
        }
        MaterialTheme {
            Surface {
                val detailViewModel = SubjectDependencyInjector.getDetailViewModel(subjectCode)

                LaunchedEffect(subjectCode) {
                    detailViewModel.getSubject(subjectCode)
                }

                val uiState by detailViewModel.detailStateFlow.collectAsState(
                    initial = DetailViewModel.DetailUiState()
                )

                SubjectDetailScreen(
                    uiState = uiState,
                    modifier = Modifier,
                    viewModel = detailViewModel
                )
            }
        }
    }
}
