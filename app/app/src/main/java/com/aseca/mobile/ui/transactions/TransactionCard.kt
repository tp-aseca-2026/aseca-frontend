package com.aseca.mobile.ui.transactions

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
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.aseca.mobile.models.Stock
import com.aseca.mobile.models.Transaction
import com.aseca.mobile.ui.AuthColors

@Composable
fun TransactionCard(
    transaction: Transaction,
    stock: Stock?,
) {
    val ticker = stock?.ticker ?: "Stock #${transaction.stockId}"
    val companyName = stock?.companyName
    val isBuy = transaction.type == "BUY"

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .semantics {
                contentDescription = "transaction_${transaction.type}_${ticker}"
            },
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
                        text = "${if (isBuy) "Compra" else "Venta"} $ticker",
                        modifier = Modifier.semantics {
                            contentDescription = "transaction_ticker_${ticker}"
                        },
                        color = AuthColors.PrimaryText,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                    )
                    if (!companyName.isNullOrBlank()) {
                        Text(
                            text = companyName,
                            color = AuthColors.MutedText,
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                }

                Text(
                    text = if (isBuy) "BUY" else "SELL",
                    modifier = Modifier.semantics {
                        contentDescription = "transaction_type_${transaction.type}_${ticker}"
                    },
                    color = if (isBuy) AuthColors.Accent else AuthColors.Error,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                )
            }

            TransactionRow("Cantidad", "${transaction.quantity} acciones")
            TransactionRow("Precio", transaction.price.money())
            TransactionRow("Fecha", transaction.executedAt.formatDate())
        }
    }
}

@Composable
private fun TransactionRow(
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
            style = MaterialTheme.typography.bodyMedium,
        )
        Text(
            text = value,
            color = AuthColors.PrimaryText,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
        )
    }
}