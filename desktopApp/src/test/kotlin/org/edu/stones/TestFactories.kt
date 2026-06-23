package org.edu.stones

import org.edu.stones.domain.entity.Subject

fun subject(
    codigo: String = "MAT001",
    nombre: String = "Materia $codigo",
    anio: Int = 1,
    periodo: String = "Primer Cuatrimestre",
    anioDeRecopilacion: Int = 2024,
    notasPromedio: Double = 7.0,
    inscriptos: Int = 100,
    presencialidad: String = "Presencial",
    cantAprobados: Int = 50,
    correlativasCursadas: String? = null,
    correlativasAprobadas: String? = null,
    abreviatura: String = codigo,
): Subject = Subject(
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
    abreviatura = abreviatura,
)
