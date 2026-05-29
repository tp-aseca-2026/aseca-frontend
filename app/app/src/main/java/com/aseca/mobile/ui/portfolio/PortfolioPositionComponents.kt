package com.aseca.mobile.ui.portfolio

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
import com.aseca.mobile.models.PortfolioPosition
import com.aseca.mobile.ui.AuthColors

@Composable
fun PortfolioPositionCard(
    position: PortfolioPosition,
    onBuy: () -> Unit,
    onSell: () -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color(0xFF0C1017),
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, AuthColors.Border),
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = position.ticker,
                        color = AuthColors.PrimaryText,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = position.companyName ?: "Sin nombre registrado",
                        color = AuthColors.MutedText,
                        style = MaterialTheme.typography.bodySmall,
                    )
                }

                Text(
                    text = position.unrealizedProfitLossPercentage.percentOrEmpty(),
                    color = position.unrealizedProfitLossPercentage.profitColor(),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                )
            }

            SummaryRow("Cantidad", position.quantity.trimmed())
            SummaryRow("Precio compra", position.averageBuyPrice.money())
            SummaryRow("Precio actual", position.latestPrice.moneyOrEmpty("Sin precio"))
            SummaryRow("Valor actual", position.currentValue.moneyOrEmpty())

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                PortfolioActionButton(
                    text = "Comprar más",
                    onClick = onBuy,
                    modifier = Modifier.weight(1f),
                    automationId = "portfolio_position_buy_${position.ticker}",
                )
                PortfolioActionButton(
                    text = "Vender",
                    onClick = onSell,
                    primary = false,
                    modifier = Modifier.weight(1f),
                    automationId = "portfolio_position_sell_${position.ticker}",
                )
            }
        }
    }
}
