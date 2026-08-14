package com.subhan.xercdefteri.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.dp
import com.subhan.xercdefteri.ui.CategoryTotal

@Composable
fun DonutChart(breakdown: List<CategoryTotal>, modifier: Modifier = Modifier) {
    val total = breakdown.sumOf { it.total }
    Canvas(modifier = modifier.size(96.dp)) {
        val strokeWidth = size.minDimension * 0.22f
        val diameter = size.minDimension - strokeWidth
        val topLeft = androidx.compose.ui.geometry.Offset(
            (size.width - diameter) / 2f,
            (size.height - diameter) / 2f
        )
        var startAngle = -90f
        if (total <= 0.0) return@Canvas
        breakdown.forEach { ct ->
            val sweep = (ct.total / total * 360.0).toFloat()
            drawArc(
                color = ct.category.color,
                startAngle = startAngle,
                sweepAngle = sweep - 1.5f,
                useCenter = false,
                topLeft = topLeft,
                size = Size(diameter, diameter),
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = strokeWidth, cap = androidx.compose.ui.graphics.StrokeCap.Butt)
            )
            startAngle += sweep
        }
    }
}
