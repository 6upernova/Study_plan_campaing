package org.edu.stones.domain.usecase

import kotlinx.coroutines.delay
import org.edu.stones.domain.entity.Subject

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
