package org.edu.stones.domain.usecase

import com.sun.tools.jdeps.Graph
import org.edu.stones.domain.entity.Subject
import org.edu.stones.domain.repository.SubjectRepository

class GetAllSubjectsUseCaseImpl(
    private val subjectRepository: SubjectRepository,
) : GetAllSubjectsUseCase {
    override suspend operator fun invoke(): Graph<Subject> = subjectRepository.getAllSubjects()
}

