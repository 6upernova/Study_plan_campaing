package org.edu.stones.data.external.dto

import io.ktor.client.HttpClient
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.URLProtocol
import io.ktor.http.contentType
import io.ktor.http.path
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.edu.stones.data.external.LegendExternalSource

@Serializable
private data class ChatRequest(val model: String, val messages: List<ChatMessage>)

@Serializable
private data class ChatMessage(val role: String, val content: String)

@Serializable
private data class ChatResponse(val choices: List<Choice>)

@Serializable
private data class Choice(val message: ChatMessage)

class OpenAiLegendExternalSourceImpl : LegendExternalSource {

    private val json = Json { ignoreUnknownKeys = true }
    private val client = createOpenAIHttpClient(json)
    private val semaphore = Semaphore(1)

    override suspend fun generate(prompt: String): String? {
        for (attempt in 0 until 3) {
            try {
                val (status, body) = semaphore.withPermit {
                    val response = client.post {
                        url { path("openai") }
                        contentType(ContentType.Application.Json)
                        setBody(
                            ChatRequest(
                                model = "openai",
                                messages = listOf(ChatMessage(role = "user", content = prompt))
                            )
                        )
                    }
                    response.status to response.bodyAsText()
                }
                when {
                    status == HttpStatusCode.TooManyRequests -> {
                        val waitSec = (attempt + 1) * 15
                        println("Leyenda (POST): 429 recibido, reintentando en ${waitSec}s (intento ${attempt + 1}/3)")
                        delay(waitSec * 1_000L)
                    }
                    status.value in 200..299 -> {
                        val chatResponse = json.decodeFromString<ChatResponse>(body)
                        return chatResponse.choices.firstOrNull()?.message?.content?.trim()
                            ?.takeIf { it.isNotEmpty() }
                    }
                    else -> {
                        println("Error generando leyenda (POST): HTTP ${status.value}")
                        return null
                    }
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                println("Error generando leyenda (POST): ${e.message}")
                return null
            }
        }
        println("Leyenda (POST): 3 intentos fallidos, se omite esta materia")
        return null
    }
}

private fun createOpenAIHttpClient(json: Json): HttpClient =
    HttpClient {
        install(ContentNegotiation) { json(json) }
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
