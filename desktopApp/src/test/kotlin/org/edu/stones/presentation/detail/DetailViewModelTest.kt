package org.edu.stones.presentation.detail

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
import org.edu.stones.subject
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

private class FakeDetailUseCase(private val result: Subject?) : GetSubjectDetailUseCase {
    override suspend fun invoke(id: String): Subject? = result
}

private class FakeImageUseCase(private val bytes: ByteArray?) : GenerateSubjectImageUseCase {
    var calls = 0
        private set

    override suspend fun invoke(subject: Subject): ByteArray? {
        calls++
        return bytes
    }
}

private class FakeLegendUseCase(private val legend: String?) : GetSubjectLegendUseCase {
    var calls = 0
        private set

    override suspend fun invoke(subject: Subject): String? {
        calls++
        return legend
    }
}

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
    }

    @Test
    fun `getSubject carga el detalle, la imagen y la leyenda`() = runTest(dispatcher) {
        val subject = subject(codigo = "MAT001")
        val image = byteArrayOf(1, 2, 3)
        val vm = DetailViewModel(
            getSubjectDetailUseCase = FakeDetailUseCase(subject),
            generateSubjectImageUseCase = FakeImageUseCase(image),
            getSubjectLegendUseCase = FakeLegendUseCase("una leyenda"),
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
    }

    @Test
    fun `getSubject con materia inexistente no carga imagen ni leyenda`() = runTest(dispatcher) {
        val image = FakeImageUseCase(byteArrayOf(9))
        val legend = FakeLegendUseCase("x")
        val vm = DetailViewModel(
            getSubjectDetailUseCase = FakeDetailUseCase(null),
            generateSubjectImageUseCase = image,
            getSubjectLegendUseCase = legend,
        )

        vm.getSubject("NO_EXISTE")
        advanceUntilIdle()

        val state = vm.detailStateFlow.value
        assertFalse(state.isLoading)
        assertNull(state.subject)
        assertEquals(0, image.calls)
        assertEquals(0, legend.calls)
        assertNull(state.imageBytes)
        assertNull(state.legend)
    }

    @Test
    fun `getDetail resetea el estado`() = runTest(dispatcher) {
        val vm = DetailViewModel(
            getSubjectDetailUseCase = FakeDetailUseCase(subject()),
            generateSubjectImageUseCase = FakeImageUseCase(byteArrayOf(1)),
            getSubjectLegendUseCase = FakeLegendUseCase("l"),
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
