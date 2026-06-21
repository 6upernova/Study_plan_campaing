package org.edu.stones.presentation.home.components

import org.edu.stones.domain.entity.Subject
import org.edu.stones.subject
import org.jgrapht.graph.DefaultDirectedGraph
import org.jgrapht.graph.DefaultEdge
import kotlin.test.Test
import kotlin.test.assertEquals
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
    fun `la columna se calcula segun anio y periodo`() {
        // columna = (anio-1)*2 + periodo ; Primer=1, otro=2
        val primer1 = subject(codigo = "P1", anio = 1, periodo = "Primer Cuatrimestre")
        val segundo1 = subject(codigo = "S1", anio = 1, periodo = "Segundo Cuatrimestre")
        val primer2 = subject(codigo = "P2", anio = 2, periodo = "Primer Cuatrimestre")

        val layout = GraphLayoutEngine.computeLayout(graphOf(primer1, segundo1, primer2))
        val byCode = layout.nodes.associateBy { it.subject.codigo }

        assertEquals(1, byCode.getValue("P1").column)
        assertEquals(2, byCode.getValue("S1").column)
        assertEquals(3, byCode.getValue("P2").column)
    }

    @Test
    fun `el ruido es determinista para el mismo codigo`() {
        val s = subject(codigo = "DET")
        val n1 = GraphLayoutEngine.computeLayout(graphOf(s)).nodes.first()
        val n2 = GraphLayoutEngine.computeLayout(graphOf(s)).nodes.first()

        assertEquals(n1.noiseX, n2.noiseX)
        assertEquals(n1.noiseY, n2.noiseY)
    }

    @Test
    fun `grafo vacio produce layout vacio`() {
        val layout = GraphLayoutEngine.computeLayout(graphOf())
        assertTrue(layout.nodes.isEmpty())
        assertTrue(layout.edges.isEmpty())
    }

    @Test
    fun `ordena dentro de la columna por cantidad de prerequisitos`() {
        // Dos materias en la misma columna: una con prerequisito (in-degree 1)
        // y otra sin (in-degree 0). La de in-degree 0 va antes.
        val prereq = subject(codigo = "PRE", anio = 1, periodo = "Primer Cuatrimestre")
        val sinPrereq = subject(codigo = "LIBRE", anio = 1, periodo = "Primer Cuatrimestre")
        val conPrereq = subject(codigo = "DEP", anio = 1, periodo = "Primer Cuatrimestre")

        val graph = graphOf(prereq, sinPrereq, conPrereq)
        graph.addEdge(prereq, conPrereq) // conPrereq tiene in-degree 1

        val layout = GraphLayoutEngine.computeLayout(graph)
        val sameColumn = layout.nodes
            .filter { it.column == 1 }
            .sortedBy { it.positionInColumn }
            .map { it.subject.codigo }

        // conPrereq (in-degree 1) debe ir despues de los de in-degree 0
        assertTrue(sameColumn.indexOf("DEP") > sameColumn.indexOf("LIBRE"))
    }

    @Test
    fun `edge inexistente entre nodos no rompe el layout`() {
        val a = subject(codigo = "A")
        val layout = GraphLayoutEngine.computeLayout(graphOf(a))
        assertNotNull(layout)
        assertEquals(1, layout.nodes.size)
    }
}
