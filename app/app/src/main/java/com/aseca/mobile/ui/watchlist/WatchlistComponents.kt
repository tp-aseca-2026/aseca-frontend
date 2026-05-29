package com.aseca.mobile.ui.watchlist

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.aseca.mobile.models.Stock
import com.aseca.mobile.models.WatchlistComparisonItem
import com.aseca.mobile.models.WatchlistItem
import com.aseca.mobile.models.WatchlistMetric
import com.aseca.mobile.ui.AuthColors
import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

@Composable
fun AddWatchlistCard(
    selectedTicker: String,
    availableStocks: List<Stock>,
    actionLoading: Boolean,
    onTickerChange: (String) -> Unit,
    onAdd: () -> Unit,
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
            Text(
                text = "Agregar empresa",
                color = AuthColors.PrimaryText,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )

            OutlinedTextField(
                value = selectedTicker,
                onValueChange = onTickerChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Ticker") },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AuthColors.Accent,
                    unfocusedBorderColor = AuthColors.Border,
                    focusedLabelColor = AuthColors.Accent,
                    unfocusedLabelColor = AuthColors.MutedText,
                    focusedTextColor = AuthColors.PrimaryText,
                    unfocusedTextColor = AuthColors.PrimaryText,
                    cursorColor = AuthColors.Accent,
                ),
            )

            if (availableStocks.isNotEmpty()) {
                Text(
                    text = "Disponibles",
                    color = AuthColors.MutedText,
                    style = MaterialTheme.typography.bodySmall,
                )

                availableStocks.take(6).forEach { stock ->
                    StockChoice(
                        stock = stock,
                        selected = stock.ticker == selectedTicker,
                        onClick = { onTickerChange(stock.ticker) },
                    )
                }
            }

            Button(
                onClick = onAdd,
                enabled = !actionLoading,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AuthColors.Accent,
                    contentColor = Color(0xFF06100B),
                    disabledContainerColor = Color(0xFF183425),
                    disabledContentColor = AuthColors.MutedText,
                ),
                shape = RoundedCornerShape(14.dp),
            ) {
                Text(if (actionLoading) "Guardando..." else "+ Agregar")
            }
        }
    }
}

@Composable
fun WatchlistCard(
    item: WatchlistItem,
    actionLoading: Boolean,
    onRemove: () -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color(0xFF0C1017),
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, AuthColors.Border),
    ) {
        Row(
            modifier = Modifier.padding(18.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.stock.ticker,
                    color = AuthColors.PrimaryText,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = item.stock.companyName ?: "Sin nombre registrado",
                    color = AuthColors.MutedText,
                    style = MaterialTheme.typography.bodySmall,
                )
            }

            Button(
                onClick = onRemove,
                enabled = !actionLoading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF23101A),
                    contentColor = AuthColors.Error,
                    disabledContainerColor = Color(0xFF151820),
                    disabledContentColor = AuthColors.MutedText,
                ),
                shape = RoundedCornerShape(12.dp),
            ) {
                Text("Eliminar")
            }
        }
    }
}

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
fun SectionTitle(text: String) {
    Text(
        text = text,
        color = AuthColors.PrimaryText,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
    )
}

@Composable
fun WatchlistLoading() {
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
fun WatchlistMessage(
    text: String,
    error: Boolean = false,
) {
    Text(
        text = text,
        color = if (error) AuthColors.Error else AuthColors.MutedText,
        style = MaterialTheme.typography.bodyMedium,
    )
}

@Composable
private fun StockChoice(
    stock: Stock,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = if (selected) Color(0xFF10291C) else Color(0xFF060A0F),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(
            1.dp,
            if (selected) AuthColors.Accent else AuthColors.Border,
        ),
        onClick = onClick,
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stock.ticker,
                    color = AuthColors.PrimaryText,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = stock.companyName ?: "Sin nombre registrado",
                    color = AuthColors.MutedText,
                    style = MaterialTheme.typography.bodySmall,
                )
            }

            Text(
                text = if (selected) "Seleccionada" else "Elegir",
                color = if (selected) AuthColors.Accent else AuthColors.MutedText,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
            )
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

private fun WatchlistMetric?.money(): String {
    return this?.value?.let { value ->
        "${moneyFormatter.format(value.toCompactAmount())}${value.compactSuffix()}"
    } ?: "No disponible"
}

private fun WatchlistMetric?.number(): String {
    return this?.value?.let { value ->
        numberFormatter.format(value)
    } ?: "No disponible"
}

private fun Double.toCompactAmount(): Double {
    val absolute = kotlin.math.abs(this)
    return when {
        absolute >= 1_000_000_000 -> this / 1_000_000_000
        absolute >= 1_000_000 -> this / 1_000_000
        absolute >= 1_000 -> this / 1_000
        else -> this
    }
}

private fun Double.compactSuffix(): String {
    val absolute = kotlin.math.abs(this)
    return when {
        absolute >= 1_000_000_000 -> "B"
        absolute >= 1_000_000 -> "M"
        absolute >= 1_000 -> "K"
        else -> ""
    }
}

private val moneyFormatter = NumberFormat.getCurrencyInstance(Locale.US).apply {
    currency = Currency.getInstance("USD")
    maximumFractionDigits = 2
}

private val numberFormatter = NumberFormat.getNumberInstance(Locale.US).apply {
    maximumFractionDigits = 2
}
