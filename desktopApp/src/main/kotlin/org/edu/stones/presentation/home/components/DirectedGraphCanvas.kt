package org.edu.stones.presentation.home.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.HorizontalScrollbar
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.sqrt

private const val LABEL_HEIGHT = 24f
private val DIRT_DARK = Color(0xFF5D4037)
private val DIRT_MID = Color(0xFF8D6E63)
private val DIRT_LIGHT = Color(0xFFA1887F)
private val DIRT_SHADOW = Color(0x403D2B1E)
private const val CATMULL_TENSION = 0.5f

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun DirectedGraphCanvas(
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues = PaddingValues(0.dp),
    nodes: List<GraphNode>,
    edges: List<GraphEdge>,
    onNodeClick: (String) -> Unit
) {
    val visibleNodes = nodes.filter { !it.isDummy }

    val maxX = visibleNodes.maxOfOrNull { it.x + it.spriteSize } ?: 1000f

    val canvasWidth = (maxX + 200).coerceAtLeast(800f)

    val horizontalScrollState = rememberScrollState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .horizontalScroll(horizontalScrollState)
                .onPointerEvent(PointerEventType.Scroll) { event ->
                    val delta = event.changes.firstOrNull()?.scrollDelta ?: return@onPointerEvent
                    val horizontalDelta = delta.y * -48f
                    horizontalScrollState.dispatchRawDelta(horizontalDelta)
                }
        ) {
            BoxWithConstraints(
                modifier = Modifier
                    .width(canvasWidth.dp)
                    .fillMaxHeight()
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    edges.forEach { edge -> drawDirtPath(edge) }
                }

                visibleNodes.forEach { node ->
                    StructureNode(
                        node = node,
                        onClick = { onNodeClick(node.subject.codigo) }
                    )
                }
            }
        }

        HorizontalScrollbar(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(),
            adapter = rememberScrollbarAdapter(horizontalScrollState)
        )
    }
}

@Composable
private fun StructureNode(
    node: GraphNode,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .offset { IntOffset(node.x.toInt(), node.y.toInt()) }
            .clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        androidx.compose.foundation.Image(
            painter = painterResource(node.structureType.spritePath),
            contentDescription = node.subject.nombre,
            contentScale = ContentScale.Fit,
            modifier = Modifier.size(
                with(LocalDensity.current) { node.spriteSize.toInt().dp }
            )
        )
        Text(
            text = node.subject.abreviatura,
            color = Color(0xFFF5E6C8),
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.width(
                with(LocalDensity.current) { (node.spriteSize + 20f).toInt().dp }
            )
        )
    }
}

private fun DrawScope.drawDirtPath(edge: GraphEdge) {
    val path = buildSmoothPath(edge)
    drawPathLayered(path)
    drawPathTexture(path, edge)
}

private fun DrawScope.drawPathLayered(path: Path) {
    drawPath(path, color = DIRT_SHADOW, style = Stroke(width = 11f))
    drawPath(path, color = DIRT_DARK, style = Stroke(width = 9f))
    drawPath(path, color = DIRT_MID, style = Stroke(width = 7f))
    drawPath(path, color = DIRT_LIGHT, style = Stroke(width = 5f))
}

private fun DrawScope.drawPathTexture(path: Path, edge: GraphEdge) {
    val rng = SimpleRNG(edge.fromCode.hashCode())

    val points = buildPointList(edge)
    val approxLen = estimatePathLength(points)
    val step = 15f
    var t = 0f
    while (t <= 1f) {
        val pt = sampleCatmullRom(points, t, CATMULL_TENSION)
        val pebbleR = rng.next() % 3 + 1
        val pebbleX = pt.x + (rng.next() % 7 - 3)
        val pebbleY = pt.y + (rng.next() % 7 - 3)
        drawCircle(
            color = DIRT_DARK.copy(alpha = 0.3f),
            radius = pebbleR.toFloat(),
            center = Offset(pebbleX, pebbleY)
        )
        t += step / approxLen.coerceAtLeast(1f)
    }
}

private fun estimatePathLength(points: List<Offset>): Float {
    if (points.size <= 2) {
        val dx = points[1].x - points[0].x
        val dy = points[1].y - points[0].y
        return sqrt((dx * dx + dy * dy).toDouble()).toFloat() * 1.2f
    }
    var len = 0f
    for (i in 0 until points.size - 1) {
        val dx = points[i + 1].x - points[i].x
        val dy = points[i + 1].y - points[i].y
        len += sqrt((dx * dx + dy * dy).toDouble()).toFloat()
    }
    return len * 1.15f
}

private fun sampleCatmullRom(points: List<Offset>, t: Float, tension: Float): Offset {
    if (points.size <= 2) {
        val p0 = points[0]
        val p1 = points[1]
        val dx = p1.x - p0.x
        val cp1x = p0.x + dx * 0.4f
        val cp1y = p0.y
        val cp2x = p1.x - dx * 0.4f
        val cp2y = p1.y
        val u = t
        val u2 = u * u
        val u3 = u2 * u
        val x = (2 * u3 - 3 * u2 + 1) * p0.x
            + (u3 - 2 * u2 + u) * (cp1x - p0.x) * 3
            + (-2 * u3 + 3 * u2) * p1.x
            + (u3 - u2) * (cp2x - p1.x) * 3
        val y = (2 * u3 - 3 * u2 + 1) * p0.y
            + (u3 - 2 * u2 + u) * (cp1y - p0.y) * 3
            + (-2 * u3 + 3 * u2) * p1.y
            + (u3 - u2) * (cp2y - p1.y) * 3
        return Offset(x, y)
    }
    val segmentCount = points.size - 1
    val segmentT = t * segmentCount
    val i = segmentT.toInt().coerceIn(0, segmentCount - 1)
    val localT = segmentT - i

    val p0 = if (i > 0) points[i - 1] else points[0]
    val p1 = points[i]
    val p2 = points[i + 1]
    val p3 = if (i + 2 < points.size) points[i + 2] else points.last()

    val cp1x = p1.x + (p2.x - p0.x) * tension / 3f
    val cp1y = p1.y + (p2.y - p0.y) * tension / 3f
    val cp2x = p2.x - (p3.x - p1.x) * tension / 3f
    val cp2y = p2.y - (p3.y - p1.y) * tension / 3f

    val u = localT
    val u2 = u * u
    val u3 = u2 * u
    val x = (2 * u3 - 3 * u2 + 1) * p1.x
        + (u3 - 2 * u2 + u) * (cp1x - p1.x) * 3
        + (-2 * u3 + 3 * u2) * p2.x
        + (u3 - u2) * (cp2x - p2.x) * 3
    val y = (2 * u3 - 3 * u2 + 1) * p1.y
        + (u3 - 2 * u2 + u) * (cp1y - p1.y) * 3
        + (-2 * u3 + 3 * u2) * p2.y
        + (u3 - u2) * (cp2y - p2.y) * 3
    return Offset(x, y)
}

private fun buildSmoothPath(edge: GraphEdge): Path {
    val points = buildPointList(edge)

    if (points.size <= 2) {
        return buildSimpleSCurve(points[0], points[1])
    }

    return buildCatmullRomSpline(points)
}

private fun buildPointList(edge: GraphEdge): List<Offset> {
    val points = mutableListOf<Offset>()
    points.add(Offset(edge.fromX, edge.fromY))
    points.addAll(edge.waypoints)
    points.add(Offset(edge.toX, edge.toY))
    return points
}

private fun buildSimpleSCurve(from: Offset, to: Offset): Path {
    val dx = to.x - from.x
    return Path().apply {
        moveTo(from.x, from.y)
        val cp1x = from.x + dx * 0.4f
        val cp1y = from.y
        val cp2x = to.x - dx * 0.4f
        val cp2y = to.y
        cubicTo(cp1x, cp1y, cp2x, cp2y, to.x, to.y)
    }
}

private fun buildCatmullRomSpline(points: List<Offset>): Path {
    val path = Path().apply { moveTo(points[0].x, points[0].y) }
    val tension = CATMULL_TENSION

    for (i in 0 until points.size - 1) {
        val p0 = if (i > 0) points[i - 1] else points[0]
        val p1 = points[i]
        val p2 = points[i + 1]
        val p3 = if (i + 2 < points.size) points[i + 2] else points.last()

        val cp1x = p1.x + (p2.x - p0.x) * tension / 3f
        val cp1y = p1.y + (p2.y - p0.y) * tension / 3f
        val cp2x = p2.x - (p3.x - p1.x) * tension / 3f
        val cp2y = p2.y - (p3.y - p1.y) * tension / 3f

        path.cubicTo(cp1x, cp1y, cp2x, cp2y, p2.x, p2.y)
    }
    return path
}

private class SimpleRNG(seed: Int) {
    private var state = seed
    fun next(): Int {
        state = state * 1103515245 + 12345
        return (state ushr 16) and 0x7FFF
    }
}