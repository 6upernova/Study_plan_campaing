package org.edu.stones.domain.repository


import org.jgrapht.graph.DefaultDirectedGraph
import org.jgrapht.graph.DefaultEdge


import org.edu.stones.domain.entity.Subject

interface SubjectRepository {
    suspend fun getAllSubjects(): DefaultDirectedGraph<Subject, DefaultEdge>
    suspend fun getSubjectDetail(id: String): Subject?
}