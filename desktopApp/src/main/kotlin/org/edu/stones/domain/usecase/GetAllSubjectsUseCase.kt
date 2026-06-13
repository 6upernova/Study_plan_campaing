package org.edu.stones.domain.usecase

import org.edu.stones.domain.entity.Graph
import org.edu.stones.domain.entity.Subject

interface GetAllSubjectsUseCase {
	suspend operator fun invoke(): Graph<Subject>
}