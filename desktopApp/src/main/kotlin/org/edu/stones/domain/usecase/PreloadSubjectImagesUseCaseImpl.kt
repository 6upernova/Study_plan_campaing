package org.edu.stones.domain.usecase

import kotlinx.coroutines.delay
import org.edu.stones.domain.entity.Subject

class PreloadSubjectImagesUseCaseImpl(
    private val generateSubjectImageUseCase: GenerateSubjectImageUseCase,
    private val delayBetweenRequestsMs: Long = 1000,
    private val maxAttemptsPerSubject: Int = 10,
) : PreloadSubjectImagesUseCase {

    override suspend fun invoke(subjects: List<Subject>) {

        val pending = subjects.associateWith { 0 }.toMutableMap()

        while (pending.isNotEmpty()) {
            for (subject in pending.keys.toList()) {
                val ok = runCatching { generateSubjectImageUseCase(subject) }.getOrNull() != null

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
