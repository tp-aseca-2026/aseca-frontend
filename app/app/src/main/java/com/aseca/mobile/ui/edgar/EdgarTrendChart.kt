package com.aseca.mobile.ui.edgar

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.aseca.mobile.models.EdgarMetricPoint
import com.aseca.mobile.ui.AuthColors

@Composable
internal fun TrendChart(
    points: List<EdgarMetricPoint>,
    money: Boolean,
) {
    val chartPoints = points
        .filter { point -> point.value != null }
        .take(8)
        .reversed()

    if (chartPoints.size < 2) return

    val values = chartPoints.mapNotNull { point -> point.value }
    val minValue = values.minOrNull() ?: return
    val maxValue = values.maxOrNull() ?: return
    val range = (maxValue - minValue).takeIf { value -> value != 0.0 } ?: 1.0

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp),
        ) {
            val widthStep = size.width / (values.size - 1)
            val topPadding = 18f
            val bottomPadding = 24f
            val chartHeight = size.height - topPadding - bottomPadding

            val offsets = values.mapIndexed { index, value ->
                val normalized = ((value - minValue) / range).toFloat()
                Offset(
                    x = index * widthStep,
                    y = topPadding + chartHeight - (normalized * chartHeight),
                )
            }

            val areaPath = Path().apply {
                moveTo(offsets.first().x, size.height - bottomPadding)
                offsets.forEach { offset -> lineTo(offset.x, offset.y) }
                lineTo(offsets.last().x, size.height - bottomPadding)
                close()
            }

            val linePath = Path().apply {
                moveTo(offsets.first().x, offsets.first().y)
                offsets.drop(1).forEach { offset -> lineTo(offset.x, offset.y) }
            }

            drawPath(
                path = areaPath,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        AuthColors.Accent.copy(alpha = 0.34f),
                        AuthColors.Accent.copy(alpha = 0.04f),
                    ),
                ),
            )

            drawPath(
                path = linePath,
                color = Color(0xFF8EA2FF),
                style = Stroke(width = 5f, cap = StrokeCap.Round),
            )

            offsets.forEach { offset ->
                drawCircle(
                    color = AuthColors.PrimaryText,
                    radius = 4.5f,
                    center = offset,
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = chartPoints.first().end ?: "Inicio",
                color = AuthColors.MutedText,
                style = MaterialTheme.typography.bodySmall,
            )
            Text(
                text = "${values.last().formatMetric(money)} último",
                color = AuthColors.Accent,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = chartPoints.last().end ?: "Último",
                color = AuthColors.MutedText,
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}

@Composable
internal fun MetricPointRow(
    point: EdgarMetricPoint,
    money: Boolean,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = point.end ?: "Sin fecha",
                color = AuthColors.MutedText,
                style = MaterialTheme.typography.bodySmall,
            )
            Text(
                text = point.form ?: "Sin form",
                color = AuthColors.Accent,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
            )
        }
        Text(
            text = if (money) point.value.moneyOrUnavailable() else point.value.numberOrUnavailable(),
            color = AuthColors.PrimaryText,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
        )
    }
}
