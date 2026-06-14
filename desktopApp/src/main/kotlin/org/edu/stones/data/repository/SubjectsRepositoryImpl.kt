package org.edu.stones.data.repository


import org.jgrapht.graph.DefaultDirectedGraph
import org.jgrapht.graph.DefaultEdge
import org.edu.stones.domain.entity.Subject
import org.edu.stones.domain.repository.SubjectRepository

class SubjectsRepositoryImpl() : SubjectRepository {
    override suspend fun getAllSubjects(): DefaultDirectedGraph<Subject, DefaultEdge> {
        TODO("Not yet implemented")
    }

    override suspend fun getSubjectDetail(id: String): Subject? {
        TODO("Not yet implemented")
    }

}
