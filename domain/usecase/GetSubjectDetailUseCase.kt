package edu.dyds.movies.domain.usecase

import edu.dyds.movies.domain.entity.Subject

interface GetSubjectDetailUseCase {
    suspend operator fun invoke(id: String): Subject?
}
