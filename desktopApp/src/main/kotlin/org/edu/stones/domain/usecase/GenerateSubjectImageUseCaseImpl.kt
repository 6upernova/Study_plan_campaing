package org.edu.stones.domain.usecase

import org.edu.stones.domain.entity.Subject
import org.edu.stones.domain.repository.ImageRepository

class GenerateSubjectImageUseCaseImpl(
    private val imageRepository: ImageRepository
) : GenerateSubjectImageUseCase {
    override suspend fun invoke(subject: Subject): ByteArray? {
        val prompt = buildPrompt(subject)
        return imageRepository.getImageForPrompt(prompt)
    }

    private fun buildPrompt(subject: Subject): String {
        val creature = selectCreature(subject)
        val mood = when (creatureDifficulty(subject)) {
            CreatureDifficulty.EASY ->
                "calm and approachable, soft warm lighting, friendly welcoming atmosphere"
            CreatureDifficulty.MEDIUM ->
                "threatening and cunning, dramatic moody lighting, dangerous aura"
            CreatureDifficulty.HARD ->
                "terrifying and battle-scarred, dark ominous lighting, menacing shadows, fearsome presence"
        }
        return "fantasy RPG digital painting, highly detailed concept art, " +
            "${creature.nameEn} themed around \"${subject.nombre}\", $mood, " +
            "surrounded by magical objects and symbols related to ${subject.nombre}, " +
            "in a medieval fantasy setting, painterly, " +
            "same epic art style as a classic fantasy creature portrait"
    }
}
