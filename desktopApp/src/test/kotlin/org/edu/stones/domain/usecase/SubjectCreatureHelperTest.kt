package org.edu.stones.domain.usecase

import org.edu.stones.subject
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SubjectCreatureHelperTest {

    @Test
    fun `selectCreature es determinista para el mismo codigo`() {
        val s1 = subject(codigo = "TUP123", anio = 2)
        val s2 = subject(codigo = "TUP123", anio = 2, nombre = "otro nombre")

        assertEquals(selectCreature(s1), selectCreature(s2))
    }

    @Test
    fun `selectCreature siempre devuelve indice dentro de rango para cualquier anio`() {

        val anios = listOf(1, 2, 3, 4, 5, 99)
        val codigos = listOf("A", "ZZZ", "codigo-con-hash-negativo", "1234567890")

        for (anio in anios) {
            for (codigo in codigos) {

                val creature = selectCreature(subject(codigo = codigo, anio = anio))
                assertTrue(creature.nameEn.isNotEmpty())
                assertTrue(creature.nameEs.isNotEmpty())
            }
        }
    }

    @Test
    fun `selectCreature usa listas distintas por anio`() {

        val anio1Creatures = (1..5).map { selectCreature(subject(codigo = "C$it", anio = 1)).nameEn }
        val anio5Creatures = (1..5).map { selectCreature(subject(codigo = "C$it", anio = 5)).nameEn }

        val pool1 = setOf(
            "a goblin", "a sneaky thief", "a kobold", "a giant rat", "a mischievous imp"
        )
        val pool5 = setOf(
            "a mighty dragon", "a chimera", "a demon lord", "a multi-headed hydra", "an ancient lich"
        )

        assertTrue(anio1Creatures.all { it in pool1 })
        assertTrue(anio5Creatures.all { it in pool5 })
    }

    @Test
    fun `creatureDifficulty con cero inscriptos es EASY`() {

        val s = subject(inscriptos = 0, cantAprobados = 0, notasPromedio = 9.0)
        assertEquals(CreatureDifficulty.EASY, creatureDifficulty(s))
    }

    @Test
    fun `creatureDifficulty nota alta y alta aprobacion es EASY`() {
        val s = subject(notasPromedio = 9.0, inscriptos = 100, cantAprobados = 95)
        assertEquals(CreatureDifficulty.EASY, creatureDifficulty(s))
    }

    @Test
    fun `creatureDifficulty nota baja y baja aprobacion es HARD`() {
        val s = subject(notasPromedio = 3.0, inscriptos = 100, cantAprobados = 20)
        assertEquals(CreatureDifficulty.HARD, creatureDifficulty(s))
    }

    @Test
    fun `creatureDifficulty caso intermedio es MEDIUM`() {

        val s = subject(notasPromedio = 6.0, inscriptos = 100, cantAprobados = 55)
        assertEquals(CreatureDifficulty.MEDIUM, creatureDifficulty(s))
    }
}
