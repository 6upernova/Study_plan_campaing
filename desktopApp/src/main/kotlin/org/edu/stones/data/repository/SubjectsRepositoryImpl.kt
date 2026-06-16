package org.edu.stones.data.repository


import org.edu.stones.data.external.broker.SubjectsBroker
import org.jgrapht.graph.DefaultDirectedGraph
import org.jgrapht.graph.DefaultEdge
import org.edu.stones.domain.entity.Subject
import org.edu.stones.domain.repository.SubjectRepository

class SubjectsRepositoryImpl(
    private val subjectsBroker: SubjectsBroker
) : SubjectRepository {

    override suspend fun getAllSubjects(): DefaultDirectedGraph<Subject, DefaultEdge> {
        val subjectsList = subjectsBroker.getSubjectsByCareer("ISI")
        val graph = DefaultDirectedGraph<Subject, DefaultEdge>(DefaultEdge::class.java)


        val subjectMap = subjectsList.associateBy { it.codigo }


        subjectsList.forEach { graph.addVertex(it) }


        subjectsList.forEach { subject ->
            val prerequisitesCodes = subject.correlativasAprobadas
                ?.split(',')
                ?.map { it.trim() }
                ?.filter { it.isNotEmpty() }
                ?: emptyList()

            for (code in prerequisitesCodes) {
                val prerequisiteSubject = subjectMap[code]
                if (prerequisiteSubject != null) {
                    graph.addEdge(prerequisiteSubject, subject)
                }
            }
        }
        println(graph)
        return graph
    }

    override suspend fun getSubjectDetail(id: String): Subject? {
        TODO("Not yet implemented")
    }
}
