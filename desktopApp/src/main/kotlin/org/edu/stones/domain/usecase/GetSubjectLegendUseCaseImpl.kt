package org.edu.stones.domain.usecase

import org.edu.stones.domain.entity.Subject
import org.edu.stones.domain.repository.LegendRepository

class GetSubjectLegendUseCaseImpl(
    private val legendRepository: LegendRepository
) : GetSubjectLegendUseCase {

    override suspend fun invoke(subject: Subject): String? {
        val prompt = buildPrompt(subject)
        return legendRepository.getLegendForPrompt(prompt)
    }

    private fun buildPrompt(subject: Subject): String {
        val creature = selectCreature(subject)
        val ferocidad = when (creatureDifficulty(subject)) {
            CreatureDifficulty.EASY ->
                "que puede ser vencido por quienes se preparan con dedicación"
            CreatureDifficulty.MEDIUM ->
                "peligroso y astuto, que pone a prueba hasta al aventurero más valiente"
            CreatureDifficulty.HARD ->
                "despiadado y sin piedad, que destruye a casi todos los que osan enfrentarlo"
        }
        val rareza = when {
            subject.inscriptos < 100 -> "tan escaso que pocos en la carrera han tenido el privilegio de enfrentarlo"
            subject.inscriptos < 500 -> "conocido solo por los aventureros más avanzados de la carrera"
            else -> "legendario entre generaciones de estudiantes"
        }
        return "Genera una sola frase épica en español que describa la materia \"${subject.nombre}\" " +
               "como si ella misma fuera ${creature.nameEs}: $ferocidad y $rareza. " +
               "La materia es la bestia, no un lugar. Responde solo con la frase, sin comillas ni texto adicional."
    }
}
