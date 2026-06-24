package org.edu.stones.data.local.legend

import io.mockk.clearAllMocks
import io.mockk.mockk
import java.nio.file.Files
import java.nio.file.Path
import kotlinx.coroutines.test.runTest
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNull

class LegendLocalDataSourceImplTest {

    private lateinit var tempDir: Path
    private lateinit var cache: LegendLocalDataSourceImpl

    @BeforeTest
    fun setup() {
        tempDir = Files.createTempDirectory("legend-disk-test")
        cache = LegendLocalDataSourceImpl(cacheDir = tempDir)
    }

    @AfterTest
    fun cleanup() {
        Files.walk(tempDir).sorted(Comparator.reverseOrder()).forEach { Files.deleteIfExists(it) }
        clearAllMocks()
    }

    @Test
    fun `cacheKey es deterministico y distinto por prompt`() = runTest {
        assertEquals(cache.cacheKey("a"), cache.cacheKey("a"))
        assertNotEquals(cache.cacheKey("a"), cache.cacheKey("b"))
    }

    @Test
    fun `put y get hacen round-trip`() = runTest {
        val key = cache.cacheKey("materia")
        cache.put(key, "la leyenda del minotauro")
        assertEquals("la leyenda del minotauro", cache.get(key).getOrThrow())
    }

    @Test
    fun `get de archivo inexistente devuelve null`() = runTest {
        assertNull(cache.get(cache.cacheKey("nunca-guardado")).getOrThrow())
    }

    @Test
    fun `get de contenido en blanco devuelve null`() = runTest {
        val key = cache.cacheKey("vacio")
        cache.put(key, "   ")
        assertNull(cache.get(key).getOrThrow())
    }
}
