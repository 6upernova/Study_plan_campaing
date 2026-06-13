package org.edu.stones.domain.repository

import org.edu.stones.domain.entity.Graph
import org.edu.stones.domain.entity.Subject

interface SubjectRepository {
    suspend fun getAllSubjects(): Graph<Subject>
    suspend fun getSubjectDetail(id: String): Subject?
}

