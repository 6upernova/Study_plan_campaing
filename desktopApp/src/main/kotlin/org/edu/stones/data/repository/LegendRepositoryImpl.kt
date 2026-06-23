package org.edu.stones.data.repository

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.edu.stones.data.local.legend.LegendLocalDataSource
import org.edu.stones.data.external.LegendExternalSource
import org.edu.stones.domain.repository.LegendRepository

class LegendRepositoryImpl(
    private val source: LegendExternalSource,
    private val diskCache: LegendLocalDataSource,
) : LegendRepository {

    private val memory = mutableMapOf<String, String>()
    private val mutex = Mutex()

    override suspend fun getLegendForPrompt(prompt: String): String? {
        val key = diskCache.cacheKey(prompt)

        mutex.withLock { memory[key] }?.let { return it }

        diskCache.get(key).getOrNull()?.let { fromDisk ->
            mutex.withLock { memory[key] = fromDisk }
            return fromDisk
        }

        val legend = source.generate(prompt) ?: return null
        diskCache.put(key, legend).onSuccess {
            mutex.withLock { memory[key] = legend }
        }
        return legend
    }
}
