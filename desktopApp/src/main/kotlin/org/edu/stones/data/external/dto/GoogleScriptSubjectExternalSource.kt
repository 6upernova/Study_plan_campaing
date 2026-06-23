package org.edu.stones.data.external.dto

import org.edu.stones.data.external.SubjectDetailExternalSource
import org.edu.stones.domain.entity.Subject
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.http.URLProtocol
import io.ktor.http.encodedPath
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import io.ktor.client.statement.bodyAsText
import kotlinx.serialization.serializer

class GoogleScriptSubjectExternalSource(
    private val clientProvider: () -> HttpClient = { createHttpClient() },

    private val subjectResponseProvider:
    suspend (HttpClient) -> List<DTORemoteMaterias> =
        { client ->
            val response = client.get("")
            val jsonString = response.bodyAsText()
            val jsonParser = Json { ignoreUnknownKeys = true }
            val materias = jsonParser.decodeFromString<List<DTORemoteMaterias>>(
                serializer<List<DTORemoteMaterias>>(),
                jsonString
            )

            materias
        },
) : SubjectDetailExternalSource {

    private val supportedCareerId = "ISI"

    override suspend fun getSubjectsByCareer(
        careerId: String,
    ): List<Subject> {

        if (careerId != supportedCareerId) {
            return emptyList()
        }

        return runCatching {
            subjectResponseProvider(clientProvider())
                .map { it.toDomain() }
        }.getOrElse { error ->
            println("Error obteniendo materias: ${error.message}")
            error.printStackTrace()
            emptyList()
        }
    }
}

private fun createHttpClient(): HttpClient =
    HttpClient {
        install(ContentNegotiation) {
            json(
                Json {
                    ignoreUnknownKeys = true
                }
            )
        }

        install(DefaultRequest) {
            url {
                protocol = URLProtocol.HTTPS
                host = "script.google.com"
                encodedPath =
                    "/macros/s/AKfycbzA1e6tJE4A5vfsp_mJwUro-0FGxXL9eBnDC5WQLRKBX0ma4CYYVxjjxtgfGqtBL2aXsQ/exec"
            }
        }

        install(HttpTimeout) {
            requestTimeoutMillis = 8000
        }
    }
