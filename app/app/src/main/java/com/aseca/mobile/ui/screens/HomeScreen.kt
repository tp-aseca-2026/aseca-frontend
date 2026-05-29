package com.aseca.mobile.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.aseca.mobile.ui.AuthColors
import com.aseca.mobile.ui.home.DashboardHero
import com.aseca.mobile.ui.home.HomeHeader
import com.aseca.mobile.ui.home.HomeLoading
import com.aseca.mobile.ui.home.HomeMessage
import com.aseca.mobile.ui.home.HomeMetrics
import com.aseca.mobile.ui.home.LatestPricesCard
import com.aseca.mobile.ui.home.SectionTitle
import com.aseca.mobile.ui.home.UpdatePricesButton
import com.aseca.mobile.ui.portfolio.PortfolioActions
import com.aseca.mobile.ui.portfolio.PortfolioPositionCard
import com.aseca.mobile.ui.portfolio.TransactionDialog
import com.aseca.mobile.viewmodel.PortfolioViewModel

@Composable
fun HomeScreen(
    accessToken: String,
    viewModel: PortfolioViewModel,
    modifier: Modifier = Modifier,
    onLogout: () -> Unit,
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 40.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            HomeHeader(onLogout = onLogout)
            DashboardHero(summary = portfolio?.summary)

            PortfolioActions(
                onBuy = { viewModel.openBuy() },
                onSell = { viewModel.openSell() },
            )

            UpdatePricesButton(
                loading = state.priceUpdateLoading,
                onClick = { viewModel.updatePrices(accessToken) },
            )

            if (state.priceUpdateMessage.isNotBlank()) {
                HomeMessage(state.priceUpdateMessage)
            }

            if (state.priceUpdateError.isNotBlank()) {
                HomeMessage(state.priceUpdateError, error = true)
            }

            LatestPricesCard(latestPriceSnapshots = state.latestPriceSnapshots)

            when {
                state.loading -> HomeLoading()
                state.error.isNotBlank() -> HomeMessage(state.error, error = true)
                portfolio == null -> HomeMessage("No se pudo cargar el dashboard.")
                positions.isEmpty() -> {
                    HomeMetrics(summary = portfolio.summary, positionsCount = 0)
                    HomeMessage("Todavía no tenés posiciones activas.")
                }
                else -> {
                    HomeMetrics(
                        summary = portfolio.summary,
                        positionsCount = positions.size,
                    )

                    SectionTitle("Mis posiciones")

                    positions.take(3).forEach { position ->
                        PortfolioPositionCard(
                            position = position,
                            onBuy = { viewModel.openBuy(position.ticker) },
                            onSell = { viewModel.openSell(position.ticker) },
                        )
                    }

                    if (positions.size > 3) {
                        HomeMessage("Mostrando 3 de ${positions.size} posiciones.")
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
