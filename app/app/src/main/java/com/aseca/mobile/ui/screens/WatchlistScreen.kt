package com.aseca.mobile.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aseca.mobile.ui.AuthColors
import com.aseca.mobile.ui.watchlist.AddWatchlistCard
import com.aseca.mobile.ui.watchlist.ComparisonSection
import com.aseca.mobile.ui.watchlist.SectionTitle
import com.aseca.mobile.ui.watchlist.WatchlistCard
import com.aseca.mobile.ui.watchlist.WatchlistLoading
import com.aseca.mobile.ui.watchlist.WatchlistMessage
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

            item { WatchlistHeader() }

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

            item {
                ComparisonSection(
                    loading = state.loading,
                    comparison = state.comparison,
                )
            }
        }
    }
}

@Composable
private fun WatchlistHeader() {
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
