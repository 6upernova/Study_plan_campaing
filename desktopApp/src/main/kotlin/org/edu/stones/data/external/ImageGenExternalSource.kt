package org.edu.stones.data.external

interface ImageGenExternalSource {
    suspend fun generate(prompt: String): ByteArray?
}
