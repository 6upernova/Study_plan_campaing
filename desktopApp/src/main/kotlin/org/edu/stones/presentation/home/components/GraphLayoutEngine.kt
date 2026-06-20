package org.edu.stones.presentation.home.components

import org.edu.stones.domain.entity.Subject
import org.jgrapht.graph.DefaultDirectedGraph
import org.jgrapht.graph.DefaultEdge

object GraphLayoutEngine {

    private const val NODE_WIDTH = 130f
    private const val NODE_HEIGHT = 65f
    private const val COLUMN_SPACING = 190f
    private const val ROW_SPACING = 110f
    private const val PADDING_LEFT = 80f
    private const val PADDING_TOP = 120f
    private const val NOISE_SCALE_X = 8f
    private const val NOISE_SCALE_Y = 6f

    fun computeLayout(graph: DefaultDirectedGraph<Subject, DefaultEdge>): GraphLayoutData {
        val nodesMap = mutableMapOf<String, GraphNode>()
        val edges = mutableListOf<GraphEdge>()

        val groupedByColumn = graph.vertexSet()
            .groupBy { subject -> (subject.anio - 1) * 2 + determinePeriod(subject.periodo) }
            .entries
            .sortedBy { it.key }

        val columnPositions = mutableMapOf<Int, Float>()

        groupedByColumn.forEachIndexed { index, entry ->
            columnPositions[entry.key] = PADDING_LEFT + index * COLUMN_SPACING
        }

        groupedByColumn.forEach { entry ->
            val column = entry.key
            val subjects = entry.value
            val x = columnPositions[column] ?: PADDING_LEFT

            val sortedSubjects = if (subjects.size > 1) {
                sortByPrerequisites(subjects, graph)
            } else {
                subjects
            }

            sortedSubjects.forEachIndexed { posInColumn, subject ->
                val gridY = PADDING_TOP + posInColumn * ROW_SPACING

                val seed = subject.codigo.hashCode()
                val noiseX = ((seed * 7 + 13) % 17 - 8) * NOISE_SCALE_X / 16f
                val noiseY = ((seed * 11 + 17) % 13 - 6) * NOISE_SCALE_Y / 12f

                nodesMap[subject.codigo] = GraphNode(
                    subject = subject,
                    year = subject.anio,
                    semester = determinePeriod(subject.periodo),
                    column = column,
                    positionInColumn = posInColumn,
                    gridX = x,
                    gridY = gridY,
                    noiseX = noiseX,
                    noiseY = noiseY
                )
            }
        }

        graph.edgeSet().forEach { edge ->
            val source = graph.getEdgeSource(edge) as Subject
            val target = graph.getEdgeTarget(edge) as Subject

            val sourceNode = nodesMap[source.codigo] ?: return@forEach
            val targetNode = nodesMap[target.codigo] ?: return@forEach

            val fromX = sourceNode.x + NODE_WIDTH / 2
            val fromY = sourceNode.y + NODE_HEIGHT
            val toX = targetNode.x + NODE_WIDTH / 2
            val toY = targetNode.y

            edges.add(
                GraphEdge(
                    fromCode = source.codigo,
                    toCode = target.codigo,
                    fromX = fromX,
                    fromY = fromY,
                    toX = toX,
                    toY = toY
                )
            )
        }

        return GraphLayoutData(
            nodes = nodesMap.values.toList(),
            edges = edges
        )
    }

    fun getNodeWidth(): Float = NODE_WIDTH
    fun getNodeHeight(): Float = NODE_HEIGHT

    private fun determinePeriod(periodo: String): Int {
        return if (periodo == "Primer Cuatrimestre") 1 else 2
    }

    private fun sortByPrerequisites(
        subjects: List<Subject>,
        graph: DefaultDirectedGraph<Subject, DefaultEdge>
    ): List<Subject> {
        val inDegreeMap = mutableMapOf<Subject, Int>()
        subjects.forEach { subject ->
            inDegreeMap[subject] = graph.inDegreeOf(subject)
        }
        return subjects.sortedBy { inDegreeMap[it] ?: 0 }
    }
}