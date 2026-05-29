package com.aseca.mobile.ui.portfolio

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.aseca.mobile.models.Stock
import com.aseca.mobile.ui.AuthColors
import com.aseca.mobile.viewmodel.TransactionMode

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
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
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
