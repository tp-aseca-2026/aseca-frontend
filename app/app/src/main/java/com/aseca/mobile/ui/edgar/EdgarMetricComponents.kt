package com.aseca.mobile.ui.edgar

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.aseca.mobile.models.EdgarMetricPoint
import com.aseca.mobile.models.EdgarMetrics
import com.aseca.mobile.ui.AuthColors

private data class MetricDisplay(
    val title: String,
    val point: EdgarMetricPoint?,
    val money: Boolean,
)

@Composable
fun MetricsSection(metrics: EdgarMetrics) {
    val items = listOf(
        MetricDisplay("Revenue", metrics.revenue, money = true),
        MetricDisplay("Net Income", metrics.netIncome, money = true),
        MetricDisplay("EPS", metrics.eps, money = false),
        MetricDisplay("Total Assets", metrics.totalAssets, money = true),
        MetricDisplay("Total Liabilities", metrics.totalLiabilities, money = true),
    )

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        SectionTitle("Métricas financieras")
        items.forEach { metric ->
            MetricCard(metric)
        }
    }
}

@Composable
private fun MetricCard(metric: MetricDisplay) {
    val value = metric.point?.value
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color(0xFF0C1017),
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, AuthColors.Border),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = metric.title,
                color = AuthColors.MutedText,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = if (metric.money) value.moneyOrUnavailable() else value.numberOrUnavailable(),
                color = AuthColors.PrimaryText,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )
            metric.point?.let { point ->
                Text(
                    text = listOfNotNull(point.fp, point.fy?.toString(), point.form).joinToString(" · "),
                    color = AuthColors.MutedText,
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
    }
}
