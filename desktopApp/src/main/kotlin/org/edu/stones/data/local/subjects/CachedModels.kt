package org.edu.stones.data.local.subjects

import kotlinx.serialization.Serializable

@Serializable
data class CachedVertex(
    val codigo: String,
    val nombre: String,
    val anio: Int,
    val periodo: String,
    val anioDeRecopilacion: Int,
    val notasPromedio: Double,
    val inscriptos: Int,
    val presencialidad: String,
    val cantAprobados: Int,
    val correlativasCursadas: String,
    val correlativasAprobadas: String,
    val abreviatura: String
)

@Serializable
data class CachedEdge(
    val sourceCode: String,
    val targetCode: String
)

@Serializable
data class CachedGraph(
    val vertices: List<CachedVertex>,
    val edges: List<CachedEdge>,
    val careerId: String,
    val cachedAt: Long,
    val version: String
)

data class CacheMetadata(
    val careerId: String,
    val cachedAt: Long,
    val version: String,
    val etag: String? = null
)

object CacheConstants {
    const val CACHE_VALIDITY_MS = 24 * 60 * 60 * 1000L
    const val DEFAULT_VERSION = "1.0"
}
