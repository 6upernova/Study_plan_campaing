package edu.dyds.movies.domain.usecase


import edu.dyds.movies.domain.entity.Subject
import edu.dyds.movies.domain.repository.SubjectRepository

class GetSubjectDetailUseCaseImpl(
    private val subjectRepository: SubjectRepository
) : GetSubjectDetailUseCase {
    override suspend operator fun invoke(id: String): Subject? = subjectRepository.getSubjectDetail(id)
}

