package org.edu.stones.presentation.home

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.edu.stones.domain.entity.Subject
import org.edu.stones.domain.usecase.GetAllSubjectsUseCase
import org.edu.stones.domain.usecase.GetSubjectDetailUseCase
import org.edu.stones.domain.usecase.PreloadSubjectImagesUseCase
import org.edu.stones.domain.usecase.PreloadSubjectLegendsUseCase
import org.edu.stones.subject
import org.jgrapht.graph.DefaultDirectedGraph
import org.jgrapht.graph.DefaultEdge
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

private class FakeGetAllSubjects(
    private val graph: DefaultDirectedGraph<Subject, DefaultEdge>,
) : GetAllSubjectsUseCase {
    override suspend fun invoke(): DefaultDirectedGraph<Subject, DefaultEdge> = graph
}

private class FakeGetDetail(private val result: Subject?) : GetSubjectDetailUseCase {
    override suspend fun invoke(id: String): Subject? = result
}

private class FakePreloadImages : PreloadSubjectImagesUseCase {
    var subjects: List<Subject>? = null
        private set

    override suspend fun invoke(subjects: List<Subject>) {
        this.subjects = subjects
    }
}

private class FakePreloadLegends : PreloadSubjectLegendsUseCase {
    var subjects: List<Subject>? = null
        private set

    override suspend fun invoke(subjects: List<Subject>) {
        this.subjects = subjects
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private val dispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(dispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun graphOf(vararg subjects: Subject): DefaultDirectedGraph<Subject, DefaultEdge> {
        val g = DefaultDirectedGraph<Subject, DefaultEdge>(DefaultEdge::class.java)
        subjects.forEach { g.addVertex(it) }
        return g
    }

    @Test
    fun `getAllSubjects produce el layout y termina sin loading`() = runTest(dispatcher) {
        val graph = graphOf(subject(codigo = "A"), subject(codigo = "B", anio = 2))
        val vm = HomeViewModel(
            getAllSubjectsUseCase = FakeGetAllSubjects(graph),
            getSubjectsUseCase = FakeGetDetail(null),
            preloadSubjectImagesUseCase = FakePreloadImages(),
            preloadSubjectLegendsUseCase = FakePreloadLegends(),
        )

        vm.getAllSubjects()
        advanceUntilIdle()

        val state = vm.homeStateFlow.value
        assertFalse(state.isLoading)
        assertNotNull(state.graphLayoutData)
        assertEquals(2, state.graphLayoutData!!.nodes.size)
    }

    @Test
    fun `getAllSubjects dispara la precarga de imagenes y leyendas`() = runTest(dispatcher) {
        val graph = graphOf(subject(codigo = "A"))
        val preImages = FakePreloadImages()
        val preLegends = FakePreloadLegends()
        val vm = HomeViewModel(
            getAllSubjectsUseCase = FakeGetAllSubjects(graph),
            getSubjectsUseCase = FakeGetDetail(null),
            preloadSubjectImagesUseCase = preImages,
            preloadSubjectLegendsUseCase = preLegends,
        )

        vm.getAllSubjects()
        advanceUntilIdle()

        assertEquals(listOf("A"), preImages.subjects?.map { it.codigo })
        assertEquals(listOf("A"), preLegends.subjects?.map { it.codigo })
    }

    @Test
    fun `getSubjectDetail delega en el use case`() = runTest(dispatcher) {
        val expected = subject(codigo = "DET")
        val vm = HomeViewModel(
            getAllSubjectsUseCase = FakeGetAllSubjects(graphOf()),
            getSubjectsUseCase = FakeGetDetail(expected),
            preloadSubjectImagesUseCase = FakePreloadImages(),
            preloadSubjectLegendsUseCase = FakePreloadLegends(),
        )

        assertEquals(expected, vm.getSubjectDetail("DET"))
    }

    @Test
    fun `getAllSubjects con grafo vacio produce layout vacio`() = runTest(dispatcher) {
        val vm = HomeViewModel(
            getAllSubjectsUseCase = FakeGetAllSubjects(graphOf()),
            getSubjectsUseCase = FakeGetDetail(null),
            preloadSubjectImagesUseCase = FakePreloadImages(),
            preloadSubjectLegendsUseCase = FakePreloadLegends(),
        )

        vm.getAllSubjects()
        advanceUntilIdle()

        val state = vm.homeStateFlow.value
        assertNotNull(state.graphLayoutData)
        assertTrue(state.graphLayoutData!!.nodes.isEmpty())
    }
}
