package org.edu.stones.data.repository

import org.edu.stones.data.external.broker.SubjectsBroker
import org.edu.stones.data.local.SubjectLocalDataSource
import org.edu.stones.data.local.graph.CacheConstants
import org.edu.stones.data.local.mapper.GraphCacheMapper.toCachedGraph
import org.edu.stones.data.local.mapper.GraphCacheMapper.toDomainGraph
import org.edu.stones.domain.entity.Subject
import org.edu.stones.domain.repository.SubjectRepository
import org.jgrapht.graph.DefaultDirectedGraph
import org.jgrapht.graph.DefaultEdge

class SubjectsRepositoryImpl(
    private val subjectsBroker: SubjectsBroker,
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
        val subjectsList = subjectsBroker.getSubjectsByCareer("ISI")
        val graph = buildGraph(subjectsList)

        val cachedGraphToSave = graph.toCachedGraph("ISI", CacheConstants.DEFAULT_VERSION)
        localDataSource.saveGraph(cachedGraphToSave)

        return graph
    }

    private fun buildGraph(subjectsList: List<Subject>): DefaultDirectedGraph<Subject, DefaultEdge> {
        val graph = DefaultDirectedGraph<Subject, DefaultEdge>(DefaultEdge::class.java)

        val subjectMap = subjectsList.associateBy { it.codigo }

        subjectsList.forEach { graph.addVertex(it) }

        val edgesToAdd = mutableListOf<Pair<Subject, Subject>>()

        subjectsList.forEach { subject ->
            val prerequisitesCodes = subject.correlativasAprobadas
                ?.split(',')
                ?.map { it.trim() }
                ?.filter { it.isNotEmpty() }
                ?: emptyList()

            for (code in prerequisitesCodes) {
                val prerequisiteSubject = subjectMap[code]
                if (prerequisiteSubject != null) {
                    edgesToAdd.add(prerequisiteSubject to subject)
                }
            }
        }

        val edgesWithoutTransitive = removeTransitiveEdges(graph, edgesToAdd)

        edgesWithoutTransitive.forEach { (source, target) ->
            graph.addEdge(source, target)
        }

        println("Graph built with ${graph.vertexSet().size} vertices and ${graph.edgeSet().size} edges")
        return graph
    }

    private fun removeTransitiveEdges(
        graph: DefaultDirectedGraph<Subject, DefaultEdge>,
        edges: List<Pair<Subject, Subject>>
    ): List<Pair<Subject, Subject>> {
        val subjectList = graph.vertexSet().toList()
        val adjacencyMap = mutableMapOf<Subject, MutableList<Subject>>()

        subjectList.forEach { adjacencyMap[it] = mutableListOf() }

        edges.forEach { (source, target) ->
            adjacencyMap[source]?.add(target)
        }

        val edgesToKeep = mutableListOf<Pair<Subject, Subject>>()

        for ((source, target) in edges) {
            val reachableFromSource = bfsReachable(source, adjacencyMap, excludeTarget = target)

            if (!reachableFromSource.contains(target)) {
                edgesToKeep.add(source to target)
            } else {
                println("Removing transitive edge: ${source.codigo} -> ${target.codigo}")
            }
        }

        return edgesToKeep
    }

    private fun bfsReachable(
        start: Subject,
        adjacencyMap: Map<Subject, List<Subject>>,
        excludeTarget: Subject? = null
    ): Set<Subject> {
        val visited = mutableSetOf<Subject>()
        val queue = ArrayDeque<Subject>()
        queue.add(start)
        visited.add(start)

        while (queue.isNotEmpty()) {
            val current = queue.removeFirst()
            val neighbors = adjacencyMap[current] ?: emptyList()

            for (neighbor in neighbors) {
                if (neighbor == excludeTarget) continue
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor)
                    queue.add(neighbor)
                }
            }
        }

        return visited
    }

    override suspend fun getSubjectDetail(id: String): Subject? {
        return subjectsBroker.getSubjectsByCareer("ISI").firstOrNull { it.codigo == id }
    }

    suspend fun forceRefresh(): DefaultDirectedGraph<Subject, DefaultEdge> {
        localDataSource.invalidateCache()
        val subjectsList = subjectsBroker.getSubjectsByCareer("ISI")
        val graph = buildGraph(subjectsList)

        val cachedGraphToSave = graph.toCachedGraph("ISI", CacheConstants.DEFAULT_VERSION)
        localDataSource.saveGraph(cachedGraphToSave)

        return graph
    }
}