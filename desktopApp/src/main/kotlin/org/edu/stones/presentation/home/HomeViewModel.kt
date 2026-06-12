package org.edu.stones.presentation.home

import androidx.lifecycle.ViewModel
import edu.dyds.movies.domain.entity.QualifiedMovie
import edu.dyds.movies.domain.usecase.GetPopularMoviesUseCase
import org.edu.stones.domain.usecase.GetAllSubjectsUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class HomeViewModel(
    private val getPopularMoviesUseCase: GetPopularMoviesUseCase,
    private val getGetUseCase: GetPopularMoviesUseCase,
) : ViewModel() {

   // private val homeStateMutableStateFlow = MutableStateFlow(HomeUiState())

    val homeStateFlow: Flow<HomeUiState> = homeStateMutableStateFlow

    fun getAllSubjects(){

    }

//    data class HomeUiState(
//        val isLoading: Boolean = false,
//        val movies: List<QualifiedMovie> = emptyList(),
//    )
}