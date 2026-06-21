package org.edu.stones.data.cache

import java.nio.file.Files
import java.nio.file.Path
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNull

class LegendDiskCacheTest {

    private lateinit var tempDir: Path
    private lateinit var cache: LegendDiskCache

    @BeforeTest
    fun setup() {
        tempDir = Files.createTempDirectory("legend-disk-test")
        cache = LegendDiskCache(cacheDir = tempDir)
    }

    @AfterTest
    fun cleanup() {
        Files.walk(tempDir).sorted(Comparator.reverseOrder()).forEach { Files.deleteIfExists(it) }
    }

    @Test
    fun `cacheKey es deterministico y distinto por prompt`() {
        assertEquals(cache.cacheKey("a"), cache.cacheKey("a"))
        assertNotEquals(cache.cacheKey("a"), cache.cacheKey("b"))
    }

    @Test
    fun `put y get hacen round-trip`() {
        val key = cache.cacheKey("materia")
        cache.put(key, "la leyenda del minotauro")
        assertEquals("la leyenda del minotauro", cache.get(key))
    }

    @Test
    fun `get de archivo inexistente devuelve null`() {
        assertNull(cache.get(cache.cacheKey("nunca-guardado")))
    }

    @Test
    fun `get de contenido en blanco devuelve null`() {
        val key = cache.cacheKey("vacio")
        cache.put(key, "   ")
        assertNull(cache.get(key))
    }
}
