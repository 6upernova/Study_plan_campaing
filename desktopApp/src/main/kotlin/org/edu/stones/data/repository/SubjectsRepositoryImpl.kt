package org.edu.stones.data.repository

import com.sun.tools.jdeps.Graph
import org.edu.stones.domain.entity.Subject
import org.edu.stones.domain.repository.SubjectRepository

class SubjectsRepositoryImpl() : SubjectRepository {
    override suspend fun getAllSubjects(): Graph<Subject> {
        TODO("Not yet implemented")
    }

    override suspend fun getSubjectDetail(id: String): Subject? {
        TODO("Not yet implemented")
    }

}
