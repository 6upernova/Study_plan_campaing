package org.edu.stones.domain.usecase

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import org.edu.stones.domain.entity.Subject

/**
 * Pre-genera (o recupera de cache) la imagen de cada materia al inicio, de modo
 * que cada materia termine teniendo la suya.
 *
 * - Reutiliza [GenerateSubjectImageUseCase], que es cache-aside: si ya está en
 *   cache no llama a la API (por eso el proceso es resumible entre arranques).
 * - Corre con concurrencia acotada ([maxConcurrent]) para llenar el cache rápido
 *   sin saturar la API gratuita.
 * - Reintenta con backoff las que fallan (ej. 429 Too Many Requests) para no
 *   dejar materias sin imagen.
 */
class PreloadSubjectImagesUseCaseImpl(
    private val generateSubjectImageUseCase: GenerateSubjectImageUseCase,
    // Pollinations anonimo solo permite 1 pedido por IP a la vez: con mas de 1
    // se auto-inunda y devuelve 429. (Con NVIDIA esto se puede subir.)
    private val maxConcurrent: Int = 1,
    private val maxRetries: Int = 3,
    private val backoffBaseMs: Long = 1500,
) : PreloadSubjectImagesUseCase {

    override suspend fun invoke(subjects: List<Subject>) = coroutineScope {
        val semaphore = Semaphore(maxConcurrent)
        subjects.map { subject ->
            launch {
                semaphore.withPermit {
                    generateWithRetry(subject)
                }
            }
        }.joinAll()
    }

    private suspend fun generateWithRetry(subject: Subject) {
        repeat(maxRetries) { attempt ->
            val result = runCatching { generateSubjectImageUseCase(subject) }.getOrNull()
            if (result != null) return
            // backoff incremental antes del proximo intento
            delay(backoffBaseMs * (attempt + 1))
        }
    }
}
