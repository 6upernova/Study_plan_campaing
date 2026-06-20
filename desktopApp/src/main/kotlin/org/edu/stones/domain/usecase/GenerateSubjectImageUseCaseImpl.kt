package org.edu.stones.domain.usecase

import org.edu.stones.domain.entity.Subject
import org.edu.stones.domain.repository.ImageRepository

class GenerateSubjectImageUseCaseImpl(
    private val imageRepository: ImageRepository
) : GenerateSubjectImageUseCase {
    override suspend fun invoke(subject: Subject): ByteArray? {
        val prompt = "ilustración educativa minimalista sobre ${subject.nombre}, estilo plano, fondo claro"
        return imageRepository.getImageForPrompt(prompt)
    }
}
