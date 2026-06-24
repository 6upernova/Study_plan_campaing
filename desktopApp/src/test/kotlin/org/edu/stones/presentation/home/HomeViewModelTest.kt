package org.edu.stones.presentation.home

import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
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
        clearAllMocks()
    }

    private fun graphOf(vararg subjects: Subject): DefaultDirectedGraph<Subject, DefaultEdge> {
        val g = DefaultDirectedGraph<Subject, DefaultEdge>(DefaultEdge::class.java)
        subjects.forEach { g.addVertex(it) }
        return g
    }

    @Test
    fun `getAllSubjects produce el layout y termina sin loading`() = runTest(dispatcher) {
        val graph = graphOf(subject(codigo = "A"), subject(codigo = "B", anio = 2))
        val getAllSubjectsUseCase: GetAllSubjectsUseCase = mockk()
        val getSubjectsUseCase: GetSubjectDetailUseCase = mockk()
        val preloadSubjectImagesUseCase: PreloadSubjectImagesUseCase = mockk()
        val preloadSubjectLegendsUseCase: PreloadSubjectLegendsUseCase = mockk()
        
        coEvery { getAllSubjectsUseCase() } returns graph
        coEvery { getSubjectsUseCase(any()) } returns null
        coEvery { preloadSubjectImagesUseCase(any()) } returns Unit
        coEvery { preloadSubjectLegendsUseCase(any()) } returns Unit
        
        val vm = HomeViewModel(
            getAllSubjectsUseCase = getAllSubjectsUseCase,
            getSubjectsUseCase = getSubjectsUseCase,
            preloadSubjectImagesUseCase = preloadSubjectImagesUseCase,
            preloadSubjectLegendsUseCase = preloadSubjectLegendsUseCase,
        )

        vm.getAllSubjects()
        advanceUntilIdle()

        val state = vm.homeStateFlow.value
        assertFalse(state.isLoading)
        assertNotNull(state.graphLayoutData)
        assertEquals(2, state.graphLayoutData!!.nodes.size)
        coVerify(exactly = 1) { getAllSubjectsUseCase() }
    }

    @Test
    fun `getAllSubjects dispara la precarga de imagenes y leyendas`() = runTest(dispatcher) {
        val graph = graphOf(subject(codigo = "A"))
        val getAllSubjectsUseCase: GetAllSubjectsUseCase = mockk()
        val getSubjectsUseCase: GetSubjectDetailUseCase = mockk()
        val preloadSubjectImagesUseCase: PreloadSubjectImagesUseCase = mockk()
        val preloadSubjectLegendsUseCase: PreloadSubjectLegendsUseCase = mockk()
        
        coEvery { getAllSubjectsUseCase() } returns graph
        coEvery { getSubjectsUseCase(any()) } returns null
        coEvery { preloadSubjectImagesUseCase(any()) } returns Unit
        coEvery { preloadSubjectLegendsUseCase(any()) } returns Unit
        
        val vm = HomeViewModel(
            getAllSubjectsUseCase = getAllSubjectsUseCase,
            getSubjectsUseCase = getSubjectsUseCase,
            preloadSubjectImagesUseCase = preloadSubjectImagesUseCase,
            preloadSubjectLegendsUseCase = preloadSubjectLegendsUseCase,
        )

        vm.getAllSubjects()
        advanceUntilIdle()

        coVerify(exactly = 1) { preloadSubjectImagesUseCase(listOf(subject(codigo = "A"))) }
        coVerify(exactly = 1) { preloadSubjectLegendsUseCase(listOf(subject(codigo = "A"))) }
    }

    @Test
    fun `getSubjectDetail delega en el use case`() = runTest(dispatcher) {
        val expected = subject(codigo = "DET")
        val getAllSubjectsUseCase: GetAllSubjectsUseCase = mockk()
        val getSubjectsUseCase: GetSubjectDetailUseCase = mockk()
        val preloadSubjectImagesUseCase: PreloadSubjectImagesUseCase = mockk()
        val preloadSubjectLegendsUseCase: PreloadSubjectLegendsUseCase = mockk()
        
        coEvery { getAllSubjectsUseCase() } returns graphOf()
        coEvery { getSubjectsUseCase(any()) } returns expected
        
        val vm = HomeViewModel(
            getAllSubjectsUseCase = getAllSubjectsUseCase,
            getSubjectsUseCase = getSubjectsUseCase,
            preloadSubjectImagesUseCase = preloadSubjectImagesUseCase,
            preloadSubjectLegendsUseCase = preloadSubjectLegendsUseCase,
        )

        assertEquals(expected, vm.getSubjectDetail("DET"))
        coVerify(exactly = 1) { getSubjectsUseCase("DET") }
    }

    @Test
    fun `getAllSubjects con grafo vacio produce layout vacio`() = runTest(dispatcher) {
        val getAllSubjectsUseCase: GetAllSubjectsUseCase = mockk()
        val getSubjectsUseCase: GetSubjectDetailUseCase = mockk()
        val preloadSubjectImagesUseCase: PreloadSubjectImagesUseCase = mockk()
        val preloadSubjectLegendsUseCase: PreloadSubjectLegendsUseCase = mockk()
        
        coEvery { getAllSubjectsUseCase() } returns graphOf()
        coEvery { getSubjectsUseCase(any()) } returns null
        coEvery { preloadSubjectImagesUseCase(any()) } returns Unit
        coEvery { preloadSubjectLegendsUseCase(any()) } returns Unit
        
        val vm = HomeViewModel(
            getAllSubjectsUseCase = getAllSubjectsUseCase,
            getSubjectsUseCase = getSubjectsUseCase,
            preloadSubjectImagesUseCase = preloadSubjectImagesUseCase,
            preloadSubjectLegendsUseCase = preloadSubjectLegendsUseCase,
        )

        vm.getAllSubjects()
        advanceUntilIdle()

        val state = vm.homeStateFlow.value
        assertNotNull(state.graphLayoutData)
        assertTrue(state.graphLayoutData!!.nodes.isEmpty())
        coVerify(exactly = 1) { getAllSubjectsUseCase() }
    }
}
