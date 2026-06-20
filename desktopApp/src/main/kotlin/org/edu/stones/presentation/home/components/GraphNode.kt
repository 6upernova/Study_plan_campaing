package org.edu.stones.presentation.home.components

import org.edu.stones.domain.entity.Subject

data class GraphNode(
    val subject: Subject,
    val year: Int,
    val semester: Int,
    val column: Int,
    val positionInColumn: Int,
    val gridX: Float,
    val gridY: Float,
    val noiseX: Float,
    val noiseY: Float
) {
    val x: Float get() = gridX + noiseX
    val y: Float get() = gridY + noiseY
}

data class GraphEdge(
    val fromCode: String,
    val toCode: String,
    val fromX: Float,
    val fromY: Float,
    val toX: Float,
    val toY: Float
)

data class GraphLayoutData(
    val nodes: List<GraphNode>,
    val edges: List<GraphEdge>
)