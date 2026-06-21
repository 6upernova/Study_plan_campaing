package org.edu.stones.domain.repository

interface LegendRepository {
    suspend fun getLegendForPrompt(prompt: String): String?
}
