package edu.dyds.movies.data.external.dto

import edu.dyds.movies.domain.entity.Subject
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DTORemoteMaterias(
    @SerialName("Codigo")val codigo: String,
    @SerialName("Nombre")val nombre: String,
    @SerialName("Año")val anio: Int,
    @SerialName("Período")val periodo: String,
    @SerialName("AñoDeRecopilacion")val anioDeRecopilacion: Int,
    @SerialName("NotasPromedio")val notasPromedio: Double,
    @SerialName("Inscriptos")val inscriptos: Int,
    @SerialName("Presencialidad")val presencialidad: String,
    @SerialName("CantAprobados")val cantAprobados: Int,
    @SerialName("CorrelativasCursadas")val correlativasCursadas: String? = null,
    @SerialName("CorrelativasAprobadas")val correlativasAprobadas: String? = null,
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
    )

