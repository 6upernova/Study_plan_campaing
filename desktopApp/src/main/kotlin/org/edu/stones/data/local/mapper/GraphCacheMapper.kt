package org.edu.stones.data.local.mapper

import org.edu.stones.data.local.graph.CachedEdge
import org.edu.stones.data.local.graph.CachedGraph
import org.edu.stones.data.local.graph.CachedVertex
import org.edu.stones.domain.entity.Subject
import org.jgrapht.graph.DefaultDirectedGraph
import org.jgrapht.graph.DefaultEdge

object GraphCacheMapper {

    fun Subject.toCachedVertex(): CachedVertex = CachedVertex(
        codigo = codigo,
        nombre = nombre,
        anio = anio,
        periodo = periodo,
        anioDeRecopilacion = anioDeRecopilacion,
        notasPromedio = notasPromedio,
        inscriptos = inscriptos,
        presencialidad = presencialidad,
        cantAprobados = cantAprobados,
        correlativasCursadas = correlativasCursadas,
        correlativasAprobadas = correlativasAprobadas,
        abreviatura = abreviatura
    )

    fun CachedVertex.toDomain(): Subject = Subject(
        codigo = codigo,
        nombre = nombre,
        anio = anio,
        periodo = periodo,
        anioDeRecopilacion = anioDeRecopilacion,
        notasPromedio = notasPromedio,
        inscriptos = inscriptos,
        presencialidad = presencialidad,
        cantAprobados = cantAprobados,
        correlativasCursadas = correlativasCursadas,
        correlativasAprobadas = correlativasAprobadas,
        abreviatura = abreviatura
    )

    fun DefaultDirectedGraph<Subject, DefaultEdge>.toCachedGraph(careerId: String, version: String): CachedGraph {
        val vertices = this.vertexSet().map { it.toCachedVertex() }
        val edges = this.edgeSet().map { edge ->
            val source = this.getEdgeSource(edge)
            val target = this.getEdgeTarget(edge)
            CachedEdge(sourceCode = source.codigo, targetCode = target.codigo)
        }
        return CachedGraph(
            vertices = vertices,
            edges = edges,
            careerId = careerId,
            cachedAt = System.currentTimeMillis(),
            version = version
        )
    }

    fun CachedGraph.toDomainGraph(): DefaultDirectedGraph<Subject, DefaultEdge> {
        val graph = DefaultDirectedGraph<Subject, DefaultEdge>(DefaultEdge::class.java)

        val vertexMap = vertices.associateBy({ it.codigo }, { it.toDomain() })

        vertexMap.values.forEach { graph.addVertex(it) }

        edges.forEach { edge ->
            val source = vertexMap[edge.sourceCode]
            val target = vertexMap[edge.targetCode]
            if (source != null && target != null) {
                graph.addEdge(source, target)
            }
        }

        return graph
    }
}