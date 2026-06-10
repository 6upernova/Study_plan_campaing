@file:Suppress("FunctionName")

package edu.dyds.movies.presentation.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import dydsproject.composeapp.generated.resources.Res
import dydsproject.composeapp.generated.resources.app_name
import edu.dyds.movies.domain.entity.Movie
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
	viewModel: HomeViewModel,
	onGoodMovieClick: (Movie) -> Unit
) {

	// ============================================================
	// DATOS DE PRUEBA HARDCODEADOS
	// ELIMINAR CUANDO SE CONECTE AL VIEWMODEL REAL
	// ============================================================
	val graphNodes = listOf(
		"ELEMENTOS DE ALGEBRA Y DE GEOMETRIA" to 10,
		"RESOLUCION DE PROBLEMAS Y ALGORITMOS" to 10,
		"ANALISIS MATEMATICO I" to 20,
		"INTRODUCCION A LA PROGRAMACION ORIENTADA A OBJETOS" to 20,
		"LENGUAJES FORMALES Y AUTOMATAS" to 20,
		"ESTRUCTURAS DE DATOS" to 30,
		"TEORIA DE LA COMPUTABILIDAD" to 30,
		"ANALISIS MATEMATICO II" to 40,
		"Idioma de Lic. en Ciencias de la Computación, plan 2012" to 40,
		"ORGANIZACION DE COMPUTADORAS" to 40,
		"TECNOLOGIA DE PROGRAMACION" to 40,
		"ANALISIS Y DISEÑO DE SISTEMAS" to 50,
		"ARQUITECTURA DE COMPUTADORAS" to 50,
		"LOGICA PARA CIENCIAS DE LA COMPUTACION" to 50,
		"BASES DE DATOS" to 60,
		"METODOS DE COMPUTACION CIENTIFICA" to 60,
		"MODELOS ESTADISTICOS PARA CIENCIAS DE LA COMPUTACION" to 60,
		"SISTEMAS OPERATIVOS Y DISTRIBUIDOS" to 60,
		"DISEÑO Y DESARROLLO DE SOFTWARE" to 70,
		"LENGUAJES DE PROGRAMACION" to 70,
		"REDES DE COMPUTADORAS" to 70,
		"ADMINISTRACION DE PROYECTOS DE SOFTWARE" to 80,
		"COMPILADORES E INTERPRETES" to 80,
		"INTELIGENCIA ARTIFICIAL" to 80,
	)
	// ============================================================

	MaterialTheme {
		Surface {

			val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

			Scaffold(
				topBar = {
					TopAppBar(
						title = {
							Text(stringResource(Res.string.app_name))
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
					nodes = graphNodes,
					onNodeClick = { label ->

						println("Nodo seleccionado: $label")

						// Aquí luego puedes traducir el String
						// a la navegación o evento real.
					}
				)
			}
		}
	}
}

@Composable
private fun GraphView(
	padding: PaddingValues,
	nodes: List<Pair<String, Int>>,
	onNodeClick: (String) -> Unit
) {

	val groupedNodes = nodes
		.groupBy { it.second }
		.toSortedMap()

	BoxWithConstraints(
		modifier = Modifier
			.fillMaxSize()
			.padding(padding)
	) {

		val graphWidth = maxWidth
		val graphHeight = maxHeight

		val columnCount = groupedNodes.size

		if (columnCount == 0) return@BoxWithConstraints

		/*
         * Espacio horizontal asignado a cada columna.
         */
		val horizontalStep = graphWidth / columnCount

		/*
         * El botón ocupará aproximadamente el 90%
         * del ancho de la columna.
         */
		val buttonWidth = horizontalStep * 0.9f

		/*
         * Fuente adaptable al tamaño disponible.
         */
		val fontSize =
			(horizontalStep.value * 0.07f)
				.coerceIn(8f, 14f)
				.sp

		groupedNodes.entries.forEachIndexed { columnIndex, (_, group) ->

			/*
             * Centra el botón dentro de la columna.
             */
			val x =
				horizontalStep * columnIndex +
						(horizontalStep - buttonWidth) / 2

			val nodeCount = group.size

			/*
             * Distribuye verticalmente los nodos
             * usando toda la altura disponible.
             */
			val verticalStep =
				graphHeight / (nodeCount + 1)

			group.forEachIndexed { rowIndex, (label, _) ->

				val y =
					verticalStep * (rowIndex + 1)

				Button(
					onClick = {
						onNodeClick(label)
					},
					modifier = Modifier
						.width(buttonWidth)
						.offset(
							x = x,
							y = y
						)
				) {
					Text(
						text = label,
						fontSize = fontSize,
						textAlign = TextAlign.Center,
						maxLines = 4
					)
				}
			}
		}
	}
}