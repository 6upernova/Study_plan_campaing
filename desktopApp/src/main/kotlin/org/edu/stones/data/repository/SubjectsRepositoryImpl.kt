package org.edu.stones.data.repository

import org.edu.stones.data.external.SubjectDetailExternalSource
import org.edu.stones.data.local.subjects.SubjectLocalDataSource
import org.jgrapht.alg.TransitiveReduction
import org.edu.stones.data.local.subjects.CacheConstants
import org.edu.stones.data.local.mapper.GraphCacheMapper.toCachedGraph
import org.edu.stones.data.local.mapper.GraphCacheMapper.toDomainGraph
import org.edu.stones.domain.entity.Subject
import org.edu.stones.domain.repository.SubjectRepository
import org.jgrapht.graph.DefaultDirectedGraph
import org.jgrapht.graph.DefaultEdge

class SubjectsRepositoryImpl(
    private val externalSource: SubjectDetailExternalSource,
    private val localDataSource: SubjectLocalDataSource
) : SubjectRepository {

    override suspend fun getAllSubjects(): DefaultDirectedGraph<Subject, DefaultEdge> {
        val cachedGraph = localDataSource.getGraph().getOrNull()
        val isCacheValid = localDataSource.isCacheValid().getOrElse { false }

        if (cachedGraph != null && isCacheValid) {
            println("Cache hit - returning cached graph")
            return cachedGraph.toDomainGraph()
        }

        println("Cache miss - fetching from remote source")
        val graph = forceRefresh()

        return graph
    }

    internal fun buildGraph(subjectsList: List<Subject>): DefaultDirectedGraph<Subject, DefaultEdge> {
        val graph = DefaultDirectedGraph<Subject, DefaultEdge>(DefaultEdge::class.java)

        val subjectMap = subjectsList.associateBy { it.codigo }

        subjectsList.forEach { graph.addVertex(it) }

        val edgesToAdd = mutableListOf<Pair<Subject, Subject>>()

        subjectsList.forEach { subject ->
            val codesAprobadas = subject.correlativasAprobadas
                ?.split(',')
                ?.map { it.trim() }
                ?.filter { it.isNotEmpty() }
                ?: emptyList()

            val codesCursadas = subject.correlativasCursadas
                ?.split(',')
                ?.map { it.trim() }
                ?.filter { it.isNotEmpty() }
                ?: emptyList()

            val allCodes = (codesAprobadas + codesCursadas).distinct()

            for (code in allCodes) {
                val prerequisiteSubject = subjectMap[code]
                if (prerequisiteSubject != null) {
                    edgesToAdd.add(prerequisiteSubject to subject)
                }
            }
        }
        val edgesWithoutTransitive = edgesToAdd.toList()

        edgesWithoutTransitive.forEach { (source, target) ->
            graph.addEdge(source, target)
        }

        TransitiveReduction.INSTANCE.reduce(graph)

        println("Graph built with ${graph.vertexSet()} vertices and ${graph.edgeSet()} edges")
        return graph
    }

    override suspend fun getSubjectDetail(id: String): Subject? {
        return externalSource.getSubjectsByCareer("ISI").firstOrNull { it.codigo == id }
    }

    suspend fun forceRefresh(): DefaultDirectedGraph<Subject, DefaultEdge> {
        localDataSource.invalidateCache()
        val subjectsList = externalSource.getSubjectsByCareer("ISI")
        val graph = buildGraph(subjectsList)

        val cachedGraphToSave = graph.toCachedGraph("ISI", CacheConstants.DEFAULT_VERSION)
        localDataSource.saveGraph(cachedGraphToSave)

        return graph
    }
}
