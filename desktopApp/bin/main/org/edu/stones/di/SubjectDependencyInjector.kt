package org.edu.stones.di

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import org.edu.stones.data.repository.SubjectsRepositoryImpl
import org.edu.stones.presentation.home.HomeViewModel
import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import org.edu.stones.data.external.SubjectDetailExternalSource
import org.edu.stones.data.external.broker.SubjectsBroker
import org.edu.stones.data.external.dto.GoogleScriptSubjectExternalSource
import org.edu.stones.data.external.dto.PollinationsImageSource
import org.edu.stones.data.repository.ImageRepositoryImpl
import org.edu.stones.domain.usecase.GenerateSubjectImageUseCaseImpl
import org.edu.stones.data.local.SubjectLocalDataSource
import org.edu.stones.data.local.SubjectLocalDataSourceImpl
import org.edu.stones.domain.usecase.GetAllSubjectsUseCaseImpl
import org.edu.stones.domain.usecase.GetSubjectDetailUseCaseImpl
import org.edu.stones.domain.entity.Subject
import org.edu.stones.presentation.detail.DetailViewModel
import kotlin.collections.List

object SubjectDependencyInjector {

    private val localDataSource: SubjectLocalDataSource = SubjectLocalDataSourceImpl()

    private val sources: List<SubjectDetailExternalSource> = listOf(
        GoogleScriptSubjectExternalSource()
    )
    private val subjectsBroker = SubjectsBroker(sources)
    private val subjectRepository = SubjectsRepositoryImpl(subjectsBroker, localDataSource)

    private val imageSource = PollinationsImageSource()
    private val imageRepository = ImageRepositoryImpl(imageSource)
    private val generateSubjectImageUseCase = GenerateSubjectImageUseCaseImpl(imageRepository)

    @Composable
    fun getHomeViewModel(): HomeViewModel {
        return viewModel {
            HomeViewModel(
                getAllSubjectsUseCase = GetAllSubjectsUseCaseImpl(subjectRepository),
                getSubjectsUseCase = GetSubjectDetailUseCaseImpl(subjectRepository)
            )
        }
    }

    @Composable
    fun getDetailViewModel(): DetailViewModel {
        return viewModel {
            DetailViewModel(
                getSubjectDetailUseCase = GetSubjectDetailUseCaseImpl(subjectRepository),
                generateSubjectImageUseCase = generateSubjectImageUseCase
            )
        }
    }

}
