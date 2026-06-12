package edu.dyds.movies.domain.usecase

import com.sun.tools.jdeps.Graph
import edu.dyds.movies.domain.entity.Subject

interface GetAllSubjectsUseCase {
	suspend operator fun invoke(): Graph<Subject>
}