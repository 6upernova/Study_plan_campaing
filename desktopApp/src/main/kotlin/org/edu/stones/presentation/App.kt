@file:Suppress("FunctionName")

package org.edu.stones.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.tooling.preview.Preview
import org.edu.stones.presentation.home.HomeScreen
import org.edu.stones.di.SubjectDependencyInjector.getHomeViewModel

import org.edu.stones.data.external.broker.SubjectsBroker
import org.edu.stones.data.external.dto.GoogleScriptSubjectExternalSource

@Composable
@Preview
fun App() {

	startApp()
}

@Composable
private fun startApp() {
	HomeScreen(
		viewModel = getHomeViewModel()
	)
}
