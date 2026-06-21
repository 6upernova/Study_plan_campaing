package org.edu.stones.data.external.broker

import org.edu.stones.data.external.LegendExternalSource
import java.util.concurrent.atomic.AtomicInteger

class LegendBroker(
    private val sources: List<LegendExternalSource>
) : LegendExternalSource {

    private val counter = AtomicInteger(0)

    override suspend fun generate(prompt: String): String? {
        if (sources.isEmpty()) return null
        val startIndex = Math.floorMod(counter.getAndIncrement(), sources.size)
        // Intenta desde el índice round-robin, luego hace fallback al resto
        for (i in sources.indices) {
            val source = sources[(startIndex + i) % sources.size]
            val result = source.generate(prompt)
            if (result != null) return result
        }
        return null
    }
}
