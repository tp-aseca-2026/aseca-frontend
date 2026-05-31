package com.aseca.mobile.ui.portfolio

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
import com.aseca.mobile.models.PortfolioSummary
import com.aseca.mobile.ui.AuthColors

@Composable
fun PortfolioSummaryCard(summary: PortfolioSummary) {
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
            Text(
                text = "Resumen",
                color = AuthColors.PrimaryText,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )

            SummaryRow("Costo total", summary.totalCostBasis.money())
            SummaryRow("Valor actual", summary.currentValue.moneyOrEmpty())
            SummaryRow(
                label = "P&L total",
                value = summary.totalProfitLoss.moneyOrEmpty(),
                valueColor = summary.totalProfitLoss.profitColor(),
            )
            SummaryRow(
                label = "P&L no realizado",
                value = summary.unrealizedProfitLossPercentage.percentOrEmpty(),
                valueColor = summary.unrealizedProfitLossPercentage.profitColor(),
            )
        }
    }
}
