package org.edu.stones.presentation.home.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.HorizontalScrollbar
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun DirectedGraphCanvas(
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues = PaddingValues(0.dp),
    nodes: List<GraphNode>,
    edges: List<GraphEdge>,
    onNodeClick: (String) -> Unit
) {
    val nodeWidth = GraphLayoutEngine.getNodeWidth()
    val nodeHeight = GraphLayoutEngine.getNodeHeight()

    val maxX = nodes.maxOfOrNull { it.x + nodeWidth } ?: 1000f
    val maxY = nodes.maxOfOrNull { it.y + nodeHeight } ?: 1000f

    val canvasWidth = (maxX + 100).coerceAtLeast(800f)
    val canvasHeight = (maxY + 150).coerceAtLeast(600f)

    val verticalScrollState = rememberScrollState()
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
                .verticalScroll(verticalScrollState)
                .onPointerEvent(PointerEventType.Scroll) { event ->
                    val delta = event.changes.firstOrNull()?.scrollDelta ?: return@onPointerEvent
                    val totalDelta = (delta.x + delta.y) * 48f
                    horizontalScrollState.dispatchRawDelta(-totalDelta)
                }
        ) {
            BoxWithConstraints(
                modifier = Modifier
                    .width(canvasWidth.dp)
                    .height(canvasHeight.dp)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    edges.forEach { edge -> drawEdge(edge = edge) }
                    nodes.forEach { node -> drawNodeBg(node = node) }
                }

                nodes.forEach { node ->
                    NodeLabel(
                        node = node,
                        nodeWidth = nodeWidth,
                        nodeHeight = nodeHeight,
                        onClick = { onNodeClick(node.subject.codigo) }
                    )
                }
            }
        }

        VerticalScrollbar(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .fillMaxHeight(),
            adapter = rememberScrollbarAdapter(verticalScrollState)
        )

        HorizontalScrollbar(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(),
            adapter = rememberScrollbarAdapter(horizontalScrollState)
        )
    }
}

@Composable
private fun NodeLabel(
    node: GraphNode,
    nodeWidth: Float,
    nodeHeight: Float,
    onClick: () -> Unit
) {
    val backgroundColor = getColorForYear(node.year)

    Box(
        modifier = Modifier
            .offset { IntOffset(node.x.toInt(), node.y.toInt()) }
            .size(
                width = with(LocalDensity.current) { nodeWidth.toInt() }.dp,
                height = with(LocalDensity.current) { nodeHeight.toInt() }.dp
            )
            .shadow(6.dp, RoundedCornerShape(14.dp))
            .clip(RoundedCornerShape(14.dp))
            .background(backgroundColor)
            .clickable { onClick() }
            .padding(4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = node.subject.abreviatura,
            color = Color.White,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis
        )
    }
}

private fun DrawScope.drawEdge(edge: GraphEdge) {
    val color = Color(0xFFFF0E52)
    val strokeWidth = 3f

    val dx = edge.toX - edge.fromX
    val cp1x = edge.fromX + dx * 0.25f
    val cp1y = edge.fromY + (edge.toY - edge.fromY) * 0.3f
    val cp2x = edge.toX - dx * 0.25f
    val cp2y = edge.toY - (edge.toY - edge.fromY) * 0.3f

    val path = Path().apply {
        moveTo(edge.fromX, edge.fromY)
        cubicTo(cp1x, cp1y, cp2x, cp2y, edge.toX, edge.toY)
    }

    drawPath(
        path = path,
        color = color,
        style = Stroke(width = strokeWidth)
    )

    val tangentAngle = atan2(
        (edge.toY - cp2y).toDouble(),
        (edge.toX - cp2x).toDouble()
    )

    drawArrowHead(
        tipX = edge.toX,
        tipY = edge.toY,
        angle = tangentAngle,
        color = color
    )
}

private fun DrawScope.drawArrowHead(tipX: Float, tipY: Float, angle: Double, color: Color) {
    val arrowSize = 11f
    val arrowAngle = Math.toRadians(28.0)

    val x1 = tipX - arrowSize * cos(angle - arrowAngle).toFloat()
    val y1 = tipY - arrowSize * sin(angle - arrowAngle).toFloat()
    val x2 = tipX - arrowSize * cos(angle + arrowAngle).toFloat()
    val y2 = tipY - arrowSize * sin(angle + arrowAngle).toFloat()

    val path = Path().apply {
        moveTo(tipX, tipY)
        lineTo(x1, y1)
        lineTo(x2, y2)
        close()
    }

    drawPath(path = path, color = color)
}

private fun DrawScope.drawNodeBg(node: GraphNode) {
    val nodeWidth = GraphLayoutEngine.getNodeWidth()
    val nodeHeight = GraphLayoutEngine.getNodeHeight()
    val baseColor = getColorForYear(node.year)

    drawRoundRect(
        color = baseColor.copy(alpha = 0.1f),
        topLeft = Offset(node.x, node.y),
        size = Size(nodeWidth, nodeHeight),
        cornerRadius = CornerRadius(14f, 14f)
    )
}

private fun getColorForYear(year: Int): Color {
    return when (year) {
        1 -> Color(0xFF43A047)
        2 -> Color(0xFF1E88E5)
        3 -> Color(0xFFF57C00)
        4 -> Color(0xFF8E24AA)
        else -> Color(0xFF546E7A)
    }
}