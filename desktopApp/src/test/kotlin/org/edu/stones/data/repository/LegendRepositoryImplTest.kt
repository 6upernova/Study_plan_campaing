package org.edu.stones.data.repository

import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.edu.stones.data.local.legend.LegendLocalDataSourceImpl
import org.edu.stones.data.external.LegendExternalSource
import java.nio.file.Files
import java.nio.file.Path
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class LegendRepositoryImplTest {

    private lateinit var tempDir: Path
    private lateinit var cache: LegendLocalDataSourceImpl

    @BeforeTest
    fun setup() {
        tempDir = Files.createTempDirectory("legend-cache-test")
        cache = LegendLocalDataSourceImpl(cacheDir = tempDir)
    }

    @AfterTest
    fun cleanup() {
        Files.walk(tempDir).sorted(Comparator.reverseOrder()).forEach { Files.deleteIfExists(it) }
        clearAllMocks()
    }

    @Test
    fun `segundo pedido del mismo prompt sale de memoria sin llamar al source`() = runTest {
        val source = mockk<LegendExternalSource> {
            coEvery { generate(any()) } returns "una leyenda epica"
        }
        val repo = LegendRepositoryImpl(source, cache)

        repo.getLegendForPrompt("mismo prompt")
        val second = repo.getLegendForPrompt("mismo prompt")

        coVerify(exactly = 1) { source.generate("mismo prompt") }
        assertEquals("una leyenda epica", second)
    }

    @Test
    fun `un repo nuevo con memoria vacia recupera del disco sin llamar al source`() = runTest {
        val sourceA = mockk<LegendExternalSource> {
            coEvery { generate(any()) } returns "persistida en disco"
        }
        LegendRepositoryImpl(sourceA, cache).getLegendForPrompt("persistente")

        val sourceB = mockk<LegendExternalSource> {
            coEvery { generate(any()) } returns "otra distinta"
        }
        val resultB = LegendRepositoryImpl(sourceB, cache).getLegendForPrompt("persistente")

        coVerify(exactly = 0) { sourceB.generate(any()) }
        assertEquals("persistida en disco", resultB)
    }

    @Test
    fun `no cachea fallos del source`() = runTest {
        val source = mockk<LegendExternalSource> {
            coEvery { generate(any()) } returns null
        }
        val repo = LegendRepositoryImpl(source, cache)

        assertNull(repo.getLegendForPrompt("falla"))
        assertNull(repo.getLegendForPrompt("falla"))

        coVerify(exactly = 2) { source.generate("falla") }
    }
}
