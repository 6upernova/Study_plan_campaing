package org.edu.stones.data.local

import kotlinx.coroutines.test.runTest
import org.edu.stones.data.local.subjects.CacheConstants
import org.edu.stones.data.local.subjects.CachedEdge
import org.edu.stones.data.local.subjects.CachedGraph
import org.edu.stones.data.local.subjects.CachedVertex
import org.edu.stones.data.local.subjects.SubjectLocalDataSourceImpl
import java.io.File
import java.nio.file.Files
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class SubjectLocalDataSourceImplTest {

    private lateinit var baseDir: File
    private lateinit var dataSource: SubjectLocalDataSourceImpl

    @BeforeTest
    fun setup() {
        baseDir = Files.createTempDirectory("local-cache-test").toFile()
        dataSource = SubjectLocalDataSourceImpl(baseDir = baseDir)
    }

    @AfterTest
    fun cleanup() {
        baseDir.walkBottomUp().forEach { it.delete() }
    }

    private fun graph(cachedAt: Long = System.currentTimeMillis()) = CachedGraph(
        vertices = listOf(
            CachedVertex(
                codigo = "A", nombre = "Algebra", anio = 1, periodo = "Primer Cuatrimestre",
                anioDeRecopilacion = 2024, notasPromedio = 7.0, inscriptos = 100,
                presencialidad = "Presencial", cantAprobados = 50,
                correlativasCursadas = "", correlativasAprobadas = "", abreviatura = "ALG"
            )
        ),
        edges = listOf(CachedEdge("A", "A")),
        careerId = "ISI",
        cachedAt = cachedAt,
        version = CacheConstants.DEFAULT_VERSION,
    )

    @Test
    fun `saveGraph y getGraph hacen round-trip`() = runTest {
        val original = graph()
        dataSource.saveGraph(original).getOrThrow()

        val restored = dataSource.getGraph().getOrThrow()
        assertEquals(original, restored)
    }

    @Test
    fun `getGraph devuelve null si no hay cache`() = runTest {
        assertNull(dataSource.getGraph().getOrThrow())
    }

    @Test
    fun `isCacheValid es false sin cache`() = runTest {
        assertFalse(dataSource.isCacheValid().getOrThrow())
    }

    @Test
    fun `isCacheValid es true para cache fresco e integro`() = runTest {
        dataSource.saveGraph(graph()).getOrThrow()
        assertTrue(dataSource.isCacheValid().getOrThrow())
    }

    @Test
    fun `isCacheValid es false cuando el cache esta expirado`() = runTest {
        val viejo = System.currentTimeMillis() - CacheConstants.CACHE_VALIDITY_MS - 1000
        dataSource.saveGraph(graph(cachedAt = viejo)).getOrThrow()
        assertFalse(dataSource.isCacheValid().getOrThrow())
    }

    @Test
    fun `isCacheValid es false cuando el contenido fue modificado (hash no coincide)`() = runTest {
        dataSource.saveGraph(graph()).getOrThrow()

        val cacheFile = File(File(baseDir, ".cache"), "graph_cache.json")
        cacheFile.writeText(cacheFile.readText() + "\n// tampered")

        assertFalse(dataSource.isCacheValid().getOrThrow())
    }

    @Test
    fun `invalidateCache borra los archivos y deja el cache invalido`() = runTest {
        dataSource.saveGraph(graph()).getOrThrow()
        dataSource.invalidateCache().getOrThrow()

        assertNull(dataSource.getGraph().getOrThrow())
        assertFalse(dataSource.isCacheValid().getOrThrow())
    }
}
