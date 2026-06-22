package org.edu.stones.presentation.home.components

import org.edu.stones.domain.entity.Subject
import androidx.compose.ui.geometry.Offset

enum class StructureType(val iconIndex: Int) {
    HUT_1(1), HUT_2(2), HUT_3(3),
    HOUSE_1(4), HOUSE_2(5), HOUSE_3(6),
    CAVE_1(7), CAVE_2(8), CAVE_3(9),
    TOWER_1(10), TOWER_2(11), TOWER_3(12),
    CASTLE_1(13), CASTLE_2(14), CASTLE_3(15),
    BOSS_1(16), BOSS_2(17), BOSS_3(18);

    val spritePath: String get() = "images/homeScreen/icono$iconIndex.png"

    companion object {
        private val byYear = mapOf(
            1 to listOf(HUT_1, HUT_2, HUT_3),
            2 to listOf(HOUSE_1, HOUSE_2, HOUSE_3),
            3 to listOf(CAVE_1, CAVE_2, CAVE_3),
            4 to listOf(TOWER_1, TOWER_2, TOWER_3),
            5 to listOf(CASTLE_1, CASTLE_2, CASTLE_3)
        )

        fun forYear(year: Int, seed: Int): StructureType {
            val variants = byYear[year] ?: byYear[5]!!
            return variants[Math.floorMod(seed, variants.size)]
        }
    }
}

data class GraphNode(
    val subject: Subject,
    val year: Int,
    val semester: Int,
    val layer: Int,
    val positionInLayer: Int,
    val x: Float,
    val y: Float,
    val isDummy: Boolean = false,
    val structureType: StructureType = StructureType.forYear(year, subject.codigo.hashCode()),
    val spriteSize: Float,
    val strokeWidth: Float,
    val fontSize: Float
)

data class GraphEdge(
    val fromCode: String,
    val toCode: String,
    val fromX: Float,
    val fromY: Float,
    val toX: Float,
    val toY: Float,
    val waypoints: List<Offset> = emptyList(),
    val strokeWidth: Float
)

data class GraphLayoutData(
    val nodes: List<GraphNode>,
    val edges: List<GraphEdge>
)