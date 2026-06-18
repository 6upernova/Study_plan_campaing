package org.edu.stones.presentation.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.edu.stones.domain.entity.Subject
import org.edu.stones.domain.usecase.GetSubjectDetailUseCase

class DetailViewModel(
    private val getSubjectDetailUseCase: GetSubjectDetailUseCase,
) : ViewModel() {

    private val detailUiState = MutableStateFlow(DetailUiState())

    val detailStateFlow: StateFlow<DetailUiState> =
        detailUiState.asStateFlow()

    fun getSubject(code: String) {
        viewModelScope.launch {
            detailUiState.value = DetailUiState(
                isLoading = true
            )

            val subject = getSubjectDetailUseCase(code)

            detailUiState.value = DetailUiState(
                isLoading = false,
                subject = subject
            )
        }
    }

    data class DetailUiState(
        val isLoading: Boolean = false,
        val subject: Subject? = null,
    )

    fun getDetail() {
        detailUiState.value = DetailUiState()
    }
}