package org.edu.stones.data.external.broker

import org.edu.stones.data.external.SubjectDetailExternalSource
import org.edu.stones.domain.Subject

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