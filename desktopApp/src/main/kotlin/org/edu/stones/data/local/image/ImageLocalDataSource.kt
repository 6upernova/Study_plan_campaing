package org.edu.stones.data.local.image

import kotlin.Result

interface ImageLocalDataSource {
    suspend fun get(key: String): Result<ByteArray?>
    suspend fun put(key: String, bytes: ByteArray): Result<Unit>
    suspend fun cacheKey(prompt: String): String
}
