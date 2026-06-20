package org.edu.stones.domain.usecase

import kotlinx.coroutines.delay
import org.edu.stones.domain.entity.Subject

/**
 * Worker que va generando (o recuperando de cache) la imagen de cada materia de
 * forma continua: recorre las pendientes una y otra vez hasta que todas tengan
 * imagen. Pensado para correr en un hilo/corrutina de fondo.
 *
 * - Reutiliza [GenerateSubjectImageUseCase], que es cache-aside: si ya esta en
 *   cache no llama a la API (por eso es resumible entre arranques).
 * - Es SECUENCIAL con una espera entre pedidos, porque Pollinations anonimo solo
 *   permite 1 pedido por IP a la vez (mas paralelo => 429).
 * - Reintenta las que fallan en las siguientes pasadas, hasta [maxAttemptsPerSubject].
 */
class PreloadSubjectImagesUseCaseImpl(
    private val generateSubjectImageUseCase: GenerateSubjectImageUseCase,
    private val delayBetweenRequestsMs: Long = 1000,
    private val maxAttemptsPerSubject: Int = 10,
) : PreloadSubjectImagesUseCase {

    override suspend fun invoke(subjects: List<Subject>) {
        // materia -> intentos hechos
        val pending = subjects.associateWith { 0 }.toMutableMap()

        while (pending.isNotEmpty()) {
            for (subject in pending.keys.toList()) {
                val ok = runCatching { generateSubjectImageUseCase(subject) }.getOrNull() != null

                if (ok) {
                    pending.remove(subject)
                } else {
                    val intentos = (pending[subject] ?: 0) + 1
                    if (intentos >= maxAttemptsPerSubject) {
                        pending.remove(subject) // me rindo con esta, sigo con el resto
                    } else {
                        pending[subject] = intentos
                    }
                }

                delay(delayBetweenRequestsMs)
            }
        }
    }
}
