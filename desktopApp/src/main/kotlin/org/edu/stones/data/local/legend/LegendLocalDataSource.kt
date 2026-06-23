package org.edu.stones.data.local.legend

import kotlin.Result

interface LegendLocalDataSource {
    suspend fun get(key: String): Result<String?>
    suspend fun put(key: String, legend: String): Result<Unit>
    suspend fun cacheKey(prompt: String): String
}
