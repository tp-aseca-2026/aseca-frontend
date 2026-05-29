package com.aseca.mobile.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aseca.mobile.ui.AuthColors
import com.aseca.mobile.ui.portfolio.PortfolioActions
import com.aseca.mobile.ui.portfolio.PortfolioLoading
import com.aseca.mobile.ui.portfolio.PortfolioMessage
import com.aseca.mobile.ui.portfolio.PortfolioPositionCard
import com.aseca.mobile.ui.portfolio.PortfolioSummaryCard
import com.aseca.mobile.ui.portfolio.TransactionDialog
import com.aseca.mobile.viewmodel.PortfolioViewModel

@Composable
fun PortfolioScreen(
    viewModel: PortfolioViewModel,
    accessToken: String,
    modifier: Modifier = Modifier,
) {
    val state = viewModel.uiState
    val portfolio = state.portfolio
    val positions = portfolio?.positions.orEmpty()

    LaunchedEffect(accessToken) {
        viewModel.loadPortfolio(accessToken)
    }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = AuthColors.Background,
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
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
                    Text(
                        text = "Portfolio completo",
                        color = AuthColors.PrimaryText,
                        style = MaterialTheme.typography.headlineLarge,
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Normal,
                    )
                }

                item {
                    PortfolioActions(
                        onBuy = { viewModel.openBuy() },
                        onSell = { viewModel.openSell() },
                    )
                }

                when {
                    state.loading -> item { PortfolioLoading() }
                    state.error.isNotBlank() -> item {
                        PortfolioMessage(state.error, error = true)
                    }
                    portfolio == null -> item {
                        PortfolioMessage("No se pudo cargar el portfolio.")
                    }
                    positions.isEmpty() -> {
                        item { PortfolioSummaryCard(portfolio.summary) }
                        item { PortfolioMessage("Todavía no tenés posiciones activas.") }
                    }
                    else -> {
                        item { PortfolioSummaryCard(portfolio.summary) }

                        item {
                            Text(
                                text = "Mis posiciones",
                                color = AuthColors.PrimaryText,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                            )
                        }

                        items(
                            items = positions,
                            key = { position -> position.stockId },
                        ) { position ->
                            PortfolioPositionCard(
                                position = position,
                                onBuy = { viewModel.openBuy(position.ticker) },
                                onSell = { viewModel.openSell(position.ticker) },
                            )
                        }
                    }
                }
            }

            TransactionDialog(
                open = state.transactionModalOpen,
                mode = state.transactionMode,
                stocks = state.stocks,
                selectedTicker = state.selectedTicker,
                quantity = state.quantity,
                loading = state.transactionLoading,
                error = state.transactionError,
                onTickerChange = viewModel::onTickerChange,
                onQuantityChange = viewModel::onQuantityChange,
                onClose = viewModel::closeTransaction,
                onSubmit = { viewModel.submitTransaction(accessToken) },
            )
        }
    }
}
