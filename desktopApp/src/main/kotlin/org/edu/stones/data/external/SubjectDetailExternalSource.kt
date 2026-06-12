package org.edu.stones.data.external

import org.edu.stones.domain.Subject

interface SubjectDetailExternalSource {
    suspend fun getSubjectsByCareer(careerId: String): List<Subject>
}

