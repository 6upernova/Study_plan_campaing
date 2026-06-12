@file:Suppress("FunctionName")

package edu.dyds.movies.presentation.home

import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.sp
import edu.dyds.movies.presentation.home.components.GraphBackground

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
) {

	// ============================================================
	// DATOS DE PRUEBA HARDCODEADOS
	// ELIMINAR CUANDO SE CONECTE AL VIEWMODEL REAL
	// ============================================================
	val graphNodes = listOf(
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