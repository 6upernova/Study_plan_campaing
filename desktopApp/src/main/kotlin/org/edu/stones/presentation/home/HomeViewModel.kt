package org.edu.stones.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import org.edu.stones.domain.entity.Subject
import org.edu.stones.domain.usecase.GetAllSubjectsUseCase
import org.edu.stones.domain.usecase.GetSubjectDetailUseCase
import org.edu.stones.domain.usecase.PreloadSubjectImagesUseCase
import org.edu.stones.domain.usecase.PreloadSubjectLegendsUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.edu.stones.presentation.home.components.GraphLayoutData
import org.edu.stones.presentation.home.components.GraphLayoutEngine
import org.edu.stones.presentation.home.config.GraphConfig
import org.edu.stones.presentation.home.config.GraphConfigDefaults

class HomeViewModel(
    private val getAllSubjectsUseCase: GetAllSubjectsUseCase,
    private val getSubjectsUseCase: GetSubjectDetailUseCase,
    private val preloadSubjectImagesUseCase: PreloadSubjectImagesUseCase,
    private val preloadSubjectLegendsUseCase: PreloadSubjectLegendsUseCase,
    private val graphConfig: GraphConfig = GraphConfigDefaults.Default
) : ViewModel() {

    private val homeStateMutableStateFlow = MutableStateFlow(HomeUiState())

    val homeStateFlow: StateFlow<HomeUiState> = homeStateMutableStateFlow.asStateFlow()

    fun getAllSubjects() {

        updateLayoutConstraints(1200f, 800f, 1f)
    }

    fun updateLayoutConstraints(availableWidth: Float, availableHeight: Float, density: Float) {
        viewModelScope.launch {
            homeStateMutableStateFlow.emit(HomeUiState(isLoading = true))

            val graph = getAllSubjectsUseCase()

            val layoutData = GraphLayoutEngine.computeLayout(graph, availableWidth, availableHeight, density, graphConfig)

            val subjects = graph.vertexSet().toList()

            homeStateMutableStateFlow.emit(
                HomeUiState(
                    isLoading = false,
                    graphLayoutData = layoutData
                )
            )

            launch { preloadSubjectImagesUseCase(subjects) }
            launch { preloadSubjectLegendsUseCase(subjects) }
        }
    }

    private fun determatePeriod(periodo: String): Int {
        if (periodo == "Primer Cuatrimestre") {
            return 1
        } else {
            return 2
        }
    }

    suspend fun getSubjectDetail(id: String): Subject? {
        return getSubjectsUseCase(id)
    }

    data class HomeUiState(
        val isLoading: Boolean = false,
        val graphLayoutData: GraphLayoutData? = null
    )
}
