package org.edu.stones.data.local

import org.edu.stones.data.local.graph.CacheConstants
import org.edu.stones.data.local.graph.CachedGraph
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import kotlinx.serialization.serializer
import java.io.File
import java.security.MessageDigest

class SubjectLocalDataSourceImpl(
    private val baseDir: File = File(System.getProperty("user.dir")),
) : SubjectLocalDataSource {

    private val json = Json {
        ignoreUnknownKeys = true
        prettyPrint = true
    }

    private val cacheFile: File by lazy {
        val cacheDir = File(baseDir, ".cache")
        if (!cacheDir.exists()) {
            cacheDir.mkdirs()
        }
        File(cacheDir, "graph_cache.json")
    }

    private val metadataFile: File by lazy {
        File(cacheFile.parent, "graph_cache.meta")
    }

    override suspend fun saveGraph(graph: CachedGraph): Result<Unit> = runCatching {
        val jsonString = json.encodeToString(serializer<CachedGraph>(), graph)
        cacheFile.writeText(jsonString)

        val contentHash = computeHash(jsonString)
        val metadataContent = "${graph.careerId}|${graph.cachedAt}|${graph.version}|$contentHash"
        metadataFile.writeText(metadataContent)
    }

    override suspend fun getGraph(): Result<CachedGraph?> = runCatching {
        if (!cacheFile.exists() || !metadataFile.exists()) {
            return@runCatching null
        }

        val cachedContent = cacheFile.readText()
        val cached = json.decodeFromString(serializer<CachedGraph>(), cachedContent)
        cached
    }

    override suspend fun isCacheValid(): Result<Boolean> = runCatching {
        if (!cacheFile.exists() || !metadataFile.exists()) {
            return@runCatching false
        }

        val metadata = metadataFile.readText()
        val parts = metadata.split("|")
        if (parts.size != 4) {
            return@runCatching false
        }

        val careerId = parts[0]
        val cachedAt = parts[1].toLongOrNull() ?: return@runCatching false
        val version = parts[2]
        val storedHash = parts[3]

        val currentContent = cacheFile.readText()
        val currentHash = computeHash(currentContent)

        if (currentHash != storedHash) {
            return@runCatching false
        }

        val now = System.currentTimeMillis()
        val isExpired = (now - cachedAt) > CacheConstants.CACHE_VALIDITY_MS

        !isExpired
    }

    override suspend fun invalidateCache(): Result<Unit> = runCatching {
        if (cacheFile.exists()) {
            cacheFile.delete()
        }
        if (metadataFile.exists()) {
            metadataFile.delete()
        }
    }

    override suspend fun getCacheMetadata(): Result<CachedGraph?> = getGraph()

    private fun computeHash(content: String): String {
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(content.toByteArray())
        return digest.joinToString("") { "%02x".format(it) }
    }
}