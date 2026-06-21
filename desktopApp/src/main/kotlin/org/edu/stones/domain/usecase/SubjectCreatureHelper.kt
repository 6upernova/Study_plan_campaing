package org.edu.stones.domain.usecase

import org.edu.stones.domain.entity.Subject

data class SubjectCreature(val nameEn: String, val nameEs: String)

enum class CreatureDifficulty { EASY, MEDIUM, HARD }

fun selectCreature(subject: Subject): SubjectCreature {
    val list = creaturesByYear(subject.anio)
    return list[Math.floorMod(subject.codigo.hashCode(), list.size)]
}

fun creatureDifficulty(subject: Subject): CreatureDifficulty {
    val approvalRate = if (subject.inscriptos > 0)
        subject.cantAprobados.toFloat() / subject.inscriptos.toFloat() else 1f
    // Promedio de nota baja + tasa de aprobación baja = más difícil
    val score = (10.0 - subject.notasPromedio) / 10.0 * 0.5 + (1.0 - approvalRate) * 0.5
    return when {
        score < 0.33 -> CreatureDifficulty.EASY
        score < 0.55 -> CreatureDifficulty.MEDIUM
        else -> CreatureDifficulty.HARD
    }
}

private fun creaturesByYear(anio: Int): List<SubjectCreature> = when (anio) {
    1 -> listOf(
        SubjectCreature("a goblin", "un goblin"),
        SubjectCreature("a sneaky thief", "un ladrón sigiloso"),
        SubjectCreature("a kobold", "un kobold"),
        SubjectCreature("a giant rat", "una rata gigante"),
        SubjectCreature("a mischievous imp", "un diablillo travieso")
    )
    2 -> listOf(
        SubjectCreature("an orc warrior", "un guerrero orco"),
        SubjectCreature("a bandit", "un bandido"),
        SubjectCreature("a skeleton soldier", "un soldado esqueleto"),
        SubjectCreature("a giant spider", "una araña gigante"),
        SubjectCreature("a harpy", "una harpía")
    )
    3 -> listOf(
        SubjectCreature("an ogre", "un ogro"),
        SubjectCreature("a cave troll", "un troll de las cavernas"),
        SubjectCreature("a dark knight", "un caballero oscuro"),
        SubjectCreature("a vengeful wraith", "un espectro vengativo"),
        SubjectCreature("a stone gargoyle", "una gárgola de piedra")
    )
    4 -> listOf(
        SubjectCreature("a minotaur", "un minotauro"),
        SubjectCreature("a stone golem", "un gólem de piedra"),
        SubjectCreature("a vampire lord", "un señor vampiro"),
        SubjectCreature("a manticore", "una mantícora"),
        SubjectCreature("a basilisk", "un basilisco")
    )
    else -> listOf(
        SubjectCreature("a mighty dragon", "un dragón ancestral"),
        SubjectCreature("a chimera", "una quimera"),
        SubjectCreature("a demon lord", "un señor demonio"),
        SubjectCreature("a multi-headed hydra", "una hidra multicéfala"),
        SubjectCreature("an ancient lich", "un liche ancestral")
    )
}
