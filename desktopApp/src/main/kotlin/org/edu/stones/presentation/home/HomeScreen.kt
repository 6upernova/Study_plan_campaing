@file:Suppress("FunctionName")

package org.edu.stones.presentation.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
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
import androidx.compose.runtime.setValue
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.rememberWindowState
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.edu.stones.di.SubjectDependencyInjector
import org.edu.stones.domain.entity.Subject
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
                    val headerWidthDp = maxWidth.value
                    val availableWidthPx = with(LocalDensity.current) { maxWidth.toPx() }
                    val availableHeightPx = with(LocalDensity.current) { (maxHeight.value - headerHeightDp).dp.toPx() }

                    Box(modifier = Modifier.fillMaxSize()) {
                        GraphBackground(alpha = config.backgroundAlpha, config = config)

                        Column(modifier = Modifier.fillMaxSize()) {
                            TitleHeader(
                                careerName = careerName,
                                heightDp = headerHeightDp,
                                headerWidthDp = headerWidthDp,
                                config = config
                            )

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
                                        config = config
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
    headerWidthDp: Float,
    config: GraphConfig
) {
    // Sign dimensions
    val signWidthDp = (heightDp * config.signAspectRatio)
        .coerceIn(config.signWidthMinDp, config.signWidthMaxDp)
    
    // Calculate sign position within the header area
    val headerWidth = headerWidthDp
    val signX = (headerWidth - signWidthDp) * config.signHorizontalAlign + config.signHorizontalOffset * signWidthDp
    val signY = config.signVerticalOffset * heightDp // vertical offset within header

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(heightDp.dp)
            .padding(start = signX.dp, top = signY.dp)
    ) {
        // Sign image
        Image(
            painter = painterResource("images/TitleSign.png"),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .size(signWidthDp.dp, heightDp.dp)
                .align(Alignment.TopStart)
        )
        // Title text positioned relative to sign
        // Use relative coordinates (0.0-1.0) within the sign for text alignment
        val textX = signWidthDp * config.titleTextAlign + config.titleTextOffsetX * signWidthDp
        val textY = heightDp * config.titleTextVerticalAlign + config.titleTextOffsetY * heightDp
        
        Text(
            text = careerName,
            color = config.colorTitleText,
            fontSize = (heightDp * config.titleFontSizeFactor).sp,
            fontWeight = FontWeight.Bold,
            fontFamily = AppTypography.displayLarge.fontFamily,
            letterSpacing = config.titleLetterSpacing.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .size(signWidthDp.dp, heightDp.dp)
                .padding(start = textX.dp, top = textY.dp)
                .align(Alignment.TopStart)
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