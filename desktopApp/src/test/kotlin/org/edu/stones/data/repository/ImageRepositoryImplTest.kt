package org.edu.stones.data.repository

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

private class CountingSource(private val bytes: ByteArray?) : ImageGenExternalSource {
    var calls = 0
        private set

    override suspend fun generate(prompt: String): ByteArray? {
        calls++
        return bytes
    }
}

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
    }

    @Test
    fun `segundo pedido del mismo prompt sale de memoria sin llamar al source`() = runTest {
        val source = CountingSource(byteArrayOf(1, 2, 3))
        val repo = ImageRepositoryImpl(source, cache)

        repo.getImageForPrompt("mismo prompt")
        repo.getImageForPrompt("mismo prompt")

        assertEquals(1, source.calls)
    }

    @Test
    fun `un repo nuevo con memoria vacia recupera del disco sin llamar al source`() = runTest {
        val sourceA = CountingSource(byteArrayOf(5, 6, 7))
        ImageRepositoryImpl(sourceA, cache).getImageForPrompt("persistente")

        val sourceB = CountingSource(byteArrayOf(9, 9, 9))
        val resultB = ImageRepositoryImpl(sourceB, cache).getImageForPrompt("persistente")

        assertEquals(0, sourceB.calls)
        assertTrue(resultB != null && resultB.contentEquals(byteArrayOf(5, 6, 7)))
    }

    @Test
    fun `no cachea fallos del source`() = runTest {
        val source = CountingSource(null)
        val repo = ImageRepositoryImpl(source, cache)

        assertNull(repo.getImageForPrompt("falla"))
        assertNull(repo.getImageForPrompt("falla"))

        assertEquals(2, source.calls)
    }

    @Test
    fun `cacheKey es deterministico y distinto por prompt`() {
        assertEquals(cache.cacheKey("a"), cache.cacheKey("a"))
        assertNotEquals(cache.cacheKey("a"), cache.cacheKey("b"))
    }
}
