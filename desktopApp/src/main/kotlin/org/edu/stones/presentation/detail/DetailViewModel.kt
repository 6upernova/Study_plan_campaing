package org.edu.stones.presentation.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.edu.stones.domain.usecase.GetSubjectDetailUseCase

class DetailViewModel(
    private val getSubjectDetailUseCase: GetSubjectDetailUseCase,
) : ViewModel() {}