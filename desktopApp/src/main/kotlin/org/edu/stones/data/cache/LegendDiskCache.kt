package org.edu.stones.data.cache

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.security.MessageDigest

class LegendDiskCache(
    private val cacheDir: Path =
        Paths.get(System.getProperty("user.dir"), ".cache", "legends"),
) {
    fun cacheKey(prompt: String): String {
        val digest = MessageDigest.getInstance("SHA-256").digest(prompt.toByteArray())
        return digest.joinToString("") { "%02x".format(it) }
    }

    fun get(key: String): String? = runCatching {
        val file = cacheDir.resolve("$key.txt")
        if (Files.exists(file)) Files.readString(file).takeIf { it.isNotBlank() } else null
    }.getOrNull()

    fun put(key: String, legend: String) {
        runCatching {
            Files.createDirectories(cacheDir)
            Files.writeString(cacheDir.resolve("$key.txt"), legend)
        }
    }
}
