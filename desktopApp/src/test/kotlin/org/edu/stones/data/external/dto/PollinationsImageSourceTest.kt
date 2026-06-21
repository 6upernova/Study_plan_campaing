package org.edu.stones.data.external.dto

import io.ktor.client.HttpClient
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class PollinationsImageSourceTest {

    private val noopClient: () -> HttpClient = { HttpClient() }

    @Test
    fun `devuelve los bytes cuando la respuesta no esta vacia`() = runTest {
        val expected = byteArrayOf(1, 2, 3, 4)
        val source = ImageGenExternalSourceImpl(
            clientProvider = noopClient,
            bytesProvider = { _, _ -> expected },
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
            bytesProvider = { _, _ -> ByteArray(0) },
        )

        assertNull(source.generate("quimica"))
    }

    @Test
    fun `pasa el prompt recibido al provider`() = runTest {
        var capturado: String? = null
        val source = ImageGenExternalSourceImpl(
            clientProvider = noopClient,
            bytesProvider = { _, prompt -> capturado = prompt; byteArrayOf(9) },
        )

        source.generate("algoritmos")

        assertEquals("algoritmos", capturado)
    }
}
