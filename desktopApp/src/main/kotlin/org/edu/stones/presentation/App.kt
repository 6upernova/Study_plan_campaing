@file:Suppress("FunctionName")

package org.edu.stones.presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import org.edu.stones.presentation.home.HomeScreen
import org.edu.stones.di.MoviesDependencyInjector.getHomeViewModel

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