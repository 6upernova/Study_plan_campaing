package org.edu.stones.domain.usecase

import org.edu.stones.domain.entity.Subject

interface GenerateSubjectImageUseCase {
    suspend operator fun invoke(subject: Subject): ByteArray?
}
