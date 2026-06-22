package org.edu.stones.data.external.dto

import org.edu.stones.domain.entity.Subject
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json
import kotlin.String

@Serializable
data class DTORemoteMaterias(
    @SerialName("Codigo")val codigo: String,
    @SerialName("Nombre")val nombre: String,
    @SerialName("Año")val anio: Int,
    @SerialName("Periodo")val periodo: String,
    @SerialName("AñoDeRecopilacion")val anioDeRecopilacion: Int,
    @SerialName("NotasPromedio")val notasPromedio: Double,
    @SerialName("Inscriptos")val inscriptos: Int,
    @SerialName("Presencialidad")val presencialidad: String,
    @SerialName("CantAprobados")val cantAprobados: Int,
    @SerialName("CorrelativasCursadas")val correlativasCursadas: String = "",
    @SerialName("CorrelativasAprobadas")val correlativasAprobadas: String = "",
    @SerialName("Abreviatura")val abreviatura: String = "",
)


fun DTORemoteMaterias.toDomain(): Subject =
    Subject(
        codigo = codigo,
        nombre = nombre,
        anio = anio,
        periodo = periodo,
        anioDeRecopilacion = anioDeRecopilacion,
        notasPromedio = notasPromedio,
        inscriptos = inscriptos,
        presencialidad = presencialidad,
        cantAprobados = cantAprobados,
        correlativasCursadas = correlativasCursadas,
        correlativasAprobadas = correlativasAprobadas,
        abreviatura = abreviatura
    )

fun List<DTORemoteMaterias>.toDomainList(): List<Subject> =
    this.map { it.toDomain() }