package org.edu.stones.data.repository

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.edu.stones.data.local.image.ImageLocalDataSource
import org.edu.stones.data.external.ImageGenExternalSource
import org.edu.stones.domain.repository.ImageRepository

class ImageRepositoryImpl(
    private val source: ImageGenExternalSource,
    private val diskCache: ImageLocalDataSource,
) : ImageRepository {

    private val memory = mutableMapOf<String, ByteArray>()
    private val mutex = Mutex()

    override suspend fun getImageForPrompt(prompt: String): ByteArray? {
        val key = diskCache.cacheKey(prompt)

        mutex.withLock { memory[key] }?.let { return it }

        diskCache.get(key).getOrNull()?.let { fromDisk ->
            mutex.withLock { memory[key] = fromDisk }
            return fromDisk
        }

        val generated = source.generate(prompt) ?: return null
        diskCache.put(key, generated).onSuccess {
            mutex.withLock { memory[key] = generated }
        }
        return generated
    }
}
