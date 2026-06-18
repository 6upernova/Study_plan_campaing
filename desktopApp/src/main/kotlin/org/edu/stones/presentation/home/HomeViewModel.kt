package org.edu.stones.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import org.edu.stones.domain.usecase.GetAllSubjectsUseCase
import org.edu.stones.domain.usecase.GetSubjectDetailUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.edu.stones.domain.entity.Subject
import org.jgrapht.graph.DefaultDirectedGraph
import org.jgrapht.graph.DefaultEdge

class HomeViewModel(
    private val getAllSubjectsUseCase: GetAllSubjectsUseCase,
    private val getSubjectsUseCase: GetSubjectDetailUseCase,
) : ViewModel() {

    private val homeStateMutableStateFlow = MutableStateFlow(HomeUiState())

    val homeStateFlow: StateFlow<HomeUiState> = homeStateMutableStateFlow.asStateFlow()

    fun getAllSubjects() {
        viewModelScope.launch {
            homeStateMutableStateFlow.emit(HomeUiState(isLoading = true))

            val graph = getAllSubjectsUseCase()
            val subjectsList = graph.vertexSet().toList().map { subject ->
                Triple(subject.abreviatura, subject.codigo, (subject.anio - 1) * 2 + determatePeriod(subject.periodo))
            }

            homeStateMutableStateFlow.emit(
                HomeUiState(
                    isLoading = false,
                    subjectsList = subjectsList
                )
            )
        }
    }

    suspend fun getSubjectDetail(id: String): Subject? {
        return getSubjectsUseCase(id)
    }

    data class HomeUiState(
        val isLoading: Boolean = false,
        val subjectsList: List<Triple<String, String, Int>> = emptyList()
    )

    private fun determatePeriod(periodo: String): Int {
        if (periodo == "Primer Cuatrimestre") {
            return 1
        } else {
            return 2
        }
    }
}