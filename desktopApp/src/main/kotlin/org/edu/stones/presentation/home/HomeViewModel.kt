package org.edu.stones.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import org.edu.stones.domain.entity.Subject
import org.edu.stones.domain.usecase.GetAllSubjectsUseCase
import org.edu.stones.domain.usecase.GetSubjectDetailUseCase

import org.edu.stones.domain.usecase.PreloadSubjectImagesUseCase
import kotlinx.coroutines.flow.Flow

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.edu.stones.presentation.home.components.GraphLayoutData
import org.edu.stones.presentation.home.components.GraphLayoutEngine

class HomeViewModel(
    private val getAllSubjectsUseCase: GetAllSubjectsUseCase,
    private val getSubjectsUseCase: GetSubjectDetailUseCase,
    private val preloadSubjectImagesUseCase: PreloadSubjectImagesUseCase,
) : ViewModel() {

    private val homeStateMutableStateFlow = MutableStateFlow(HomeUiState())

    val homeStateFlow: StateFlow<HomeUiState> = homeStateMutableStateFlow.asStateFlow()

    fun getAllSubjects() {
        viewModelScope.launch {
            homeStateMutableStateFlow.emit(HomeUiState(isLoading = true))

            val graph = getAllSubjectsUseCase()

            val layoutData = GraphLayoutEngine.computeLayout(graph)

            val subjects = graph.vertexSet().toList()
            val subjectsList = subjects.map { subject ->
                Triple(subject.abreviatura, subject.codigo, (subject.anio - 1) * 2 + determatePeriod(subject.periodo))
            }


            homeStateMutableStateFlow.emit(
                HomeUiState(
                    isLoading = false,
                    graphLayoutData = layoutData
                )
            )

            // Pre-genera todas las imagenes en segundo plano y las deja en cache.
            // No bloquea la UI; el detalle luego las lee del cache al instante.
            launch { preloadSubjectImagesUseCase(subjects) }
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
