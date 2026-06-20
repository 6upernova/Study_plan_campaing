package org.edu.stones.domain.usecase


import org.edu.stones.domain.entity.Subject
import org.edu.stones.domain.repository.SubjectRepository
import org.jgrapht.graph.DefaultDirectedGraph
import org.jgrapht.graph.DefaultEdge

class GetAllSubjectsUseCaseImpl(
    private val subjectRepository: SubjectRepository,
) : GetAllSubjectsUseCase {
    override suspend operator fun invoke(): DefaultDirectedGraph<Subject, DefaultEdge> {
        return subjectRepository.getAllSubjects()
    }
}

