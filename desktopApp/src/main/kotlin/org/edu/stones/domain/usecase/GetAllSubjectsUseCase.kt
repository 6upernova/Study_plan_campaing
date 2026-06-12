package org.edu.stones.domain.usecase

import com.sun.tools.jdeps.Graph
import org.edu.stones.domain.entity.Subject

interface GetAllSubjectsUseCase {
	suspend operator fun invoke(): Graph<Subject>
}