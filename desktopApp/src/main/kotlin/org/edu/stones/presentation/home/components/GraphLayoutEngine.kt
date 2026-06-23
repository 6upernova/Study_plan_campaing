package org.edu.stones.presentation.home.components

import org.edu.stones.domain.entity.Subject
import org.edu.stones.presentation.home.config.GraphConfig
import org.edu.stones.presentation.home.config.GraphConfigDefaults
import org.jgrapht.graph.DefaultDirectedGraph
import org.jgrapht.graph.DefaultEdge
import org.jgrapht.traverse.TopologicalOrderIterator
import androidx.compose.ui.geometry.Offset
import kotlin.math.abs
import kotlin.math.min
import kotlin.math.max
import kotlin.math.roundToInt

object GraphLayoutEngine {

    fun getDefaultSpriteSize(config: GraphConfig = GraphConfigDefaults.Default): Float = 120f

    fun computeLayout(
        graph: DefaultDirectedGraph<Subject, DefaultEdge>,
        availableWidth: Float,
        availableHeight: Float,
        density: Float,
        config: GraphConfig = GraphConfigDefaults.Default
    ): GraphLayoutData {
        if (graph.vertexSet().isEmpty()) return GraphLayoutData(emptyList(), emptyList())

        val subjectList = graph.vertexSet().toList()
        val subjectSet = subjectList.toSet()

        val layerMap = assignLayers(graph, subjectList, config)
        val dummyVertices = mutableListOf<DummyVertex>()
        val edgeChains = insertDummyVertices(graph, subjectList, layerMap, dummyVertices)

        val allLayerAssignments = mutableMapOf<Any, Int>()
        subjectList.forEach { allLayerAssignments[it] = layerMap[it]!! }
        dummyVertices.forEach { allLayerAssignments[it] = it.layer }

        val maxLayer = allLayerAssignments.values.maxOrNull() ?: 0
        val layers: MutableMap<Int, MutableList<Any>> = (0..maxLayer).associateWith { mutableListOf<Any>() }.toMutableMap()
        allLayerAssignments.forEach { (vertex, layer) -> layers[layer]!!.add(vertex) }

        val allEdges = mutableListOf<AnyEdge>()
        subjectList.forEach { u ->
            subjectList.forEach { v ->
                if (graph.containsEdge(u, v)) {
                    allEdges.add(AnyEdge(u, v))
                }
            }
        }
        edgeChains.forEach { chain ->
            for (i in 0 until chain.size - 1) {
                allEdges.add(AnyEdge(chain[i], chain[i + 1]))
            }
        }

        val successorMap = mutableMapOf<Any, MutableList<Any>>()
        val predecessorMap = mutableMapOf<Any, MutableList<Any>>()
        allEdges.forEach { edge ->
            successorMap.getOrPut(edge.source) { mutableListOf() }.add(edge.target)
            predecessorMap.getOrPut(edge.target) { mutableListOf() }.add(edge.source)
        }

        minimizeCrossings(layers, allEdges, successorMap, predecessorMap, config)

        val finalNodeMap = mutableMapOf<String, GraphNode>()
        val nodePositions = mutableMapOf<Any, Offset>()

        val layerHeights = layers.mapValues { (_, vertices) -> vertices.size }
        val maxLayerHeight = layerHeights.values.maxOrNull() ?: 1

        val layerSpacing = (availableWidth * config.layerSpacingPercent)
            .coerceIn(config.strokeWidthMinDp * density * 90f, config.strokeWidthMaxDp * density * 40f)
        val baseRowSpacing = availableHeight * config.baseRowSpacingPercent
        val minVerticalSpacing = max(availableHeight * config.minVerticalSpacingPercent, config.strokeWidthMinDp * density * 40f)
        val paddingLeft = availableWidth * config.paddingLeftPercent
        val paddingTop = availableHeight * config.paddingTopPercent
        val bundleSpacing = max(layerSpacing * config.bundleSpacingPercent, config.strokeWidthMinDp * density * 4f)

        val rowSpacing = if (maxLayerHeight > 1) {
            max(minVerticalSpacing, (availableHeight - 2 * paddingTop) / maxLayerHeight)
        } else {
            baseRowSpacing
        }

        val spriteSize = (rowSpacing * config.spriteSizeFactor).coerceIn(
            availableHeight * config.spriteSizeMinPercent * density,
            availableHeight * config.spriteSizeMaxPercent * density
        )

        val strokeWidth = (spriteSize * config.strokeWidthFactor).coerceIn(
            config.strokeWidthMinDp * density,
            config.strokeWidthMaxDp * density
        )

        val fontSize = (spriteSize * config.fontSizeFactor).coerceIn(
            config.fontSizeMinDp * density,
            config.fontSizeMaxDp * density
        )

        layers.forEach { (layer, vertices) ->
            val x = paddingLeft + layer * layerSpacing
            val yOffset = ((maxLayerHeight - vertices.size) * rowSpacing) / 2f
            vertices.forEachIndexed { pos, vertex ->
                val y = paddingTop + yOffset + pos * rowSpacing
                nodePositions[vertex] = Offset(x, y)
                if (vertex is Subject) {
                    val period = if (vertex.periodo == "Primer Cuatrimestre") 1 else 2
                    finalNodeMap[vertex.codigo] = GraphNode(
                        subject = vertex,
                        year = vertex.anio,
                        semester = period,
                        layer = layer,
                        positionInLayer = pos,
                        x = x,
                        y = y,
                        spriteSize = spriteSize,
                        strokeWidth = strokeWidth,
                        fontSize = fontSize
                    )
                }
            }
        }

        val edgesList = mutableListOf<GraphEdge>()
        subjectList.forEach { u ->
            subjectList.forEach { v ->
                if (graph.containsEdge(u, v)) {
                    val nodeU = finalNodeMap[u.codigo] ?: return@forEach
                    val nodeV = finalNodeMap[v.codigo] ?: return@forEach

                    if (layerMap[v]!! - layerMap[u]!! <= 1) {
                        edgesList.add(makeDirectEdge(nodeU, nodeV))
                    } else {
                        val chain = edgeChains.firstOrNull { chain ->
                            chain.first() == u && chain.last() == v
                        }
                        if (chain != null) {
                            val waypoints = mutableListOf<Offset>()
                            for (i in 1 until chain.size - 1) {
                                val dummy = chain[i] as DummyVertex
                                waypoints.add(nodePositions[dummy] ?: Offset(0f, 0f))
                            }
                            val edge = makeRoutingEdge(nodeU, nodeV, waypoints, bundleIndex(u, v, allEdges), bundleSpacing)
                            edgesList.add(edge)
                        } else {
                            edgesList.add(makeDirectEdge(nodeU, nodeV))
                        }
                    }
                }
            }
        }

        return GraphLayoutData(
            nodes = finalNodeMap.values.toList(),
            edges = edgesList
        )
    }

    private fun assignLayers(
        graph: DefaultDirectedGraph<Subject, DefaultEdge>,
        subjects: List<Subject>,
        config: GraphConfig
    ): Map<Subject, Int> {
        val layerBySubject = mutableMapOf<Subject, Int>()
        val inDegree = mutableMapOf<Subject, Int>()
        subjects.forEach { inDegree[it] = graph.inDegreeOf(it) }

        val topologicalOrder = mutableListOf<Subject>()
        val iterator = TopologicalOrderIterator(graph)
        iterator.forEachRemaining { topologicalOrder.add(it as Subject) }

        topologicalOrder.forEach { v ->
            val domainLayer = (v.anio - 1) * config.domainLayerPeriodWeight + (if (v.periodo == "Primer Cuatrimestre") 1 else 2) - 1
            var topologicalLayer = 0
            graph.incomingEdgesOf(v).forEach { edge ->
                val u = graph.getEdgeSource(edge) as Subject
                val uLayer = layerBySubject[u] ?: 0
                topologicalLayer = max(topologicalLayer, uLayer + 1)
            }
            layerBySubject[v] = max(topologicalLayer, domainLayer)
        }

        return layerBySubject
    }

    private data class DummyVertex(
        val id: Int,
        val layer: Int,
        val sourceSubject: Subject,
        val targetSubject: Subject
    )

    private data class AnyEdge(val source: Any, val target: Any)

    private fun insertDummyVertices(
        graph: DefaultDirectedGraph<Subject, DefaultEdge>,
        subjects: List<Subject>,
        layerMap: Map<Subject, Int>,
        dummyVertices: MutableList<DummyVertex>
    ): List<List<Any>> {
        var dummyCounter = 0
        val chains = mutableListOf<List<Any>>()

        subjects.forEach { u ->
            subjects.forEach { v ->
                if (graph.containsEdge(u, v)) {
                    val uLayer = layerMap[u] ?: 0
                    val vLayer = layerMap[v] ?: 0
                    val diff = vLayer - uLayer

                    if (diff > 1) {
                        val chain = mutableListOf<Any>(u)
                        for (l in uLayer + 1 until vLayer) {
                            val dummy = DummyVertex(--dummyCounter, l, u, v)
                            dummyVertices.add(dummy)
                            chain.add(dummy)
                        }
                        chain.add(v)
                        chains.add(chain)
                    }
                }
            }
        }

        return chains
    }

    private fun minimizeCrossings(
        layers: MutableMap<Int, MutableList<Any>>,
        edges: List<AnyEdge>,
        successors: Map<Any, List<Any>>,
        predecessors: Map<Any, List<Any>>,
        config: GraphConfig
    ) {
        var bestOrder = layers.mapValues { it.value.toList() }
        var bestCrossings = countCrossings(layers, edges)

        repeat(config.maxCrossingIterations) { iteration ->
            if (iteration % 2 == 0) {
                val sortedLayers = layers.keys.sorted()
                for (i in 1 until sortedLayers.size) {
                    val layer = sortedLayers[i]
                    layers[layer] = sortByBarycenter(layers[layer]!!, predecessors, layer - 1, layers)
                }
            } else {
                val sortedLayers = layers.keys.sortedDescending()
                for (i in 0 until sortedLayers.size - 1) {
                    val layer = sortedLayers[i]
                    layers[layer] = sortByBarycenter(layers[layer]!!, successors, layer + 1, layers)
                }
            }

            val crossings = countCrossings(layers, edges)
            if (crossings < bestCrossings) {
                bestCrossings = crossings
                bestOrder = layers.mapValues { it.value.toList() }
            }
        }

        bestOrder.forEach { (layer, vertices) ->
            layers[layer] = vertices.toMutableList()
        }
    }

    private fun sortByBarycenter(
        vertices: MutableList<Any>,
        neighborMap: Map<Any, List<Any>>,
        adjacentLayer: Int,
        layers: Map<Int, MutableList<Any>>
    ): MutableList<Any> {
        val adjPositions = mutableMapOf<Any, Int>()
        layers[adjacentLayer]?.forEachIndexed { index, v -> adjPositions[v] = index }

        return vertices.sortedBy { v ->
            val neighbors = neighborMap[v] ?: emptyList()
            val positions = neighbors.mapNotNull { adjPositions[it] }
            if (positions.isEmpty()) -1.0 else positions.average()
        }.toMutableList()
    }

    private fun countCrossings(
        layers: Map<Int, MutableList<Any>>,
        edges: List<AnyEdge>
    ): Int {
        var crossings = 0
        val positions = mutableMapOf<Any, Pair<Int, Int>>()

        layers.forEach { (layer, vertices) ->
            vertices.forEachIndexed { pos, v -> positions[v] = Pair(layer, pos) }
        }

        val sortedLayers = layers.keys.sorted()
        for (i in 0 until sortedLayers.size - 1) {
            val leftLayer = sortedLayers[i]
            val rightLayer = sortedLayers[i + 1]

            val leftVertices = layers[leftLayer] ?: continue
            val rightVertices = layers[rightLayer] ?: continue

            val leftPositions = mutableMapOf<Any, Int>()
            leftVertices.forEachIndexed { idx, v -> leftPositions[v] = idx }

            val relevantEdges = mutableListOf<Pair<Int, Int>>()
            edges.forEach { edge ->
                val sourcePos = leftPositions[edge.source]
                val targetPos = leftPositions[edge.target]
                val sourceIsLeft = leftPositions.containsKey(edge.source)
                val targetIsLeft = leftPositions.containsKey(edge.target)

                if (sourceIsLeft && !targetIsLeft) {
                    val targetLayerPos = positions[edge.target]?.second ?: return@forEach
                    val sourceIdx = sourcePos ?: return@forEach
                    relevantEdges.add(Pair(sourceIdx, targetLayerPos))
                } else if (!sourceIsLeft && targetIsLeft) {
                    val sourceLayerPos = positions[edge.source]?.second ?: return@forEach
                    val targetIdx = targetPos ?: return@forEach
                    relevantEdges.add(Pair(sourceLayerPos, targetIdx))
                } else if (sourceIsLeft && targetIsLeft) {
                }
            }

            for (a in relevantEdges.indices) {
                for (b in a + 1 until relevantEdges.size) {
                    val p1 = relevantEdges[a]
                    val p2 = relevantEdges[b]
                    if ((p1.first - p2.first) * (p1.second - p2.second) < 0) {
                        crossings++
                    }
                }
            }
        }
        return crossings
    }

    private fun makeDirectEdge(source: GraphNode, target: GraphNode): GraphEdge {
        return GraphEdge(
            fromCode = source.subject.codigo,
            toCode = target.subject.codigo,
            fromX = source.x + source.spriteSize / 2,
            fromY = source.y + source.spriteSize / 2,
            toX = target.x + target.spriteSize / 2,
            toY = target.y + target.spriteSize / 2,
            strokeWidth = source.strokeWidth
        )
    }

    private fun makeRoutingEdge(
        source: GraphNode,
        target: GraphNode,
        waypoints: List<Offset>,
        bundleIdx: Int,
        bundleSpacing: Float
    ): GraphEdge {
        val fromX = source.x + source.spriteSize / 2
        val fromY = source.y + source.spriteSize / 2
        val toX = target.x + target.spriteSize / 2
        val toY = target.y + target.spriteSize / 2

        val offset = bundleIdx * bundleSpacing
        val adjustedWaypoints = waypoints.map { wp ->
            Offset(wp.x + offset, wp.y)
        }

        return GraphEdge(
            fromCode = source.subject.codigo,
            toCode = target.subject.codigo,
            fromX = fromX,
            fromY = fromY,
            toX = toX,
            toY = toY,
            waypoints = adjustedWaypoints,
            strokeWidth = source.strokeWidth
        )
    }

    private fun bundleIndex(from: Subject, to: Subject, allEdges: List<AnyEdge>): Int {
        val targets = allEdges.filter { it.target == to }
        val index = targets.indexOfFirst { it.source == from }
        return index - (targets.size / 2)
    }
}
