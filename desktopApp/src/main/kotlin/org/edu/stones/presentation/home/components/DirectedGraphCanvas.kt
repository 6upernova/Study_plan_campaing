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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import org.edu.stones.presentation.home.config.GraphConfig
import org.edu.stones.presentation.home.config.GraphConfigDefaults

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun DirectedGraphCanvas(
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues = PaddingValues(0.dp),
    nodes: List<GraphNode>,
    edges: List<GraphEdge>,
    onNodeClick: (String) -> Unit,
    config: GraphConfig = GraphConfigDefaults.Default
) {
    val visibleNodes = nodes.filter { !it.isDummy }

    val maxX = visibleNodes.maxOfOrNull { it.x + it.spriteSize } ?: 1000f

    val canvasWidth = (maxX + config.canvasExtraWidth).coerceAtLeast(config.canvasMinWidth)

    val horizontalScrollState = rememberScrollState()

    val hoveredNodeCode = remember { mutableStateOf<String?>(null) }
    val currentHovered by hoveredNodeCode

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
                    val horizontalDelta = delta.y * (canvasWidth * -config.scrollWheelMultiplier)
                    horizontalScrollState.dispatchRawDelta(horizontalDelta)
                }
        ) {
            BoxWithConstraints(
                modifier = Modifier
                    .width(canvasWidth.dp)
                    .fillMaxHeight()
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    if (currentHovered != null) {
                        val connected = edges.filter { it.fromCode == currentHovered || it.toCode == currentHovered }
                        val dimmed = edges.filter { it.fromCode != currentHovered && it.toCode != currentHovered }
                        dimmed.forEach { edge -> drawDimmedDirtPath(edge, config) }
                        connected.forEach { edge -> drawHighlightedDirtPath(edge, config) }
                    } else {
                        edges.forEach { edge -> drawDirtPath(edge, config) }
                    }
                }

                visibleNodes.forEach { node ->
                    StructureNode(
                        node = node,
                        onClick = { onNodeClick(node.subject.codigo) },
                        hoveredNodeCode = hoveredNodeCode
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

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun StructureNode(
    node: GraphNode,
    onClick: () -> Unit,
    hoveredNodeCode: MutableState<String?>
) {
    Column(
        modifier = Modifier
            .offset { IntOffset(node.x.toInt(), node.y.toInt()) }
            .onPointerEvent(PointerEventType.Enter) { hoveredNodeCode.value = node.subject.codigo }
            .onPointerEvent(PointerEventType.Exit) { if (hoveredNodeCode.value == node.subject.codigo) hoveredNodeCode.value = null }
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
            fontSize = with(LocalDensity.current) { node.fontSize.toInt().sp },
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

fun DrawScope.drawDirtPath(edge: GraphEdge, config: GraphConfig) {
    val path = buildSmoothPath(edge, config)
    drawPathLayered(path, edge.strokeWidth, config)
}

fun DrawScope.drawHighlightedDirtPath(edge: GraphEdge, config: GraphConfig) {
    val path = buildSmoothPath(edge, config)
    drawPath(path, color = config.colorDirtHighlightShadow, style = Stroke(width = edge.strokeWidth * config.strokeShadowMultiplier))
    drawPath(path, color = config.colorDirtHighlight, style = Stroke(width = edge.strokeWidth * config.strokeDarkMultiplier))
    drawPath(path, color = config.colorDirtHighlight, style = Stroke(width = edge.strokeWidth * config.strokeMidMultiplier))
    drawPath(path, color = config.colorDirtHighlight, style = Stroke(width = edge.strokeWidth * config.strokeLightMultiplier))
}

fun DrawScope.drawDimmedDirtPath(edge: GraphEdge, config: GraphConfig) {
    val path = buildSmoothPath(edge, config)
    drawPath(path, color = config.colorDirtShadow.copy(alpha = config.dimmedAlpha), style = Stroke(width = edge.strokeWidth * config.strokeShadowMultiplier))
    drawPath(path, color = config.colorDirtDark.copy(alpha = config.dimmedAlpha), style = Stroke(width = edge.strokeWidth * config.strokeDarkMultiplier))
    drawPath(path, color = config.colorDirtMid.copy(alpha = config.dimmedAlpha), style = Stroke(width = edge.strokeWidth * config.strokeMidMultiplier))
    drawPath(path, color = config.colorDirtLight.copy(alpha = config.dimmedAlpha), style = Stroke(width = edge.strokeWidth * config.strokeLightMultiplier))
}

fun DrawScope.drawPathLayered(path: Path, strokeWidth: Float, config: GraphConfig) {
    drawPath(path, color = config.colorDirtShadow, style = Stroke(width = strokeWidth * config.strokeShadowMultiplier))
    drawPath(path, color = config.colorDirtDark, style = Stroke(width = strokeWidth * config.strokeDarkMultiplier))
    drawPath(path, color = config.colorDirtMid, style = Stroke(width = strokeWidth * config.strokeMidMultiplier))
    drawPath(path, color = config.colorDirtLight, style = Stroke(width = strokeWidth * config.strokeLightMultiplier))
}



fun estimatePathLength(points: List<Offset>, config: GraphConfig): Float {
    if (points.size <= 2) {
        val dx = points[1].x - points[0].x
        val dy = points[1].y - points[0].y
        return sqrt((dx * dx + dy * dy).toDouble()).toFloat() * config.directPathLenMultiplier
    }
    var len = 0f
    for (i in 0 until points.size - 1) {
        val dx = points[i + 1].x - points[i].x
        val dy = points[i + 1].y - points[i].y
        len += sqrt((dx * dx + dy * dy).toDouble()).toFloat()
    }
    return len * config.routedPathLenMultiplier
}

fun sampleCatmullRom(points: List<Offset>, t: Float, tension: Float, config: GraphConfig): Offset {
    if (points.size <= 2) {
        val p0 = points[0]
        val p1 = points[1]
        val dx = p1.x - p0.x
        val cp1x = p0.x + dx * config.curveControlPointFactor
        val cp1y = p0.y
        val cp2x = p1.x - dx * config.curveControlPointFactor
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

fun buildSmoothPath(edge: GraphEdge, config: GraphConfig): Path {
    val points = buildPointList(edge)

    if (points.size <= 2) {
        return buildSimpleSCurve(points[0], points[1], config)
    }

    return buildCatmullRomSpline(points, config)
}

fun buildPointList(edge: GraphEdge): List<Offset> {
    val points = mutableListOf<Offset>()
    points.add(Offset(edge.fromX, edge.fromY))
    points.addAll(edge.waypoints)
    points.add(Offset(edge.toX, edge.toY))
    return points
}

fun buildSimpleSCurve(from: Offset, to: Offset, config: GraphConfig): Path {
    val dx = to.x - from.x
    return Path().apply {
        moveTo(from.x, from.y)
        val cp1x = from.x + dx * config.curveControlPointFactor
        val cp1y = from.y
        val cp2x = to.x - dx * config.curveControlPointFactor
        val cp2y = to.y
        cubicTo(cp1x, cp1y, cp2x, cp2y, to.x, to.y)
    }
}

fun buildCatmullRomSpline(points: List<Offset>, config: GraphConfig): Path {
    val path = Path().apply { moveTo(points[0].x, points[0].y) }
    val tension = config.catmullTension

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

class SimpleRNG(seed: Int) {
    private var state = seed
    fun next(): Int {
        state = state * 1103515245 + 12345
        return (state ushr 16) and 0x7FFF
    }
}
