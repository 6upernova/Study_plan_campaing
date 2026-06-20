package org.edu.stones.data.external.dto

import io.ktor.client.HttpClient
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.request.get
import io.ktor.client.statement.readRawBytes
import io.ktor.http.URLProtocol
import io.ktor.http.path
import org.edu.stones.data.external.ImageGenExternalSource

class PollinationsImageSource(
    private val clientProvider: () -> HttpClient = { createImageHttpClient() },
    private val bytesProvider: suspend (HttpClient, String) -> ByteArray = { client, prompt ->
        client.get {
            url {
                path("prompt", prompt) // encodea cada segmento
                parameters.append("width", "512")
                parameters.append("height", "512")
                parameters.append("nologo", "true")
                parameters.append("model", "turbo") // modelo mas rapido
            }
        }.readRawBytes()
    },
) : ImageGenExternalSource {

    override suspend fun generate(prompt: String): ByteArray? =
        runCatching {
            bytesProvider(clientProvider(), prompt).takeIf { it.isNotEmpty() }
        }.getOrElse { error ->
            println("Error generando imagen: ${error.message}")
            null
        }
}

private fun createImageHttpClient(): HttpClient =
    HttpClient {
        // Un HTTP no-2xx (ej. 429 Too Many Requests) lanza excepcion
        // => generate() devuelve null y NO se cachea el cuerpo de error.
        expectSuccess = true

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
