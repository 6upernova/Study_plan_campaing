package edu.dyds.movies.domain.usecase

import com.sun.tools.jdeps.Graph
import edu.dyds.movies.domain.entity.Subject
import edu.dyds.movies.domain.repository.SubjectRepository

class GetAllSubjectsUseCaseImpl(
    private val subjectRepository: SubjectRepository,
) : GetAllSubjectsUseCase {
    override suspend operator fun invoke(): Graph<Subject> = subjectRepository.getAllSubjects()
}

