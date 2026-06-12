package org.edu.stones.domain.entity

data class Subject(
    val codigo: String,
    val nombre: String,
    val anio: Int,
    val periodo: String,
    val anioDeRecopilacion: Int,
    val notasPromedio: Double,
    val inscriptos: Int,
    val presencialidad: String,
    val cantAprobados: Int,
    val correlativasCursadas: String?,
    val correlativasAprobadas: String?,
)