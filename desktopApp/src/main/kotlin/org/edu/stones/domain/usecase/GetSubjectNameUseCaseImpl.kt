package org.edu.stones.domain.usecase

import org.edu.stones.domain.entity.Subject
import org.edu.stones.domain.repository.SubjectRepository

class GetSubjectNameUseCaseImpl(
    private val subjectRepository: SubjectRepository
) : GetSubjectNameUseCase {

    override suspend operator fun invoke(ids: String): String? {
        val names = ids
            .split(", ")
            .mapNotNull { id ->
                subjectRepository
                    .getSubjectDetail(id)
                    ?.nombre
            }

        return names.takeIf { it.isNotEmpty() }
            ?.joinToString(", ")
            ?: "Ninguna"
    }
}

