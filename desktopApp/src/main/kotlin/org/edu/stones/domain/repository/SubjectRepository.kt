package org.edu.stones.domain.repository

import com.sun.tools.jdeps.Graph
import org.edu.stones.domain.entity.Subject

interface SubjectRepository {
    suspend fun getAllSubjects(): Graph<Subject>
    suspend fun getSubjectDetail(id: String): Subject?
}

