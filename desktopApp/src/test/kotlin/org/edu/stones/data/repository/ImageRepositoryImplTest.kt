package org.edu.stones.data.repository

import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.edu.stones.data.local.image.ImageLocalDataSourceImpl
import org.edu.stones.data.external.ImageGenExternalSource
import java.nio.file.Files
import java.nio.file.Path
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ImageRepositoryImplTest {

    private lateinit var tempDir: Path
    private lateinit var cache: ImageLocalDataSourceImpl

    @BeforeTest
    fun setup() {
        tempDir = Files.createTempDirectory("image-cache-test")
        cache = ImageLocalDataSourceImpl(cacheDir = tempDir)
    }

    @AfterTest
    fun cleanup() {
        Files.walk(tempDir).sorted(Comparator.reverseOrder()).forEach { Files.deleteIfExists(it) }
        clearAllMocks()
    }

    @Test
    fun `segundo pedido del mismo prompt sale de memoria sin llamar al source`() = runTest {
        val source = mockk<ImageGenExternalSource> {
            coEvery { generate(any()) } returns byteArrayOf(1, 2, 3)
        }
        val repo = ImageRepositoryImpl(source, cache)

        repo.getImageForPrompt("mismo prompt")
        repo.getImageForPrompt("mismo prompt")

        coVerify(exactly = 1) { source.generate("mismo prompt") }
    }

    @Test
    fun `un repo nuevo con memoria vacia recupera del disco sin llamar al source`() = runTest {
        val sourceA = mockk<ImageGenExternalSource> {
            coEvery { generate(any()) } returns byteArrayOf(5, 6, 7)
        }
        ImageRepositoryImpl(sourceA, cache).getImageForPrompt("persistente")

        val sourceB = mockk<ImageGenExternalSource> {
            coEvery { generate(any()) } returns byteArrayOf(9, 9, 9)
        }
        val resultB = ImageRepositoryImpl(sourceB, cache).getImageForPrompt("persistente")

        coVerify(exactly = 0) { sourceB.generate(any()) }
        assertTrue(resultB != null && resultB.contentEquals(byteArrayOf(5, 6, 7)))
    }

    @Test
    fun `no cachea fallos del source`() = runTest {
        val source = mockk<ImageGenExternalSource> {
            coEvery { generate(any()) } returns null
        }
        val repo = ImageRepositoryImpl(source, cache)

        assertNull(repo.getImageForPrompt("falla"))
        assertNull(repo.getImageForPrompt("falla"))

        coVerify(exactly = 2) { source.generate("falla") }
    }

    @Test
    fun `cacheKey es deterministico y distinto por prompt`() = runTest {
        assertEquals(cache.cacheKey("a"), cache.cacheKey("a"))
        assertNotEquals(cache.cacheKey("a"), cache.cacheKey("b"))
    }
}
