package com.aseca.mobile.ui.watchlist

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.aseca.mobile.models.WatchlistComparisonItem
import com.aseca.mobile.ui.AuthColors

@Composable
fun ComparisonSection(
    loading: Boolean,
    comparison: List<WatchlistComparisonItem>,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color(0xFF0C1017),
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, AuthColors.Border),
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Comparación financiera",
                    color = AuthColors.PrimaryText,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = "Métricas principales de las empresas guardadas.",
                    color = AuthColors.MutedText,
                    style = MaterialTheme.typography.bodySmall,
                )
            }

            when {
                loading -> WatchlistMessage("Cargando comparación...")
                comparison.isEmpty() -> WatchlistMessage("Agregá empresas a tu watchlist para compararlas.")
                else -> comparison.forEach { item ->
                    ComparisonCard(item)
                }
            }
        }
    }
}

@Composable
private fun ComparisonCard(item: WatchlistComparisonItem) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color(0xFF060A0F),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, AuthColors.Border),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Column {
                Text(
                    text = item.ticker,
                    color = AuthColors.PrimaryText,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = item.companyName ?: "Sin nombre registrado",
                    color = AuthColors.MutedText,
                    style = MaterialTheme.typography.bodySmall,
                )
            }

            ComparisonRow("Revenue", item.revenue.money())
            ComparisonRow("Net Income", item.netIncome.money())
            ComparisonRow("EPS", item.eps.number())
            ComparisonRow("Assets", item.totalAssets.money())
            ComparisonRow("Liabilities", item.totalLiabilities.money())
        }
    }
}

@Composable
private fun ComparisonRow(
    label: String,
    value: String,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            color = AuthColors.MutedText,
            style = MaterialTheme.typography.bodySmall,
        )
        Text(
            text = value,
            color = AuthColors.PrimaryText,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
        )
    }
}
