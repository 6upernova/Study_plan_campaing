package edu.dyds.movies.data.external

import edu.dyds.movies.domain.entity.Subject

interface SubjectDetailExternalSource {
    suspend fun getSubjectsByCareer(careerId: String): List<Subject>
}

