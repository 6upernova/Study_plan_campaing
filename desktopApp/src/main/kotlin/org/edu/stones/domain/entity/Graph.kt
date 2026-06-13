package org.edu.stones.domain.entity

class Graph<T> private constructor(
    private val adjacency: Map<T, Set<T>>
) {
    constructor() : this(emptyMap())

    val nodes: Set<T> get() = adjacency.keys + adjacency.values.flatten().toSet()

    fun neighbors(node: T): Set<T> = adjacency[node].orEmpty()

    fun contains(node: T): Boolean = nodes.contains(node)

    fun plusNode(node: T): Graph<T> =
        if (adjacency.containsKey(node)) this
        else Graph(adjacency + (node to emptySet()))

    fun plusEdge(from: T, to: T): Graph<T> {
        val fromSet = adjacency[from].orEmpty()
        val newFromSet = fromSet + to
        val newAdjacency = adjacency + (from to newFromSet) + (to to adjacency[to].orEmpty())
        return Graph(newAdjacency)
    }

    companion object {
        fun <T> from(nodes: Iterable<T>, edges: Iterable<Pair<T, T>>): Graph<T> {
            val map = mutableMapOf<T, MutableSet<T>>()
            for (n in nodes) map.getOrPut(n) { mutableSetOf() }
            for ((f, t) in edges) {
                map.getOrPut(f) { mutableSetOf() }.add(t)
                map.getOrPut(t) { mutableSetOf() }
            }
            return Graph(map.mapValues { it.value.toSet() })
        }

        fun <T> ofEdges(edges: Iterable<Pair<T, T>>): Graph<T> {
            val map = mutableMapOf<T, MutableSet<T>>()
            for ((f, t) in edges) {
                map.getOrPut(f) { mutableSetOf() }.add(t)
                map.getOrPut(t) { mutableSetOf() }
            }
            return Graph(map.mapValues { it.value.toSet() })
        }
    }
}

