package com.aseca.mobile.ui.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import com.aseca.mobile.models.PortfolioSummary
import com.aseca.mobile.ui.AuthColors

@Composable
fun HomeMetrics(
    summary: PortfolioSummary,
    positionsCount: Int,
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            MetricCard(
                title = "P&L total",
                value = summary.totalProfitLoss.moneyOrEmpty(),
                positive = (summary.totalProfitLoss ?: 0.0) >= 0,
                modifier = Modifier.weight(1f),
            )
            MetricCard(
                title = "P&L %",
                value = summary.unrealizedProfitLossPercentage.percentOrEmpty(),
                positive = (summary.unrealizedProfitLossPercentage ?: 0.0) >= 0,
                modifier = Modifier.weight(1f),
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            MetricCard(
                title = "Costo",
                value = summary.totalCostBasis.money(),
                modifier = Modifier.weight(1f),
            )
            MetricCard(
                title = "Posiciones",
                value = positionsCount.toString(),
                subtitle = "activas",
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun MetricCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    positive: Boolean = false,
) {
    Surface(
        modifier = modifier,
        color = Color(0xFF0C1017),
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, AuthColors.Border),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = title,
                color = AuthColors.MutedText,
                style = MaterialTheme.typography.bodySmall,
            )
            Text(
                text = value,
                color = if (positive) AuthColors.Accent else AuthColors.PrimaryText,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    color = AuthColors.MutedText,
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
    }
}
