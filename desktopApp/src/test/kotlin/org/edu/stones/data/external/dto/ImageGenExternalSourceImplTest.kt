package org.edu.stones.data.external.dto

import io.ktor.client.HttpClient
import io.ktor.http.HttpStatusCode
import io.mockk.clearAllMocks
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ImageGenExternalSourceImplTest {

    private val noopClient: () -> HttpClient = { HttpClient() }

    private fun ok(bytes: ByteArray) = HttpStatusCode.OK to bytes

    @AfterTest
    fun cleanup() {
        clearAllMocks()
    }

    @Test
    fun `devuelve los bytes cuando la respuesta no esta vacia`() = runTest {
        val expected = byteArrayOf(1, 2, 3, 4)
        val source = ImageGenExternalSourceImpl(
            clientProvider = noopClient,
            bytesProvider = { _, _ -> ok(expected) },
        )

        val result = source.generate("matematica")

        assertTrue(result != null && result.contentEquals(expected))
    }

    @Test
    fun `devuelve null cuando el provider lanza excepcion`() = runTest {
        val source = ImageGenExternalSourceImpl(
            clientProvider = noopClient,
            bytesProvider = { _, _ -> throw RuntimeException("timeout") },
        )

        assertNull(source.generate("fisica"))
    }

    @Test
    fun `devuelve null cuando la respuesta es vacia`() = runTest {
        val source = ImageGenExternalSourceImpl(
            clientProvider = noopClient,
            bytesProvider = { _, _ -> ok(ByteArray(0)) },
        )

        assertNull(source.generate("quimica"))
    }

    @Test
    fun `pasa el prompt recibido al provider`() = runTest {
        var capturado: String? = null
        val source = ImageGenExternalSourceImpl(
            clientProvider = noopClient,
            bytesProvider = { _, prompt -> capturado = prompt; ok(byteArrayOf(9)) },
        )

        source.generate("algoritmos")

        assertEquals("algoritmos", capturado)
    }

    @Test
    fun `reintenta ante 429 y devuelve los bytes al siguiente intento exitoso`() = runTest {
        var calls = 0
        val expected = byteArrayOf(7, 7)
        val source = ImageGenExternalSourceImpl(
            clientProvider = noopClient,
            bytesProvider = { _, _ ->
                calls++
                if (calls == 1) HttpStatusCode.TooManyRequests to ByteArray(0) else ok(expected)
            },
        )

        val result = source.generate("base de datos")

        assertEquals(2, calls)
        assertTrue(result != null && result.contentEquals(expected))
    }

    @Test
    fun `devuelve null ante error HTTP no recuperable`() = runTest {
        val source = ImageGenExternalSourceImpl(
            clientProvider = noopClient,
            bytesProvider = { _, _ -> HttpStatusCode.InternalServerError to ByteArray(0) },
        )

        assertNull(source.generate("redes"))
    }
}
