package org.edu.stones.data.external.dto

import io.ktor.client.HttpClient
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.http.URLProtocol
import io.ktor.http.path
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import org.edu.stones.data.external.LegendExternalSource

class PollinationsTextLegendSource : LegendExternalSource {

    private val client = createLegendHttpClient()
    // Pollinations free tier: max 1 request simultáneo por IP
    private val semaphore = Semaphore(1)

    override suspend fun generate(prompt: String): String? {
        for (attempt in 0 until 3) {
            try {
                val (status, body) = semaphore.withPermit {
                    val response = client.get {
                        url {
                            path(prompt)
                            parameters.append("model", "openai")
                            parameters.append("seed", "42")
                        }
                    }
                    response.status to response.bodyAsText()
                }
                when {
                    status == HttpStatusCode.TooManyRequests -> {
                        val waitSec = (attempt + 1) * 15
                        println("Leyenda: 429 recibido, reintentando en ${waitSec}s (intento ${attempt + 1}/3)")
                        delay(waitSec * 1_000L)
                    }
                    status.value in 200..299 ->
                        return body.trim().takeIf { it.isNotEmpty() }
                    else -> {
                        println("Error generando leyenda: HTTP ${status.value}")
                        return null
                    }
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                println("Error generando leyenda: ${e.message}")
                return null
            }
        }
        println("Leyenda: 3 intentos fallidos, se omite esta materia")
        return null
    }
}

private fun createLegendHttpClient(): HttpClient =
    HttpClient {
        install(DefaultRequest) {
            url {
                protocol = URLProtocol.HTTPS
                host = "text.pollinations.ai"
            }
        }
        install(HttpTimeout) {
            requestTimeoutMillis = 30_000
        }
    }
