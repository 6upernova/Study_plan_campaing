@file:Suppress("FunctionName")

package org.edu.stones.presentation.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import java.awt.Dimension

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel
) {
    val uiState by viewModel.homeStateFlow.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.getAllSubjects()
    }

    MaterialTheme(
        typography = AppTypography
    ) {
        val openedWindows = remember { mutableStateOf<List<String>>(emptyList()) }
        if (uiState.isLoading) {
            CircularProgressIndicator()
        } else {
            val layoutData = uiState.graphLayoutData

            Surface {
                val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = {
                                Text("Lic Ciencias de la Computacion")
                            },
                            scrollBehavior = scrollBehavior
                        )
                    },
                    modifier = Modifier.nestedScroll(
                        scrollBehavior.nestedScrollConnection
                    )
                ) { padding ->
                    if (layoutData != null) {
                        androidx.compose.foundation.layout.Box(modifier = Modifier.fillMaxSize()) {
                            GraphBackground(alpha = 0.7f)

                            DirectedGraphCanvas(
                                modifier = Modifier.fillMaxSize(),
                                paddingValues = padding,
                                nodes = layoutData.nodes,
                                edges = layoutData.edges,
                                onNodeClick = { id ->
                                    openedWindows.value += id
                                }
                            )
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



@Composable
fun SubjectDetailWindow(
    subjectCode: String,
    onClose: () -> Unit,
) {

    Window(
        onCloseRequest = onClose,
        title = subjectCode,
        state = rememberWindowState(width = 900.dp,height = 850.dp)
    ) {
        LaunchedEffect(window) {
            window.minimumSize = Dimension(900, 850)
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
                    modifier = Modifier
                )
            }
        }
    }
}
