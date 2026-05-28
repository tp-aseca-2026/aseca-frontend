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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aseca.mobile.models.Stock
import com.aseca.mobile.models.WatchlistItem
import com.aseca.mobile.ui.AuthColors
import com.aseca.mobile.viewmodel.WatchlistViewModel

@Composable
fun WatchlistScreen(
    viewModel: WatchlistViewModel,
    accessToken: String,
    onBack: () -> Unit,
) {
    val state = viewModel.uiState

    LaunchedEffect(accessToken) {
        viewModel.loadWatchlist(accessToken)
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
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Watchlist",
                        color = AuthColors.PrimaryText,
                        style = MaterialTheme.typography.headlineLarge,
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Normal,
                    )
                    Text(
                        text = "Guardá empresas que querés seguir sin abrir una posición.",
                        color = AuthColors.MutedText,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }

            item {
                AddWatchlistCard(
                    selectedTicker = state.selectedTicker,
                    availableStocks = state.availableStocks,
                    actionLoading = state.actionLoading,
                    onTickerChange = viewModel::onTickerChange,
                    onAdd = { viewModel.add(accessToken) },
                )
            }

            if (state.error.isNotBlank()) {
                item { WatchlistMessage(state.error, error = true) }
            }

            if (state.success.isNotBlank()) {
                item { WatchlistMessage(state.success) }
            }

            when {
                state.loading -> item { WatchlistLoading() }
                state.items.isEmpty() -> item {
                    WatchlistMessage("Todavía no agregaste empresas a tu watchlist.")
                }
                else -> {
                    item { SectionTitle("Empresas guardadas") }
                    items(
                        items = state.items,
                        key = { item -> item.id },
                    ) { item ->
                        WatchlistCard(
                            item = item,
                            actionLoading = state.actionLoading,
                            onRemove = { viewModel.remove(accessToken, item.stock.ticker) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AddWatchlistCard(
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
private fun WatchlistCard(
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
private fun SectionTitle(text: String) {
    Text(
        text = text,
        color = AuthColors.PrimaryText,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
    )
}

@Composable
private fun WatchlistLoading() {
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
private fun WatchlistMessage(
    text: String,
    error: Boolean = false,
) {
    Text(
        text = text,
        color = if (error) AuthColors.Error else AuthColors.MutedText,
        style = MaterialTheme.typography.bodyMedium,
    )
}
