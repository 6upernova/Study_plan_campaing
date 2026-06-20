package org.edu.stones.domain.usecase

import kotlinx.coroutines.test.runTest
import org.edu.stones.domain.entity.Subject
import java.util.concurrent.ConcurrentHashMap
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

private fun subject(codigo: String) = Subject(
    codigo = codigo,
    nombre = "Materia $codigo",
    anio = 1,
    periodo = "Primer Cuatrimestre",
    anioDeRecopilacion = 2024,
    notasPromedio = 7.0,
    inscriptos = 10,
    presencialidad = "Presencial",
    cantAprobados = 5,
    correlativasCursadas = null,
    correlativasAprobadas = null,
    abreviatura = codigo,
)

class PreloadSubjectImagesUseCaseImplTest {

    @Test
    fun `genera la imagen de todas las materias`() = runTest {
        val pedidos = ConcurrentHashMap.newKeySet<String>()
        val generate = object : GenerateSubjectImageUseCase {
            override suspend fun invoke(subject: Subject): ByteArray {
                pedidos.add(subject.codigo)
                return byteArrayOf(1)
            }
        }
        val preload = PreloadSubjectImagesUseCaseImpl(generate, backoffBaseMs = 0)

        val subjects = listOf("A", "B", "C", "D").map { subject(it) }
        preload(subjects)

        assertEquals(setOf("A", "B", "C", "D"), pedidos)
    }

    @Test
    fun `reintenta las que fallan hasta tener exito`() = runTest {
        val intentos = ConcurrentHashMap<String, Int>()
        val generate = object : GenerateSubjectImageUseCase {
            override suspend fun invoke(subject: Subject): ByteArray? {
                val n = intentos.merge(subject.codigo, 1, Int::plus)!!
                return if (n >= 2) byteArrayOf(1) else null // falla la 1ra, ok la 2da
            }
        }
        val preload = PreloadSubjectImagesUseCaseImpl(generate, maxRetries = 3, backoffBaseMs = 0)

        preload(listOf(subject("X")))

        assertEquals(2, intentos["X"])
    }

    @Test
    fun `se rinde despues de maxRetries sin colgarse`() = runTest {
        val intentos = ConcurrentHashMap<String, Int>()
        val generate = object : GenerateSubjectImageUseCase {
            override suspend fun invoke(subject: Subject): ByteArray? {
                intentos.merge(subject.codigo, 1, Int::plus)
                return null // siempre falla
            }
        }
        val preload = PreloadSubjectImagesUseCaseImpl(generate, maxRetries = 3, backoffBaseMs = 0)

        preload(listOf(subject("Y")))

        assertTrue(intentos["Y"] == 3)
    }
}
