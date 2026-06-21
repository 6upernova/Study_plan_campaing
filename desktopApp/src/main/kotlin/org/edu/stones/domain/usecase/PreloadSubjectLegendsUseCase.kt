package org.edu.stones.domain.usecase

import org.edu.stones.domain.entity.Subject

interface PreloadSubjectLegendsUseCase {
    suspend operator fun invoke(subjects: List<Subject>)
}
