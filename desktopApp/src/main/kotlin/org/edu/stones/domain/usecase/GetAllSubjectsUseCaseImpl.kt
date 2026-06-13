package org.edu.stones.domain.usecase

import org.edu.stones.domain.entity.Graph
import org.edu.stones.domain.entity.Subject
import org.edu.stones.domain.repository.SubjectRepository

class GetAllSubjectsUseCaseImpl(
    private val subjectRepository: SubjectRepository,
) : GetAllSubjectsUseCase {
    override suspend operator fun invoke(): Graph<Subject> {
        return subjectRepository.getAllSubjects()
    }
}

