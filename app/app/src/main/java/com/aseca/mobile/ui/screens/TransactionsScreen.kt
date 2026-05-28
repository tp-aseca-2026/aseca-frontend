package com.aseca.mobile.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aseca.mobile.models.Stock
import com.aseca.mobile.models.Transaction
import com.aseca.mobile.ui.AuthColors
import com.aseca.mobile.viewmodel.TransactionsViewModel
import java.text.NumberFormat
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.Currency
import java.util.Locale

@Composable
fun TransactionsScreen(
    viewModel: TransactionsViewModel,
    accessToken: String,
    onBack: () -> Unit,
) {
    val state = viewModel.uiState

    LaunchedEffect(accessToken) {
        viewModel.loadTransactions(accessToken)
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = AuthColors.Background,
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = 24.dp,
                top = 40.dp,
                end = 24.dp,
                bottom = 96.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            item {
                TextButton(onClick = onBack) {
                    Text("← Home", color = AuthColors.MutedText)
                }
            }

            item {
                Text(
                    text = "Historial de transacciones",
                    color = AuthColors.PrimaryText,
                    style = MaterialTheme.typography.headlineLarge,
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Normal,
                )
            }

            when {
                state.loading -> item { TransactionsLoading() }
                state.error.isNotBlank() -> item {
                    TransactionsMessage(state.error, error = true)
                }
                state.transactions.isEmpty() -> item {
                    TransactionsMessage("Todavía no hay transacciones registradas.")
                }
                else -> itemsIndexed(
                    items = state.transactions,
                    key = { index, transaction -> transaction.id.takeIf { it > 0 } ?: index },
                ) { _, transaction ->
                    TransactionCard(
                        transaction = transaction,
                        stock = state.stocksById[transaction.stockId],
                    )
                }
            }
        }
    }
}

@Composable
private fun TransactionCard(
    transaction: Transaction,
    stock: Stock?,
) {
    val ticker = stock?.ticker ?: "Stock #${transaction.stockId}"
    val companyName = stock?.companyName
    val isBuy = transaction.type == "BUY"

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
                        text = "${if (isBuy) "Compra" else "Venta"} $ticker",
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

@Composable
private fun TransactionsLoading() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp),
        horizontalArrangement = Arrangement.Center,
    ) {
        CircularProgressIndicator(
            color = AuthColors.Accent,
            strokeWidth = 2.dp,
        )
    }
}

@Composable
private fun TransactionsMessage(
    text: String,
    error: Boolean = false,
) {
    Text(
        text = text,
        color = if (error) AuthColors.Error else AuthColors.MutedText,
        style = MaterialTheme.typography.bodyMedium,
        modifier = Modifier.padding(vertical = 12.dp),
    )
}

private fun Double.money(): String {
    return moneyFormatter.format(this)
}

private fun String?.formatDate(): String {
    if (isNullOrBlank()) return "Sin fecha"

    return try {
        val parsed = OffsetDateTime.parse(this)
        parsed.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
    } catch (_: Exception) {
        this
    }
}

private val moneyFormatter = NumberFormat.getCurrencyInstance(Locale.US).apply {
    currency = Currency.getInstance("USD")
    maximumFractionDigits = 2
    minimumFractionDigits = 2
}
