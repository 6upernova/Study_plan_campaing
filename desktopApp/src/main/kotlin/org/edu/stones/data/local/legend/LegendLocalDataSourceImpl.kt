package org.edu.stones.data.local.legend

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.security.MessageDigest

class LegendLocalDataSourceImpl(
    private val cacheDir: Path =
        Paths.get(System.getProperty("user.dir"), ".cache", "legends"),
) : LegendLocalDataSource {

    override suspend fun cacheKey(prompt: String): String {
        val digest = MessageDigest.getInstance("SHA-256").digest(prompt.toByteArray())
        return digest.joinToString("") { "%02x".format(it) }
    }

    override suspend fun get(key: String): Result<String?> = runCatching {
        val file = cacheDir.resolve("$key.txt")
        if (Files.exists(file)) Files.readString(file).takeIf { it.isNotBlank() } else null
    }

    override suspend fun put(key: String, legend: String): Result<Unit> = runCatching {
        Files.createDirectories(cacheDir)
        Files.writeString(cacheDir.resolve("$key.txt"), legend)
    }
}
