package org.edu.stones.domain.usecase

import org.edu.stones.domain.entity.Subject

interface GetSubjectDetailUseCase {
    suspend operator fun invoke(id: String): Subject?
}
