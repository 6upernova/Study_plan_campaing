package edu.dyds.movies.domain.repository

import com.sun.tools.jdeps.Graph
import edu.dyds.movies.domain.entity.Movie
import edu.dyds.movies.domain.entity.Subject

interface SubjectRepository {
    suspend fun getAllSubjects(): Graph<Subject>
    suspend fun getSubjectDetail(id: String): Subject?
}

