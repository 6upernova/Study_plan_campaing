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
        val criaturas = criaturasPorAnio(subject.anio)
        // pseudo-aleatorio pero estable: depende del codigo de la materia
        val criatura = criaturas[Math.floorMod(subject.codigo.hashCode(), criaturas.size)]

        return "fantasy RPG digital painting, highly detailed concept art, " +
            "$criatura themed around \"${subject.nombre}\", " +
            "surrounded by magical objects and symbols related to ${subject.nombre}, " +
            "in a medieval fantasy setting, warm dramatic lighting, painterly, " +
            "same epic art style as a classic fantasy creature portrait"
    }

    private fun criaturasPorAnio(anio: Int): List<String> = when (anio) {
        1 -> listOf("a goblin", "a sneaky thief", "a kobold", "a giant rat", "a mischievous imp")
        2 -> listOf("an orc warrior", "a bandit", "a skeleton soldier", "a giant spider", "a harpy")
        3 -> listOf("an ogre", "a cave troll", "a dark knight", "a vengeful wraith", "a stone gargoyle")
        4 -> listOf("a minotaur", "a stone golem", "a vampire lord", "a manticore", "a basilisk")
        else -> listOf("a mighty dragon", "a chimera", "a demon lord", "a multi-headed hydra", "an ancient lich")
    }
}
