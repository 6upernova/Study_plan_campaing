package org.edu.stones.data.external.dto

import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class DTORemoteMateriasTest {

    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun `deserializa los campos en espaniol via SerialName`() {
        val raw = """
            {
              "Codigo": "MAT001",
              "Nombre": "Algebra",
              "Año": 2,
              "Periodo": "Segundo Cuatrimestre",
              "AñoDeRecopilacion": 2024,
              "NotasPromedio": 6.5,
              "Inscriptos": 120,
              "Presencialidad": "Virtual",
              "CantAprobados": 80,
              "CorrelativasCursadas": "FIS001",
              "CorrelativasAprobadas": "ANA001",
              "Abreviatura": "ALG"
            }
        """.trimIndent()

        val dto = json.decodeFromString<DTORemoteMaterias>(raw)

        assertEquals("MAT001", dto.codigo)
        assertEquals("Algebra", dto.nombre)
        assertEquals(2, dto.anio)
        assertEquals(2024, dto.anioDeRecopilacion)
        assertEquals(6.5, dto.notasPromedio)
        assertEquals("FIS001", dto.correlativasCursadas)
        assertEquals("ALG", dto.abreviatura)
    }

    @Test
    fun `usa defaults cuando faltan correlativas y abreviatura`() {
        val raw = """
            {
              "Codigo": "MAT002",
              "Nombre": "Fisica",
              "Año": 1,
              "Periodo": "Primer Cuatrimestre",
              "AñoDeRecopilacion": 2024,
              "NotasPromedio": 7.0,
              "Inscriptos": 100,
              "Presencialidad": "Presencial",
              "CantAprobados": 60
            }
        """.trimIndent()

        val dto = json.decodeFromString<DTORemoteMaterias>(raw)

        assertEquals("", dto.correlativasCursadas)
        assertEquals("", dto.correlativasAprobadas)
        assertEquals("", dto.abreviatura)
    }

    @Test
    fun `ignora claves desconocidas`() {
        val raw = """
            {
              "Codigo": "MAT003",
              "Nombre": "Quimica",
              "Año": 1,
              "Periodo": "Primer Cuatrimestre",
              "AñoDeRecopilacion": 2024,
              "NotasPromedio": 8.0,
              "Inscriptos": 90,
              "Presencialidad": "Presencial",
              "CantAprobados": 70,
              "CampoExtraQueNoExiste": "se ignora"
            }
        """.trimIndent()

        val dto = json.decodeFromString<DTORemoteMaterias>(raw)
        assertEquals("MAT003", dto.codigo)
    }

    @Test
    fun `toDomain copia todos los campos`() {
        val dto = DTORemoteMaterias(
            codigo = "X", nombre = "N", anio = 3, periodo = "Primer Cuatrimestre",
            anioDeRecopilacion = 2023, notasPromedio = 5.0, inscriptos = 10,
            presencialidad = "Presencial", cantAprobados = 5,
            correlativasCursadas = "A", correlativasAprobadas = "B", abreviatura = "AB",
        )

        val subject = dto.toDomain()

        assertEquals("X", subject.codigo)
        assertEquals(3, subject.anio)
        assertEquals("A", subject.correlativasCursadas)
        assertEquals("B", subject.correlativasAprobadas)
        assertEquals("AB", subject.abreviatura)
    }

    @Test
    fun `toDomainList mapea una lista completa`() {
        val list = listOf(
            DTORemoteMaterias("A", "NA", 1, "Primer Cuatrimestre", 2024, 7.0, 1, "P", 1),
            DTORemoteMaterias("B", "NB", 1, "Primer Cuatrimestre", 2024, 7.0, 1, "P", 1),
        )

        assertEquals(listOf("A", "B"), list.toDomainList().map { it.codigo })
    }
}
