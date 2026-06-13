package org.edu.stones

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import org.edu.stones.presentation.App

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "study_plan_campaing",
    ) {
        App()
    }
}