package org.edu.stones

import androidx.compose.ui.res.painterResource
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import org.edu.stones.presentation.App

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "study_plan_campaing",
        state = WindowState(placement = WindowPlacement.Maximized),
        icon = painterResource("images/icon.png")
    ) {
        App()
    }
}
