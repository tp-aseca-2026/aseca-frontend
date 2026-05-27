package com.aseca.mobile.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
    onBack: () -> Unit,
) {
    val state = viewModel.uiState
    val portfolio = state.portfolio
    val positions = portfolio?.positions.orEmpty()

    LaunchedEffect(accessToken) {
        viewModel.loadPortfolio(accessToken)
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = AuthColors.Background,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 40.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {
                TextButton(onClick = onBack) {
                    Text("← Home", color = AuthColors.MutedText)
                }
            }

            Text(
                text = "Portfolio completo",
                color = AuthColors.PrimaryText,
                style = MaterialTheme.typography.headlineLarge,
                fontSize = 36.sp,
                fontWeight = FontWeight.Normal,
            )

            PortfolioActions(
                onBuy = { viewModel.openBuy() },
                onSell = { viewModel.openSell() },
            )

            when {
                state.loading -> PortfolioLoading()
                state.error.isNotBlank() -> PortfolioMessage(state.error, error = true)
                portfolio == null -> PortfolioMessage("No se pudo cargar el portfolio.")
                positions.isEmpty() -> {
                    PortfolioSummaryCard(portfolio.summary)
                    PortfolioMessage("Todavía no tenés posiciones activas.")
                }
                else -> {
                    PortfolioSummaryCard(portfolio.summary)

                    Text(
                        text = "Mis posiciones",
                        color = AuthColors.PrimaryText,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                    )

                    positions.forEach { position ->
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
