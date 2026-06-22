package org.edu.stones.presentation.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.edu.stones.domain.entity.Subject
import org.edu.stones.domain.usecase.GenerateSubjectImageUseCase
import org.edu.stones.domain.usecase.GetSubjectDetailUseCase
import org.edu.stones.domain.usecase.GetSubjectLegendUseCase
import org.edu.stones.domain.usecase.GetSubjectNameUseCase
import kotlin.Boolean
import kotlin.String

class DetailViewModel(
    private val getSubjectDetailUseCase: GetSubjectDetailUseCase,
    private val generateSubjectImageUseCase: GenerateSubjectImageUseCase,
    private val getSubjectLegendUseCase: GetSubjectLegendUseCase,
    private val getSubjectNameUseCase: GetSubjectNameUseCase
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

            if (subject != null) {
                loadSubjectImage(subject)
                loadSubjectLegend(subject)
            }
        }
    }


    private fun loadSubjectLegend(subject: Subject) {
        viewModelScope.launch {
            detailUiState.value = detailUiState.value.copy(isLegendLoading = true)
            val legend = getSubjectLegendUseCase(subject)
            detailUiState.value = detailUiState.value.copy(
                isLegendLoading = false,
                legend = legend
            )
        }
    }

    private fun loadSubjectImage(subject: Subject) {
        viewModelScope.launch {
            detailUiState.value = detailUiState.value.copy(isImageLoading = true)
            val imageBytes = generateSubjectImageUseCase(subject)
            detailUiState.value = detailUiState.value.copy(
                isImageLoading = false,
                imageBytes = imageBytes
            )
        }
    }

    data class DetailUiState(
        val isLoading: Boolean = false,
        val subject: Subject? = null,
        val imageBytes: ByteArray? = null,
        val isImageLoading: Boolean = false,
        val legend: String? = null,
        val isLegendLoading: Boolean = false,
        val aprovNames: String? = "Ninguna",
        val courseNames: String? = "Ninguna"
    )

    fun getDetail() {
        detailUiState.value = DetailUiState()
    }

    fun getAprovNames(codes: String) {
        viewModelScope.launch {
            detailUiState.update { current ->
                current.copy(
                    aprovNames = getSubjectNameUseCase(codes)
                )
            }
        }
    }

    fun getCourseNames(codes: String) {
        viewModelScope.launch {
            detailUiState.update { current ->
                current.copy(
                    courseNames = getSubjectNameUseCase(codes)
                )
            }
        }
    }
}