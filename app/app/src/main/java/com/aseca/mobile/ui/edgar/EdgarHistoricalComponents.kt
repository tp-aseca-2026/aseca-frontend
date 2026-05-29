package com.aseca.mobile.ui.edgar

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.aseca.mobile.models.EdgarHistoricalMetrics
import com.aseca.mobile.models.EdgarMetricPoint
import com.aseca.mobile.ui.AuthColors

private data class HistoricalMetricDisplay(
    val key: String,
    val title: String,
    val money: Boolean,
    val points: List<EdgarMetricPoint>,
)

@Composable
fun HistoricalSection(
    historical: EdgarHistoricalMetrics,
    activeMetric: String,
    onMetricSelected: (String) -> Unit,
) {
    val groups = listOf(
        HistoricalMetricDisplay("revenue", "Revenue", money = true, points = historical.revenue),
        HistoricalMetricDisplay("netIncome", "Net Income", money = true, points = historical.netIncome),
        HistoricalMetricDisplay("eps", "EPS", money = false, points = historical.eps),
        HistoricalMetricDisplay("totalAssets", "Total Assets", money = true, points = historical.totalAssets),
        HistoricalMetricDisplay(
            "totalLiabilities",
            "Total Liabilities",
            money = true,
            points = historical.totalLiabilities,
        ),
    )
    val selectedMetric = groups.firstOrNull { metric -> metric.key == activeMetric } ?: groups.first()

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        SectionTitle("Evolución histórica")

        MetricTabs(
            metrics = groups,
            activeMetric = selectedMetric.key,
            onMetricSelected = onMetricSelected,
        )

        HistoricalDashboardCard(metric = selectedMetric)
    }
}

@Composable
private fun MetricTabs(
    metrics: List<HistoricalMetricDisplay>,
    activeMetric: String,
    onMetricSelected: (String) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        metrics.chunked(2).forEach { rowMetrics ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                rowMetrics.forEach { metric ->
                    MetricTabButton(
                        metric = metric,
                        selected = metric.key == activeMetric,
                        onClick = { onMetricSelected(metric.key) },
                        modifier = Modifier.weight(1f),
                    )
                }

                if (rowMetrics.size == 1) {
                    Column(modifier = Modifier.weight(1f)) {}
                }
            }
        }
    }
}

@Composable
private fun MetricTabButton(
    metric: HistoricalMetricDisplay,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .height(46.dp)
            .semantics { contentDescription = "edgar_metric_tab_${metric.key}" },
        colors = ButtonDefaults.buttonColors(
            containerColor = if (selected) Color(0xFF10291C) else Color(0xFF0C1017),
            contentColor = if (selected) AuthColors.Accent else AuthColors.PrimaryText,
        ),
        shape = RoundedCornerShape(999.dp),
        border = BorderStroke(1.dp, if (selected) AuthColors.Accent else AuthColors.Border),
    ) {
        Text(
            text = metric.title,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun HistoricalDashboardCard(metric: HistoricalMetricDisplay) {
    val points = metric.points.take(8)

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color(0xFF0C1017),
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, AuthColors.Border),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = metric.title,
                        color = AuthColors.PrimaryText,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = "Últimos puntos reportados",
                        color = AuthColors.MutedText,
                        style = MaterialTheme.typography.bodySmall,
                    )
                }

                points.firstOrNull()?.value?.let { latestValue ->
                    Text(
                        text = latestValue.formatMetric(metric.money),
                        color = AuthColors.Accent,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }

            if (points.isEmpty()) {
                Text(
                    text = "No disponible",
                    color = AuthColors.MutedText,
                    style = MaterialTheme.typography.bodyMedium,
                )
            } else {
                TrendChart(
                    points = points,
                    money = metric.money,
                )

                points.take(5).forEach { point ->
                    MetricPointRow(point, metric.money)
                }
            }
        }
    }
}
