package org.edu.stones.domain.usecase

import org.edu.stones.domain.entity.Subject

interface GetSubjectNameUseCase {
    suspend operator fun invoke(ids: String): String?
}
