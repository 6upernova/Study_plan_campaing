package org.edu.stones.data.external.dto

import io.ktor.client.HttpClient
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GoogleScriptSubjectExternalSourceTest {

    private val noopClient: () -> HttpClient = { HttpClient() }

    private fun dto(codigo: String) = DTORemoteMaterias(
        codigo = codigo,
        nombre = "Materia $codigo",
        anio = 1,
        periodo = "Primer Cuatrimestre",
        anioDeRecopilacion = 2024,
        notasPromedio = 7.0,
        inscriptos = 100,
        presencialidad = "Presencial",
        cantAprobados = 50,
    )

    @Test
    fun `mapea los DTO a dominio cuando el provider responde`() = runTest {
        val source = GoogleScriptSubjectExternalSource(
            clientProvider = noopClient,
            subjectResponseProvider = { listOf(dto("A"), dto("B")) },
        )

        val result = source.getSubjectsByCareer("ISI")

        assertEquals(listOf("A", "B"), result.map { it.codigo })
        assertEquals("Materia A", result.first().nombre)
    }

    @Test
    fun `careerId no soportado devuelve vacio sin invocar la red`() = runTest {
        var providerInvocado = false
        val source = GoogleScriptSubjectExternalSource(
            clientProvider = noopClient,
            subjectResponseProvider = { providerInvocado = true; emptyList() },
        )

        val result = source.getSubjectsByCareer("OTRA")

        assertTrue(result.isEmpty())
        assertTrue(!providerInvocado)
    }

    @Test
    fun `una excepcion del provider se traduce en lista vacia`() = runTest {
        val source = GoogleScriptSubjectExternalSource(
            clientProvider = noopClient,
            subjectResponseProvider = { throw RuntimeException("timeout de red") },
        )

        assertTrue(source.getSubjectsByCareer("ISI").isEmpty())
    }
}
