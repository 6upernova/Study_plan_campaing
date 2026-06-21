package org.edu.stones.data.local.image

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.security.MessageDigest
import kotlin.Result
import kotlin.runCatching

class ImageLocalDataSourceImpl(
    private val cacheDir: Path =
        Paths.get(System.getProperty("user.dir"), ".cache", "images"),
) : ImageLocalDataSource {

    override suspend fun cacheKey(prompt: String): String {
        val digest = MessageDigest.getInstance("SHA-256").digest(prompt.toByteArray())
        return digest.joinToString("") { "%02x".format(it) }
    }

    override suspend fun get(key: String): Result<ByteArray?> = runCatching {
        val file = cacheDir.resolve("$key.png")
        if (Files.exists(file)) Files.readAllBytes(file) else null
    }

    override suspend fun put(key: String, bytes: ByteArray): Result<Unit> = runCatching {
        Files.createDirectories(cacheDir)
        Files.write(cacheDir.resolve("$key.png"), bytes)
    }
}