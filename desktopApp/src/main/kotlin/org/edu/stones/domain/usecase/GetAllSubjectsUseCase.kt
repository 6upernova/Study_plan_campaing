package org.edu.stones.domain.usecase

import org.jgrapht.graph.DefaultDirectedGraph
import org.jgrapht.graph.DefaultEdge
import org.edu.stones.domain.entity.Subject

interface GetAllSubjectsUseCase {
	suspend operator fun invoke(): DefaultDirectedGraph<Subject, DefaultEdge>
}