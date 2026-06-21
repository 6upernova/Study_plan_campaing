package org.edu.stones.domain.usecase

import kotlinx.coroutines.delay
import org.edu.stones.domain.entity.Subject

/**
 * Worker que va generando (o recuperando de cache) la leyenda de cada materia de
 * forma secuencial: recorre las pendientes una y otra vez hasta que todas tengan
 * leyenda. Pensado para correr en un hilo/corrutina de fondo.
 *
 * - Reutiliza [GetSubjectLegendUseCase], que es cache-aside: si ya está en
 *   cache no llama a la API.
 * - Es SECUENCIAL con espera entre pedidos porque Pollinations anónimo solo
 *   permite 1 pedido por IP a la vez.
 * - Reintenta las que fallan en las siguientes pasadas, hasta [maxAttemptsPerSubject].
 */
class PreloadSubjectLegendsUseCaseImpl(
    private val getSubjectLegendUseCase: GetSubjectLegendUseCase,
    private val delayBetweenRequestsMs: Long = 1500,
    private val maxAttemptsPerSubject: Int = 10,
) : PreloadSubjectLegendsUseCase {

    override suspend fun invoke(subjects: List<Subject>) {
        val pending = subjects.associateWith { 0 }.toMutableMap()

        while (pending.isNotEmpty()) {
            for (subject in pending.keys.toList()) {
                val ok = runCatching { getSubjectLegendUseCase(subject) }.getOrNull() != null

                if (ok) {
                    pending.remove(subject)
                } else {
                    val intentos = (pending[subject] ?: 0) + 1
                    if (intentos >= maxAttemptsPerSubject) {
                        pending.remove(subject)
                    } else {
                        pending[subject] = intentos
                    }
                }

                delay(delayBetweenRequestsMs)
            }
        }
    }
}
