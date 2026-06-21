package org.edu.stones.di

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import org.edu.stones.data.repository.SubjectsRepositoryImpl
import org.edu.stones.presentation.home.HomeViewModel
import org.edu.stones.data.external.SubjectDetailExternalSource
import org.edu.stones.data.external.broker.SubjectsBroker
import org.edu.stones.data.external.dto.GoogleScriptSubjectExternalSource
import org.edu.stones.data.local.image.ImageLocalDataSource
import org.edu.stones.data.local.image.ImageLocalDataSourceImpl
import org.edu.stones.data.external.dto.ImageGenExternalSourceImpl
import org.edu.stones.data.external.dto.LegendExternalSourceImpl
import org.edu.stones.data.external.dto.OpenAiLegendExternalSourceImpl
import org.edu.stones.data.external.broker.LegendBroker
import org.edu.stones.data.local.legend.LegendLocalDataSource
import org.edu.stones.data.local.legend.LegendLocalDataSourceImpl
import org.edu.stones.data.repository.ImageRepositoryImpl
import org.edu.stones.data.repository.LegendRepositoryImpl
import org.edu.stones.domain.usecase.GenerateSubjectImageUseCaseImpl
import org.edu.stones.domain.usecase.GetSubjectLegendUseCaseImpl
import org.edu.stones.domain.usecase.PreloadSubjectImagesUseCaseImpl
import org.edu.stones.domain.usecase.PreloadSubjectLegendsUseCaseImpl
import org.edu.stones.data.local.subjects.SubjectLocalDataSource
import org.edu.stones.data.local.subjects.SubjectLocalDataSourceImpl
import org.edu.stones.domain.usecase.GetAllSubjectsUseCaseImpl
import org.edu.stones.domain.usecase.GetSubjectDetailUseCaseImpl
import org.edu.stones.presentation.detail.DetailViewModel
import kotlin.collections.List

object SubjectDependencyInjector {

    private val localDataSource: SubjectLocalDataSource = SubjectLocalDataSourceImpl()

    private val sources: List<SubjectDetailExternalSource> = listOf(
        GoogleScriptSubjectExternalSource()
    )
    private val subjectsBroker = SubjectsBroker(sources)
    private val subjectRepository = SubjectsRepositoryImpl(subjectsBroker, localDataSource)

    private val imageSource = ImageGenExternalSourceImpl()
    private val imageLocalDataSource: ImageLocalDataSource = ImageLocalDataSourceImpl()
    private val imageRepository = ImageRepositoryImpl(imageSource, imageLocalDataSource)
    private val generateSubjectImageUseCase = GenerateSubjectImageUseCaseImpl(imageRepository)
    private val preloadSubjectImagesUseCase = PreloadSubjectImagesUseCaseImpl(generateSubjectImageUseCase)

    private val legendBroker = LegendBroker(listOf(
        LegendExternalSourceImpl(),
        OpenAiLegendExternalSourceImpl()
    ))
    private val legendLocalDataSource: LegendLocalDataSource = LegendLocalDataSourceImpl()
    private val legendRepository = LegendRepositoryImpl(legendBroker, legendLocalDataSource)
    private val getSubjectLegendUseCase = GetSubjectLegendUseCaseImpl(legendRepository)
    private val preloadSubjectLegendsUseCase = PreloadSubjectLegendsUseCaseImpl(getSubjectLegendUseCase)

    @Composable
    fun getHomeViewModel(): HomeViewModel {
        return viewModel {
            HomeViewModel(
                getAllSubjectsUseCase = GetAllSubjectsUseCaseImpl(subjectRepository),
                getSubjectsUseCase = GetSubjectDetailUseCaseImpl(subjectRepository),
                preloadSubjectImagesUseCase = preloadSubjectImagesUseCase,
                preloadSubjectLegendsUseCase = preloadSubjectLegendsUseCase
            )
        }
    }

    @Composable
    fun getDetailViewModel(subjectCode: String): DetailViewModel {
        return viewModel(key = "detail-$subjectCode") {
            DetailViewModel(
                getSubjectDetailUseCase = GetSubjectDetailUseCaseImpl(subjectRepository),
                generateSubjectImageUseCase = generateSubjectImageUseCase,
                getSubjectLegendUseCase = getSubjectLegendUseCase
            )
        }
    }

}
