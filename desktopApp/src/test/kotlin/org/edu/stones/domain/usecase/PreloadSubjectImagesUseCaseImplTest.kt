package org.edu.stones.domain.usecase

import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.edu.stones.domain.entity.Subject
import org.edu.stones.subject
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PreloadSubjectImagesUseCaseImplTest {

    @AfterTest
    fun cleanup() {
        clearAllMocks()
    }

    @Test
    fun `genera la imagen de todas las materias`() = runTest {
        val generate: GenerateSubjectImageUseCase = mockk()
        coEvery { generate(any()) } returns byteArrayOf(1)
        val preload = PreloadSubjectImagesUseCaseImpl(generate, delayBetweenRequestsMs = 0)

        val subjects = listOf("A", "B", "C", "D").map { subject(it) }
        preload(subjects)

        coVerify(exactly = 1) { generate(subject(codigo = "A")) }
        coVerify(exactly = 1) { generate(subject(codigo = "B")) }
        coVerify(exactly = 1) { generate(subject(codigo = "C")) }
        coVerify(exactly = 1) { generate(subject(codigo = "D")) }
    }

    @Test
    fun `reintenta las que fallan hasta tener exito`() = runTest {
        var calls = 0
        val generate: GenerateSubjectImageUseCase = mockk()
        coEvery { generate(any()) } answers {
            calls++
            if (calls >= 2) byteArrayOf(1) else null
        }
        val preload = PreloadSubjectImagesUseCaseImpl(generate, delayBetweenRequestsMs = 0, maxAttemptsPerSubject = 3)

        preload(listOf(subject("X")))

        assertEquals(2, calls)
    }

    @Test
    fun `se rinde despues de maxRetries sin colgarse`() = runTest {
        var calls = 0
        val generate: GenerateSubjectImageUseCase = mockk()
        coEvery { generate(any()) } answers {
            calls++
            null
        }
        val preload = PreloadSubjectImagesUseCaseImpl(generate, delayBetweenRequestsMs = 0, maxAttemptsPerSubject = 3)

        preload(listOf(subject("Y")))

        assertTrue(calls == 3)
    }
}
