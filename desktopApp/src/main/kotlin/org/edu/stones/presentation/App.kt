@file:Suppress("FunctionName")

package org.edu.stones.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.tooling.preview.Preview
import org.edu.stones.presentation.home.HomeScreen
import org.edu.stones.di.MoviesDependencyInjector.getHomeViewModel

import org.edu.stones.data.external.broker.SubjectsBroker
import org.edu.stones.data.external.SubjectDetailExternalSource
import org.edu.stones.data.external.dto.GoogleScriptSubjectExternalSource
import org.edu.stones.domain.entity.Subject

@Composable
@Preview
fun App() {
	LaunchedEffect(Unit) {
		val google = GoogleScriptSubjectExternalSource()
		val broker = SubjectsBroker(listOf(google))

		val lista = google.getSubjectsByCareer("ISI")
		println(lista)
	}
}

@Composable
private fun startApp() {
	HomeScreen(
		viewModel = getHomeViewModel()
	)
}
