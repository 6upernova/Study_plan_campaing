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

	//var graphNodes by remember { mutableStateOf<List<Subject>>(emptyList()) }


	LaunchedEffect(Unit) {
		viewModel.getAllSubjects()
	}
	// ============================================================
	// DATOS DE PRUEBA HARDCODEADOS
	// ELIMINAR CUANDO SE CONECTE AL VIEWMODEL REAL
	// ============================================================
	val graphNodes3 = listOf(
		"ELEMENTOS DE ALGEBRA Y DE GEOMETRIA" to 1,
		"RESOLUCION DE PROBLEMAS Y ALGORITMOS" to 1,
		"ANALISIS MATEMATICO I" to 2,
		"INTRODUCCION A LA PROGRAMACION ORIENTADA A OBJETOS" to 2,
		"LENGUAJES FORMALES Y AUTOMATAS" to 2,
		"ESTRUCTURAS DE DATOS" to 3,
		"TEORIA DE LA COMPUTABILIDAD" to 3,
		"ANALISIS MATEMATICO II" to 4,
		"Idioma de Lic. en Ciencias de la Computación, plan 2012" to 4,
		"ORGANIZACION DE COMPUTADORAS" to 4,
		"TECNOLOGIA DE PROGRAMACION" to 4,
		"ANALISIS Y DISEÑO DE SISTEMAS" to 5,
		"ARQUITECTURA DE COMPUTADORAS" to 5,
		"LOGICA PARA CIENCIAS DE LA COMPUTACION" to 5,
		"BASES DE DATOS" to 6,
		"METODOS DE COMPUTACION CIENTIFICA" to 6,
		"MODELOS ESTADISTICOS PARA CIENCIAS DE LA COMPUTACION" to 6,
		"SISTEMAS OPERATIVOS Y DISTRIBUIDOS" to 6,
		"DISEÑO Y DESARROLLO DE SOFTWARE" to 7,
		"LENGUAJES DE PROGRAMACION" to 7,
		"REDES DE COMPUTADORAS" to 7,
		"ADMINISTRACION DE PROYECTOS DE SOFTWARE" to 8,
		"COMPILADORES E INTERPRETES" to 8,
		"INTELIGENCIA ARTIFICIAL" to 8,
		"ALGORITMOS Y COMPLEJIDAD" to 9,
		"INGENIERIA DE APLICACIONES DE WEB" to 9,
		"Optativa de Lic. en Ciencias de la Computación, plan 2012" to 9,
		"Optativa de Lic. en Ciencias de la Computación, plan 2012" to 10,
		"TESIS DE LICENCIATURA" to 10,
	)
	// ============================================================
	val openedWindows = remember { mutableStateOf<List<String>>(emptyList()) }
	if(uiState.isLoading){
		CircularProgressIndicator()
	}else {
		val listNodes = uiState.subjectsList

		MaterialTheme {
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
						onNodeClick = { label ->

							println("Nodo seleccionado: $label")

							// Aquí luego puedes traducir el String
							// a la navegación o evento real.
							openedWindows.value = openedWindows.value + label
						}
					)
				}
				openedWindows.value.forEachIndexed { index, subjectName ->
					SubjectDetailWindow(
						subjectName = subjectName,
						onClose = {
							openedWindows.value = openedWindows.value.filterIndexed { i, _ -> i != index }
						},
						viewModel
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

			group.forEachIndexed { rowIndex, (buttonText, _, _) ->

				val y =
					verticalStep * (rowIndex + 1)

				Button(
					onClick = {
						onNodeClick(buttonText)
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
	subjectName: String,
	onClose: () -> Unit,
	viewModel: HomeViewModel
) {
	Window(
		onCloseRequest = onClose,
		title = subjectName,
		state = rememberWindowState(width = 900.dp, height = 700.dp)
	) {
		MaterialTheme {
			Surface {
				// Crear datos de prueba para la pantalla de detalle de materia
				val subjectDetail = Subject(
					codigo = "CS-101",
					nombre = subjectName,
					anio = 1,
					periodo = "1Q",
					anioDeRecopilacion = 2024,
					notasPromedio = 7.5,
					inscriptos = 120,
					presencialidad = "Si",
					cantAprobados = 10,
					correlativasCursadas = "AM1",
					correlativasAprobadas = "",
					abreviatura = "asdfasdf"
				)

				val detailViewModel = SubjectDependencyInjector.getDetailViewModel()

				LaunchedEffect(subjectName) {
					detailViewModel.getSubject("5793")
				}


				val uiState by detailViewModel.detailStateFlow.collectAsState(
					initial = DetailViewModel.DetailUiState()
				)
				// Crear un painter placeholder vacío
				SubjectDetailScreen(
					uiState = uiState,
					modifier = Modifier
				)
			}
		}
	}
}