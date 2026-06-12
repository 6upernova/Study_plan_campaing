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
import org.edu.stones.domain.usecase.GetAllSubjectsUseCaseImpl
import org.edu.stones.domain.usecase.GetSubjectDetailUseCaseImpl

private val TMDB_API_KEY: String = System.getenv("TMDB_API_KEY")
    ?: "d18da1b5da16397619c688b0263cd281"

object MoviesDependencyInjector {

    private val tmdbHttpClient =
        HttpClient {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                })
            }
            install(DefaultRequest) {
                url {
                    protocol = URLProtocol.HTTPS
                    host = "api.themoviedb.org"
                    parameters.append("api_key", TMDB_API_KEY)
                }
            }
            install(HttpTimeout) {
                requestTimeoutMillis = 5000
            }
        }

    //private val localDataSource = MoviesLocalDataSourceImpl()

    //private val tmdbRemoteSource = TMDBMoviesExternalSource(tmdbHttpClient)

    private val omdbApiKey: String
        get() {
            val envKey = System.getenv("OMDB_API_KEY")
            if (envKey != null) return envKey
            val env = System.getenv("APP_ENV") ?: "development"
            return if (env == "development") {
                "a96e7f78"
            } else {
                error("OMDB_API_KEY environment variable is not set")
            }
        }

//    private val omdbRemoteSource = OMDBMoviesExternalSource(apiKey = omdbApiKey)
//
//      private val popularMoviesExternalSource = tmdbRemoteSource
//      private val movieDetailExternalSource = MoviesBroker(
//        tmdb = tmdbRemoteSource,
//        omdb = omdbRemoteSource,
//      )

    @Composable
    fun getHomeViewModel(): HomeViewModel {
        return viewModel {
            val subjectRepository = SubjectsRepositoryImpl()
//                getAllSubjectsUseCase = popularMoviesExternalSource,
//            movieDetailExternalSource = movieDetailExternalSource,
//                localDataSource = localDataSource
//            )
            HomeViewModel(
                getAllSubjectsUseCase = GetAllSubjectsUseCaseImpl(subjectRepository),
                getSubjectsUseCase = GetSubjectDetailUseCaseImpl(subjectRepository)
            )
        }
    }


//    @Composable
//    fun getDetailViewModel(): DetailViewModel {
//        return viewModel {
//            val moviesRepository = MoviesRepositoryImpl(
//            popularMoviesExternalSource = popularMoviesExternalSource,
//            movieDetailExternalSource = movieDetailExternalSource,
//                localDataSource = localDataSource
//            )
//            DetailViewModel(
//                getMovieDetailUseCase = GetMovieDetailUseCaseImpl(moviesRepository)
//            )
//        }
//    }

}
