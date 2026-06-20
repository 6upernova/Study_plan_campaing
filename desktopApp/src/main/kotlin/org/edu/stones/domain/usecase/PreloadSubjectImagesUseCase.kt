package org.edu.stones.domain.usecase

import org.edu.stones.domain.entity.Subject

interface PreloadSubjectImagesUseCase {
    suspend operator fun invoke(subjects: List<Subject>)
}
