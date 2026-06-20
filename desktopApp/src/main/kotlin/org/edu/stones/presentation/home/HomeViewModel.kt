package org.edu.stones.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import org.edu.stones.domain.entity.Subject
import org.edu.stones.domain.usecase.GetAllSubjectsUseCase
import org.edu.stones.domain.usecase.GetSubjectDetailUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.edu.stones.presentation.home.components.GraphLayoutData
import org.edu.stones.presentation.home.components.GraphLayoutEngine

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
            val layoutData = GraphLayoutEngine.computeLayout(graph)

            homeStateMutableStateFlow.emit(
                HomeUiState(
                    isLoading = false,
                    graphLayoutData = layoutData
                )
            )
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