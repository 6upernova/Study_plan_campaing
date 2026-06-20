package org.edu.stones.data.cache

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.security.MessageDigest

class ImageDiskCache(
    private val cacheDir: Path =
        Paths.get(System.getProperty("user.dir"), ".cache", "images"),
) {
    fun cacheKey(prompt: String): String {
        val digest = MessageDigest.getInstance("SHA-256").digest(prompt.toByteArray())
        return digest.joinToString("") { "%02x".format(it) }
    }

    fun get(key: String): ByteArray? = runCatching {
        val file = cacheDir.resolve("$key.png")
        if (Files.exists(file)) Files.readAllBytes(file) else null
    }.getOrNull()

    fun put(key: String, bytes: ByteArray) {
        runCatching {
            Files.createDirectories(cacheDir)
            Files.write(cacheDir.resolve("$key.png"), bytes)
        }
    }
}
