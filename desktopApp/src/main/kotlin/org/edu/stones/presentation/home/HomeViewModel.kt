package org.edu.stones.presentation.home

import androidx.lifecycle.ViewModel
import org.edu.stones.domain.usecase.GetAllSubjectsUseCase
import org.edu.stones.domain.usecase.GetSubjectDetailUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class HomeViewModel(
    private val getAllSubjectsUseCase: GetAllSubjectsUseCase,
    private val getSubjectsUseCase: GetSubjectDetailUseCase,
) : ViewModel() {

    fun getAllSubjects(){

    }

}