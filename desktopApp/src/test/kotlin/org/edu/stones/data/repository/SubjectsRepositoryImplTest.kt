package org.edu.stones.data.repository

import org.edu.stones.data.external.SubjectDetailExternalSource
import org.edu.stones.data.local.subjects.SubjectLocalDataSource
import org.edu.stones.data.local.subjects.CachedGraph
import org.edu.stones.domain.entity.Subject
import org.edu.stones.subject
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

private class FakeLocalDataSource : SubjectLocalDataSource {
    override suspend fun saveGraph(graph: CachedGraph): Result<Unit> = Result.success(Unit)
    override suspend fun getGraph(): Result<CachedGraph?> = Result.success(null)
    override suspend fun isCacheValid(): Result<Boolean> = Result.success(false)
    override suspend fun invalidateCache(): Result<Unit> = Result.success(Unit)
    override suspend fun getCacheMetadata(): Result<CachedGraph?> = Result.success(null)
}

private val emptySource = object : SubjectDetailExternalSource {
    override suspend fun getSubjectsByCareer(careerId: String): List<Subject> = emptyList()
}

class SubjectsRepositoryImplTest {

    private fun repo(): SubjectsRepositoryImpl =
        SubjectsRepositoryImpl(emptySource, FakeLocalDataSource())

    private fun edgesOf(subjects: List<Subject>): Set<Pair<String, String>> {
        val graph = repo().buildGraph(subjects)
        return graph.edgeSet().map {
            graph.getEdgeSource(it).codigo to graph.getEdgeTarget(it).codigo
        }.toSet()
    }

    @Test
    fun `crea aristas desde correlativas cursadas y aprobadas`() {
        val a = subject(codigo = "A")
        val b = subject(codigo = "B")
        val c = subject(codigo = "C", correlativasCursadas = "A", correlativasAprobadas = "B")

        assertEquals(setOf("A" to "C", "B" to "C"), edgesOf(listOf(a, b, c)))
    }

    @Test
    fun `parsea lista separada por comas con espacios y vacios`() {
        val a = subject(codigo = "A")
        val b = subject(codigo = "B")
        val c = subject(codigo = "C", correlativasAprobadas = " A ,, B , ")

        assertEquals(setOf("A" to "C", "B" to "C"), edgesOf(listOf(a, b, c)))
    }

    @Test
    fun `deduplica correlativa presente en cursadas y aprobadas`() {
        val a = subject(codigo = "A")
        val c = subject(codigo = "C", correlativasCursadas = "A", correlativasAprobadas = "A")

        val graph = repo().buildGraph(listOf(a, c))
        assertEquals(1, graph.edgeSet().size)
    }

    @Test
    fun `ignora codigos de correlativas que no existen en la lista`() {
        val c = subject(codigo = "C", correlativasAprobadas = "NO_EXISTE")
        assertTrue(edgesOf(listOf(c)).isEmpty())
    }

    @Test
    fun `correlativas nulas no generan aristas`() {
        val a = subject(codigo = "A", correlativasCursadas = null, correlativasAprobadas = null)
        val graph = repo().buildGraph(listOf(a))
        assertEquals(1, graph.vertexSet().size)
        assertTrue(graph.edgeSet().isEmpty())
    }
}
