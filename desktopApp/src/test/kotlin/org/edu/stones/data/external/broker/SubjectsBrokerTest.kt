package org.edu.stones.data.external.broker

import kotlinx.coroutines.test.runTest
import org.edu.stones.data.external.SubjectDetailExternalSource
import org.edu.stones.domain.entity.Subject
import org.edu.stones.subject
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

private class FakeSource(
    private val result: List<Subject>,
) : SubjectDetailExternalSource {
    var calls = 0
        private set

    override suspend fun getSubjectsByCareer(careerId: String): List<Subject> {
        calls++
        return result
    }
}

class SubjectsBrokerTest {

    @Test
    fun `devuelve el primer source con resultado no vacio`() = runTest {
        val first = FakeSource(emptyList())
        val second = FakeSource(listOf(subject(codigo = "A")))
        val third = FakeSource(listOf(subject(codigo = "Z")))

        val result = SubjectsBroker(listOf(first, second, third)).getSubjectsByCareer("ISI")

        assertEquals(listOf("A"), result.map { it.codigo })
    }

    @Test
    fun `no consulta sources posteriores tras un hit`() = runTest {
        val first = FakeSource(listOf(subject(codigo = "A")))
        val second = FakeSource(listOf(subject(codigo = "B")))

        SubjectsBroker(listOf(first, second)).getSubjectsByCareer("ISI")

        assertEquals(1, first.calls)
        assertEquals(0, second.calls)
    }

    @Test
    fun `devuelve lista vacia si todos los sources estan vacios`() = runTest {
        val first = FakeSource(emptyList())
        val second = FakeSource(emptyList())

        val result = SubjectsBroker(listOf(first, second)).getSubjectsByCareer("ISI")

        assertTrue(result.isEmpty())
        assertEquals(1, first.calls)
        assertEquals(1, second.calls)
    }

    @Test
    fun `sin sources devuelve lista vacia`() = runTest {
        assertTrue(SubjectsBroker(emptyList()).getSubjectsByCareer("ISI").isEmpty())
    }
}
