package org.edu.stones.data.repository

import kotlinx.coroutines.test.runTest
import org.edu.stones.data.cache.LegendDiskCache
import org.edu.stones.data.external.LegendExternalSource
import java.nio.file.Files
import java.nio.file.Path
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

private class CountingLegendSource(private val legend: String?) : LegendExternalSource {
    var calls = 0
        private set

    override suspend fun generate(prompt: String): String? {
        calls++
        return legend
    }
}

class LegendRepositoryImplTest {

    private lateinit var tempDir: Path
    private lateinit var cache: LegendDiskCache

    @BeforeTest
    fun setup() {
        tempDir = Files.createTempDirectory("legend-cache-test")
        cache = LegendDiskCache(cacheDir = tempDir)
    }

    @AfterTest
    fun cleanup() {
        Files.walk(tempDir).sorted(Comparator.reverseOrder()).forEach { Files.deleteIfExists(it) }
    }

    @Test
    fun `segundo pedido del mismo prompt sale de memoria sin llamar al source`() = runTest {
        val source = CountingLegendSource("una leyenda epica")
        val repo = LegendRepositoryImpl(source, cache)

        repo.getLegendForPrompt("mismo prompt")
        val second = repo.getLegendForPrompt("mismo prompt")

        assertEquals(1, source.calls)
        assertEquals("una leyenda epica", second)
    }

    @Test
    fun `un repo nuevo con memoria vacia recupera del disco sin llamar al source`() = runTest {
        val sourceA = CountingLegendSource("persistida en disco")
        LegendRepositoryImpl(sourceA, cache).getLegendForPrompt("persistente")

        val sourceB = CountingLegendSource("otra distinta")
        val resultB = LegendRepositoryImpl(sourceB, cache).getLegendForPrompt("persistente")

        assertEquals(0, sourceB.calls)
        assertEquals("persistida en disco", resultB)
    }

    @Test
    fun `no cachea fallos del source`() = runTest {
        val source = CountingLegendSource(null)
        val repo = LegendRepositoryImpl(source, cache)

        assertNull(repo.getLegendForPrompt("falla"))
        assertNull(repo.getLegendForPrompt("falla"))

        assertEquals(2, source.calls)
    }
}
