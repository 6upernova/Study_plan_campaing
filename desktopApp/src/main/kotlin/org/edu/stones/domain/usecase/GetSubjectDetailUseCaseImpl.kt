package org.edu.stones.domain.usecase

import org.edu.stones.domain.entity.Subject
import org.edu.stones.domain.repository.SubjectRepository

class GetSubjectDetailUseCaseImpl(
    private val subjectRepository: SubjectRepository
) : GetSubjectDetailUseCase {
    override suspend operator fun invoke(id: String): Subject? = subjectRepository.getSubjectDetail(id)
}

