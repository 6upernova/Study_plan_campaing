package org.edu.stones.data.local.mapper

import org.edu.stones.data.local.graph.CachedEdge
import org.edu.stones.data.local.graph.CachedGraph
import org.edu.stones.data.local.graph.CachedVertex
import org.edu.stones.data.local.mapper.GraphCacheMapper.toCachedGraph
import org.edu.stones.data.local.mapper.GraphCacheMapper.toCachedVertex
import org.edu.stones.data.local.mapper.GraphCacheMapper.toDomain
import org.edu.stones.data.local.mapper.GraphCacheMapper.toDomainGraph
import org.edu.stones.subject
import org.jgrapht.graph.DefaultDirectedGraph
import org.jgrapht.graph.DefaultEdge
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GraphCacheMapperTest {

    @Test
    fun `round-trip de grafo preserva vertices y aristas`() {
        val a = subject(codigo = "A")
        val b = subject(codigo = "B")
        val c = subject(codigo = "C")

        val graph = DefaultDirectedGraph<org.edu.stones.domain.entity.Subject, DefaultEdge>(DefaultEdge::class.java)
        listOf(a, b, c).forEach { graph.addVertex(it) }
        graph.addEdge(a, b)
        graph.addEdge(b, c)

        val restored = graph.toCachedGraph("ISI", "1.0").toDomainGraph()

        assertEquals(graph.vertexSet().map { it.codigo }.toSet(), restored.vertexSet().map { it.codigo }.toSet())
        assertEquals(graph.edgeSet().size, restored.edgeSet().size)

        val restoredEdges = restored.edgeSet().map {
            restored.getEdgeSource(it).codigo to restored.getEdgeTarget(it).codigo
        }.toSet()
        assertEquals(setOf("A" to "B", "B" to "C"), restoredEdges)
    }

    @Test
    fun `aristas con codigos inexistentes se ignoran al reconstruir`() {
        val cached = CachedGraph(
            vertices = listOf(subject(codigo = "A").toCachedVertex()),
            edges = listOf(CachedEdge(sourceCode = "A", targetCode = "NO_EXISTE")),
            careerId = "ISI",
            cachedAt = 0L,
            version = "1.0",
        )

        val graph = cached.toDomainGraph()

        assertEquals(1, graph.vertexSet().size)
        assertTrue(graph.edgeSet().isEmpty())
    }

    @Test
    fun `mapea todos los campos incluyendo correlativas nulas`() {
        val original = subject(
            codigo = "MAT9",
            correlativasCursadas = null,
            correlativasAprobadas = "A, B",
            abreviatura = "",
        )

        val restored: org.edu.stones.domain.entity.Subject = original.toCachedVertex().toDomain()

        assertEquals(original, restored)
    }

    @Test
    fun `grafo vacio produce cache vacio y se reconstruye vacio`() {
        val empty = DefaultDirectedGraph<org.edu.stones.domain.entity.Subject, DefaultEdge>(DefaultEdge::class.java)
        val restored = empty.toCachedGraph("ISI", "1.0").toDomainGraph()
        assertTrue(restored.vertexSet().isEmpty())
        assertTrue(restored.edgeSet().isEmpty())
    }

    @Test
    fun `toCachedVertex copia los campos del subject`() {
        val s = subject(codigo = "X", nombre = "Algebra", anio = 3)
        val v: CachedVertex = s.toCachedVertex()
        assertEquals(s.codigo, v.codigo)
        assertEquals(s.nombre, v.nombre)
        assertEquals(s.anio, v.anio)
    }
}
