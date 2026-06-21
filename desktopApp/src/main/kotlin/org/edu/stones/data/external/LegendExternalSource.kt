package org.edu.stones.data.external

interface LegendExternalSource {
    suspend fun generate(prompt: String): String?
}
