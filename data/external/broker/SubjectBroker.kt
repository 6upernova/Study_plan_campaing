package edu.dyds.movies.data.external.broker

import edu.dyds.movies.data.external.SubjectDetailExternalSource
import edu.dyds.movies.domain.entity.Subject

class SubjectsBroker(
    private val sources: List<SubjectDetailExternalSource>,
) : SubjectDetailExternalSource {

    override suspend fun getSubjectsByCareer(
        careerId: String,
    ): List<Subject> {

        sources.forEach { source ->
            val result = source.getSubjectsByCareer(careerId)

            if (result.isNotEmpty()) {
                return result
            }
        }

        return emptyList()
    }
}