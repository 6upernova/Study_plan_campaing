package org.edu.stones.presentation.detail

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
import org.edu.stones.domain.usecase.GenerateSubjectImageUseCase
import org.edu.stones.domain.usecase.GetSubjectDetailUseCase
import org.edu.stones.domain.usecase.GetSubjectLegendUseCase
import org.edu.stones.domain.usecase.GetSubjectNameUseCase
import org.edu.stones.subject
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class DetailViewModelTest {

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

    @Test
    fun `getSubject carga el detalle, la imagen y la leyenda`() = runTest(dispatcher) {
        val subject = subject(codigo = "MAT001")
        val image = byteArrayOf(1, 2, 3)
        val getSubjectDetailUseCase: GetSubjectDetailUseCase = mockk()
        val generateSubjectImageUseCase: GenerateSubjectImageUseCase = mockk()
        val getSubjectLegendUseCase: GetSubjectLegendUseCase = mockk()
        val getSubjectNameUseCase: GetSubjectNameUseCase = mockk()
        
        coEvery { getSubjectDetailUseCase(any()) } returns subject
        coEvery { generateSubjectImageUseCase(any()) } returns image
        coEvery { getSubjectLegendUseCase(any()) } returns "una leyenda"
        coEvery { getSubjectNameUseCase(any()) } returns "MAT001"
        
        val vm = DetailViewModel(
            getSubjectDetailUseCase = getSubjectDetailUseCase,
            generateSubjectImageUseCase = generateSubjectImageUseCase,
            getSubjectLegendUseCase = getSubjectLegendUseCase,
            getSubjectNameUseCase = getSubjectNameUseCase,
        )

        vm.getSubject("MAT001")
        advanceUntilIdle()

        val state = vm.detailStateFlow.value
        assertFalse(state.isLoading)
        assertEquals(subject, state.subject)
        assertTrue(state.imageBytes?.contentEquals(image) == true)
        assertEquals("una leyenda", state.legend)
        assertFalse(state.isImageLoading)
        assertFalse(state.isLegendLoading)

        coVerify(exactly = 1) { getSubjectDetailUseCase("MAT001") }
        coVerify(exactly = 1) { generateSubjectImageUseCase(subject) }
        coVerify(exactly = 1) { getSubjectLegendUseCase(subject) }
    }

    @Test
    fun `getSubject con materia inexistente no carga imagen ni leyenda`() = runTest(dispatcher) {
        val generateSubjectImageUseCase: GenerateSubjectImageUseCase = mockk()
        val getSubjectLegendUseCase: GetSubjectLegendUseCase = mockk()
        val getSubjectDetailUseCase: GetSubjectDetailUseCase = mockk()
        val getSubjectNameUseCase: GetSubjectNameUseCase = mockk()
        
        coEvery { generateSubjectImageUseCase(any()) } returns byteArrayOf(9)
        coEvery { getSubjectLegendUseCase(any()) } returns "x"
        coEvery { getSubjectDetailUseCase(any()) } returns null
        coEvery { getSubjectNameUseCase(any()) } returns "NO_EXISTE"
        
        val vm = DetailViewModel(
            getSubjectDetailUseCase = getSubjectDetailUseCase,
            generateSubjectImageUseCase = generateSubjectImageUseCase,
            getSubjectLegendUseCase = getSubjectLegendUseCase,
            getSubjectNameUseCase = getSubjectNameUseCase,
        )

        vm.getSubject("NO_EXISTE")
        advanceUntilIdle()

        val state = vm.detailStateFlow.value
        assertFalse(state.isLoading)
        assertNull(state.subject)
        coVerify(exactly = 0) { generateSubjectImageUseCase(any()) }
        coVerify(exactly = 0) { getSubjectLegendUseCase(any()) }
        assertNull(state.imageBytes)
        assertNull(state.legend)
    }

    @Test
    fun `getDetail resetea el estado`() = runTest(dispatcher) {
        val getSubjectDetailUseCase: GetSubjectDetailUseCase = mockk()
        val generateSubjectImageUseCase: GenerateSubjectImageUseCase = mockk()
        val getSubjectLegendUseCase: GetSubjectLegendUseCase = mockk()
        val getSubjectNameUseCase: GetSubjectNameUseCase = mockk()
        
        coEvery { getSubjectDetailUseCase(any()) } returns subject()
        coEvery { generateSubjectImageUseCase(any()) } returns byteArrayOf(1)
        coEvery { getSubjectLegendUseCase(any()) } returns "l"
        coEvery { getSubjectNameUseCase(any()) } returns "MAT001"
        
        val vm = DetailViewModel(
            getSubjectDetailUseCase = getSubjectDetailUseCase,
            generateSubjectImageUseCase = generateSubjectImageUseCase,
            getSubjectLegendUseCase = getSubjectLegendUseCase,
            getSubjectNameUseCase = getSubjectNameUseCase,
        )

        vm.getSubject("MAT001")
        advanceUntilIdle()
        vm.getDetail()

        val state = vm.detailStateFlow.value
        assertNull(state.subject)
        assertNull(state.imageBytes)
        assertNull(state.legend)
        assertFalse(state.isLoading)
    }
}
