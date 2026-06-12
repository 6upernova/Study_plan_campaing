package org.edu.stones.data.external.dto

import org.edu.stones.data.external.SubjectDetailExternalSource
import org.edu.stones.domain.Subject
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

class GoogleScriptSubjectExternalSource(
    private val clientProvider: () -> HttpClient = { createHttpClient() },

    private val subjectResponseProvider:
    suspend (HttpClient) -> List<DTORemoteMaterias> =
        { client ->
            client.get("/").body<List<DTORemoteMaterias>>()
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
        }.getOrElse { emptyList() }
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
                    "/macros/s/AKfycbyxkoDQRowETbQ1bcUN8XFufPyT6A4jqviA65XYQOmBeEgJ-MEf6YcwRGUc_wvWv3Puvw/exec"
            }
        }

        install(HttpTimeout) {
            requestTimeoutMillis = 5000
        }
    }