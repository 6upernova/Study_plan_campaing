@file:Suppress("FunctionName")

package org.edu.stones.presentation.home

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Button

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.rememberWindowState
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.edu.stones.di.SubjectDependencyInjector

import org.edu.stones.domain.entity.Subject
import org.edu.stones.domain.usecase.GetSubjectDetailUseCase
import org.edu.stones.presentation.detail.DetailViewModel
import org.edu.stones.presentation.home.components.GraphBackground
import org.edu.stones.presentation.detail.SubjectDetailScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
	viewModel: HomeViewModel
) {
	val uiState by viewModel.homeStateFlow.collectAsStateWithLifecycle()

	LaunchedEffect(Unit) {
		viewModel.getAllSubjects()
	}

		MaterialTheme {
			val openedWindows = remember { mutableStateOf<List<String>>(emptyList()) }
			if(uiState.isLoading){
				CircularProgressIndicator()
			}else {
				val listNodes = uiState.subjectsList

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

						GraphView(
							padding = padding,
							nodes = listNodes,
							onNodeClick = { id ->
								openedWindows.value += id
							}
						)
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
private fun GraphView(
	padding: PaddingValues,
	nodes: List<Triple<String, String,Int>>,
	onNodeClick: (String) -> Unit
) {

	val groupedNodes = nodes
		.groupBy { it.third }
		.toSortedMap()

	BoxWithConstraints(
		modifier = Modifier
			.fillMaxSize()
			.padding(padding)
	) {

		GraphBackground()

		val graphWidth = maxWidth
		val graphHeight = maxHeight

		val columnCount = groupedNodes.size

		if (columnCount == 0) return@BoxWithConstraints

		val horizontalStep = graphWidth / columnCount
		val buttonWidth = horizontalStep * 0.9f

		val fontSize =
			(horizontalStep.value * 0.07f)
				.coerceIn(8f, 14f)
				.sp

		groupedNodes.entries.forEachIndexed { columnIndex, (_, group) ->

			val x =
				horizontalStep * columnIndex +
						(horizontalStep - buttonWidth) / 2

			val nodeCount = group.size

			val verticalStep =
				graphHeight / (nodeCount + 1)

			group.forEachIndexed { rowIndex, (buttonText, subjectCode, _) ->

				val y =
					verticalStep * (rowIndex + 1)

				Button(
					onClick = {
						onNodeClick(subjectCode)
					},
					modifier = Modifier
						.width(buttonWidth)
						.offset(
							x = x,
							y = y
						)
				) {
					Text(
						text = buttonText,
						fontSize = fontSize,
						textAlign = TextAlign.Center,
						maxLines = 4
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
		state = rememberWindowState(width = 900.dp, height = 700.dp)
	) {
		MaterialTheme {
			Surface {
				val detailViewModel = SubjectDependencyInjector.getDetailViewModel()

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