package org.edu.stones.data.local

import org.edu.stones.data.local.graph.CachedGraph

interface SubjectLocalDataSource {
    suspend fun saveGraph(graph: CachedGraph): Result<Unit>
    suspend fun getGraph(): Result<CachedGraph?>
    suspend fun isCacheValid(): Result<Boolean>
    suspend fun invalidateCache(): Result<Unit>
    suspend fun getCacheMetadata(): Result<CachedGraph?>
}