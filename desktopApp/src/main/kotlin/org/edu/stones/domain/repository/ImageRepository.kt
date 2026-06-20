package org.edu.stones.domain.repository

interface ImageRepository {
    suspend fun getImageForPrompt(prompt: String): ByteArray?
}
