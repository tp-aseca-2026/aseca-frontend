package com.aseca.mobile.ui.portfolio

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.aseca.mobile.models.PortfolioPosition
import com.aseca.mobile.models.PortfolioSummary
import com.aseca.mobile.models.Stock
import com.aseca.mobile.ui.AuthColors
import com.aseca.mobile.viewmodel.TransactionMode
import java.text.NumberFormat
import java.util.Locale

@Composable
fun PortfolioLoading() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CircularProgressIndicator(
            color = AuthColors.Accent,
            strokeWidth = 2.dp,
        )
    }
}

@Composable
fun PortfolioMessage(
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

@Composable
fun PortfolioActions(
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
            Text(
                text = "Acciones rápidas",
                color = AuthColors.PrimaryText,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )

            PortfolioActionButton(text = "+ Registrar compra", onClick = onBuy)
            PortfolioActionButton(text = "− Registrar venta", onClick = onSell)
        }
    }
}

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
                )
                PortfolioActionButton(
                    text = "Vender",
                    onClick = onSell,
                    primary = false,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
fun TransactionDialog(
    open: Boolean,
    mode: TransactionMode,
    stocks: List<Stock>,
    selectedTicker: String,
    quantity: String,
    loading: Boolean,
    error: String,
    onTickerChange: (String) -> Unit,
    onQuantityChange: (String) -> Unit,
    onClose: () -> Unit,
    onSubmit: () -> Unit,
) {
    if (!open) return

    val title = if (mode == TransactionMode.Buy) "Registrar compra" else "Registrar venta"

    AlertDialog(
        onDismissRequest = { if (!loading) onClose() },
        containerColor = Color(0xFF0C1017),
        titleContentColor = AuthColors.PrimaryText,
        textContentColor = AuthColors.MutedText,
        title = { Text(title) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Text("Seleccioná una acción disponible y la cantidad que querés operar.")

                if (stocks.isNotEmpty()) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        stocks.forEach { stock ->
                            TickerChoice(
                                stock = stock,
                                selected = stock.ticker == selectedTicker,
                                onClick = { onTickerChange(stock.ticker) },
                            )
                        }
                    }
                } else {
                    PortfolioMessage(
                        text = "No hay acciones cargadas. Corré la seed del backend.",
                        error = true,
                    )
                }

                PortfolioTextField(
                    value = selectedTicker,
                    onValueChange = onTickerChange,
                    label = "Ticker",
                )

                PortfolioTextField(
                    value = quantity,
                    onValueChange = onQuantityChange,
                    label = "Cantidad",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                )

                if (error.isNotBlank()) {
                    Text(
                        text = error,
                        color = AuthColors.Error,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onSubmit,
                enabled = !loading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = AuthColors.Accent,
                    contentColor = AuthColors.ButtonText,
                ),
                shape = RoundedCornerShape(12.dp),
            ) {
                if (loading) {
                    CircularProgressIndicator(
                        color = AuthColors.ButtonText,
                        strokeWidth = 2.dp,
                        modifier = Modifier.height(18.dp),
                    )
                } else {
                    Text(title)
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onClose, enabled = !loading) {
                Text("Cancelar", color = AuthColors.PrimaryText)
            }
        },
    )
}

@Composable
private fun TickerChoice(
    stock: Stock,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val background = if (selected) AuthColors.Accent else Color(0xFF060A0F)
    val content = if (selected) AuthColors.ButtonText else AuthColors.PrimaryText

    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(
            containerColor = background,
            contentColor = content,
        ),
        shape = RoundedCornerShape(12.dp),
    ) {
        Text("${stock.ticker} · ${stock.companyName ?: "Sin nombre"}")
    }
}

@Composable
private fun PortfolioActionButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    primary: Boolean = true,
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (primary) AuthColors.Accent else Color(0xFF060A0F),
            contentColor = if (primary) AuthColors.ButtonText else AuthColors.PrimaryText,
        ),
        shape = RoundedCornerShape(12.dp),
    ) {
        Text(text)
    }
}

@Composable
private fun PortfolioTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        keyboardOptions = keyboardOptions,
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = AuthColors.PrimaryText,
            unfocusedTextColor = AuthColors.PrimaryText,
            focusedContainerColor = AuthColors.InputBackground,
            unfocusedContainerColor = AuthColors.InputBackground,
            focusedBorderColor = AuthColors.Accent,
            unfocusedBorderColor = AuthColors.Border,
            focusedLabelColor = AuthColors.Accent,
            unfocusedLabelColor = AuthColors.MutedText,
            cursorColor = AuthColors.Accent,
        ),
        shape = RoundedCornerShape(14.dp),
    )
}

@Composable
private fun SummaryRow(
    label: String,
    value: String,
    valueColor: Color = AuthColors.PrimaryText,
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
            color = valueColor,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
        )
    }
}

private fun Double.money(): String {
    return "USD ${moneyFormatter.format(this)}"
}

private fun Double?.moneyOrEmpty(fallback: String = "Sin datos"): String {
    return this?.money() ?: fallback
}

private fun Double?.percentOrEmpty(): String {
    val value = this ?: return "Sin datos"
    val sign = if (value >= 0) "+" else ""
    return "$sign${"%.2f".format(Locale.US, value)}%"
}

private fun Double?.profitColor(): Color {
    val value = this ?: return AuthColors.MutedText
    return if (value >= 0) AuthColors.Accent else AuthColors.Error
}

private fun Double.trimmed(): String {
    return if (this % 1.0 == 0.0) {
        toInt().toString()
    } else {
        "%.4f".format(Locale.US, this).trimEnd('0').trimEnd('.')
    }
}

private val moneyFormatter = NumberFormat.getNumberInstance(Locale.US).apply {
    minimumFractionDigits = 2
    maximumFractionDigits = 2
}
