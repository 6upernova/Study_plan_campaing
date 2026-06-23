package org.edu.stones.data.external.dto

import io.ktor.client.HttpClient
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.request.get
import io.ktor.client.statement.readRawBytes
import io.ktor.http.HttpStatusCode
import io.ktor.http.URLProtocol
import io.ktor.http.path
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import org.edu.stones.data.external.ImageGenExternalSource

class ImageGenExternalSourceImpl(
    private val clientProvider: () -> HttpClient = { createImageHttpClient() },

    private val bytesProvider: suspend (HttpClient, String) -> Pair<HttpStatusCode, ByteArray> =
        { client, prompt ->
            val response = client.get {
                url {
                    path("prompt", prompt)
                    parameters.append("width", "512")
                    parameters.append("height", "512")
                    parameters.append("nologo", "true")
                    parameters.append("model", "turbo")
                }
            }
            response.status to response.readRawBytes()
        },
) : ImageGenExternalSource {

    private val client by lazy { clientProvider() }
    private val semaphore = Semaphore(1)

    override suspend fun generate(prompt: String): ByteArray? {
        for (attempt in 0 until 3) {
            try {
                val (status, bytes) = semaphore.withPermit {
                    bytesProvider(client, prompt)
                }
                when {
                    status == HttpStatusCode.TooManyRequests -> {
                        val waitSec = (attempt + 1) * 15
                        println("Imagen: 429 recibido, reintentando en ${waitSec}s (intento ${attempt + 1}/3)")
                        delay(waitSec * 1_000L)
                    }
                    status.value in 200..299 ->
                        return bytes.takeIf { it.isNotEmpty() }
                    else -> {
                        println("Error generando imagen: HTTP ${status.value}")
                        return null
                    }
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                println("Error generando imagen: ${e.message}")
                return null
            }
        }
        println("Imagen: 3 intentos fallidos, se omite esta materia")
        return null
    }
}

private fun createImageHttpClient(): HttpClient =
    HttpClient {
        install(DefaultRequest) {
            url {
                protocol = URLProtocol.HTTPS
                host = "image.pollinations.ai"
            }
        }
        install(HttpTimeout) {
            requestTimeoutMillis = 30_000
        }
    }
