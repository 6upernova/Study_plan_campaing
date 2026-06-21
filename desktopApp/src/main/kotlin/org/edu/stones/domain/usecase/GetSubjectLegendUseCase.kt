package org.edu.stones.domain.usecase

import org.edu.stones.domain.entity.Subject

interface GetSubjectLegendUseCase {
    suspend operator fun invoke(subject: Subject): String?
}
