package org.edu.stones

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "study_plan_campaing",
    ) {
        App()
    }
}