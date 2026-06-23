package org.edu.stones.presentation.home.components

import org.edu.stones.domain.entity.Subject
import org.edu.stones.subject
import org.jgrapht.graph.DefaultDirectedGraph
import org.jgrapht.graph.DefaultEdge
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class GraphLayoutEngineTest {

    private fun graphOf(vararg subjects: Subject): DefaultDirectedGraph<Subject, DefaultEdge> {
        val g = DefaultDirectedGraph<Subject, DefaultEdge>(DefaultEdge::class.java)
        subjects.forEach { g.addVertex(it) }
        return g
    }

    @Test
    fun `cada vertice produce un nodo y cada arista un edge`() {
        val a = subject(codigo = "A", anio = 1, periodo = "Primer Cuatrimestre")
        val b = subject(codigo = "B", anio = 1, periodo = "Segundo Cuatrimestre")
        val graph = graphOf(a, b)
        graph.addEdge(a, b)

        val layout = GraphLayoutEngine.computeLayout(graph)

        assertEquals(2, layout.nodes.size)
        assertEquals(1, layout.edges.size)
        assertEquals("A" to "B", layout.edges.first().fromCode to layout.edges.first().toCode)
    }

    @Test
    fun `la layer se calcula segun anio y periodo`() {
        val primer1 = subject(codigo = "P1", anio = 1, periodo = "Primer Cuatrimestre")
        val segundo1 = subject(codigo = "S1", anio = 1, periodo = "Segundo Cuatrimestre")
        val primer2 = subject(codigo = "P2", anio = 2, periodo = "Primer Cuatrimestre")

        val layout = GraphLayoutEngine.computeLayout(graphOf(primer1, segundo1, primer2))
        val byCode = layout.nodes.associateBy { it.subject.codigo }

        assertEquals(0, byCode.getValue("P1").layer)
        assertEquals(1, byCode.getValue("S1").layer)
        assertEquals(2, byCode.getValue("P2").layer)
    }

    @Test
    fun `posicion x e y es determinista para el mismo codigo`() {
        val s = subject(codigo = "DET", anio = 1, periodo = "Primer Cuatrimestre")
        val n1 = GraphLayoutEngine.computeLayout(graphOf(s)).nodes.first()
        val n2 = GraphLayoutEngine.computeLayout(graphOf(s)).nodes.first()

        assertEquals(n1.x, n2.x)
        assertEquals(n1.y, n2.y)
    }

    @Test
    fun `grafo vacio produce layout vacio`() {
        val layout = GraphLayoutEngine.computeLayout(graphOf())
        assertTrue(layout.nodes.isEmpty())
        assertTrue(layout.edges.isEmpty())
    }

    @Test
    fun `ordena dentro de la layer por minimizacion de cruces`() {
        val a = subject(codigo = "A", anio = 1, periodo = "Primer Cuatrimestre")
        val b = subject(codigo = "B", anio = 1, periodo = "Primer Cuatrimestre")
        val c = subject(codigo = "C", anio = 1, periodo = "Segundo Cuatrimestre")
        val d = subject(codigo = "D", anio = 1, periodo = "Segundo Cuatrimestre")

        val graph = graphOf(a, b, c, d)
        graph.addEdge(a, c)
        graph.addEdge(b, d)

        val layout = GraphLayoutEngine.computeLayout(graph)
        val layer0 = layout.nodes.filter { it.layer == 0 }.sortedBy { it.positionInLayer }.map { it.subject.codigo }
        val layer1 = layout.nodes.filter { it.layer == 1 }.sortedBy { it.positionInLayer }.map { it.subject.codigo }

        assertEquals(2, layer0.size)
        assertEquals(2, layer1.size)
    }

    @Test
    fun `arista entre layers adyacentes no tiene waypoints`() {
        val a = subject(codigo = "A", anio = 1, periodo = "Primer Cuatrimestre")
        val b = subject(codigo = "B", anio = 1, periodo = "Segundo Cuatrimestre")
        val graph = graphOf(a, b)
        graph.addEdge(a, b)

        val layout = GraphLayoutEngine.computeLayout(graph)
        val edge = layout.edges.first()

        assertTrue(edge.waypoints.isEmpty())
    }

    @Test
    fun `arista que salta layers tiene waypoints`() {
        val a = subject(codigo = "A", anio = 1, periodo = "Primer Cuatrimestre")
        val b = subject(codigo = "B", anio = 3, periodo = "Primer Cuatrimestre")
        val graph = graphOf(a, b)
        graph.addEdge(a, b)

        val layout = GraphLayoutEngine.computeLayout(graph)
        val edge = layout.edges.first()

        assertFalse(edge.waypoints.isEmpty())
    }

    @Test
    fun `estructura asignada por anio`() {
        val a = subject(codigo = "A", anio = 1, periodo = "Primer Cuatrimestre")
        val b = subject(codigo = "B", anio = 2, periodo = "Primer Cuatrimestre")
        val c = subject(codigo = "C", anio = 3, periodo = "Primer Cuatrimestre")
        val d = subject(codigo = "D", anio = 4, periodo = "Primer Cuatrimestre")
        val e = subject(codigo = "E", anio = 5, periodo = "Primer Cuatrimestre")

        val layout = GraphLayoutEngine.computeLayout(graphOf(a, b, c, d, e))
        val byCode = layout.nodes.associateBy { it.subject.codigo }

        assertEquals("HUT_1", byCode.getValue("A").structureType.name.take(6))
        assertEquals("HOUSE_1", byCode.getValue("B").structureType.name.take(7))
        assertEquals("CAVE_1", byCode.getValue("C").structureType.name.take(6))
        assertEquals("TOWER_1", byCode.getValue("D").structureType.name.take(7))
        assertEquals("CASTLE_1", byCode.getValue("E").structureType.name.take(8))
    }

    @Test
    fun `topological layer respeta prerequisitos`() {
        val prereq = subject(codigo = "PRE", anio = 2, periodo = "Primer Cuatrimestre")
        val dependent = subject(codigo = "DEP", anio = 1, periodo = "Segundo Cuatrimestre")

        val graph = graphOf(prereq, dependent)
        graph.addEdge(prereq, dependent)

        val layout = GraphLayoutEngine.computeLayout(graph)
        val preNode = layout.nodes.first { it.subject.codigo == "PRE" }
        val depNode = layout.nodes.first { it.subject.codigo == "DEP" }

        assertTrue(depNode.layer > preNode.layer)
    }

    @Test
    fun `edge inexistente entre nodos no rompe el layout`() {
        val a = subject(codigo = "A")
        val layout = GraphLayoutEngine.computeLayout(graphOf(a))
        assertNotNull(layout)
        assertEquals(1, layout.nodes.size)
    }
}
