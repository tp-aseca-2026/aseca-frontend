package com.aseca.mobile.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aseca.mobile.models.LatestPriceSnapshots
import com.aseca.mobile.models.PortfolioSummary
import com.aseca.mobile.models.PriceSnapshot
import com.aseca.mobile.ui.AuthColors
import com.aseca.mobile.ui.portfolio.PortfolioActions
import com.aseca.mobile.ui.portfolio.PortfolioPositionCard
import com.aseca.mobile.ui.portfolio.TransactionDialog
import com.aseca.mobile.viewmodel.PortfolioViewModel
import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

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

            Button(
                onClick = { viewModel.updatePrices(accessToken) },
                enabled = !state.priceUpdateLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF0C1017),
                    contentColor = AuthColors.PrimaryText,
                    disabledContainerColor = Color(0xFF151820),
                    disabledContentColor = AuthColors.MutedText,
                ),
                shape = RoundedCornerShape(18.dp),
            ) {
                Text(if (state.priceUpdateLoading) "Actualizando precios..." else "Actualizar precios")
            }

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

@Composable
private fun HomeHeader(onLogout: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column {
            Text(
                text = "StockFolio",
                color = AuthColors.PrimaryText,
                style = MaterialTheme.typography.headlineLarge,
                fontSize = 34.sp,
                fontWeight = FontWeight.Normal,
            )
            Text(
                text = "Dashboard",
                color = AuthColors.Accent,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )
        }

        Button(
            onClick = onLogout,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF0C1017),
                contentColor = AuthColors.PrimaryText,
            ),
            shape = RoundedCornerShape(14.dp),
        ) {
            Text("Cerrar sesión")
        }
    }
}

@Composable
private fun DashboardHero(summary: PortfolioSummary?) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color(0xFF0C1017),
        shape = RoundedCornerShape(22.dp),
        border = BorderStroke(1.dp, AuthColors.Border),
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = "Estado actual de tu portfolio",
                color = AuthColors.PrimaryText,
                style = MaterialTheme.typography.headlineSmall,
                fontSize = 28.sp,
                fontWeight = FontWeight.Normal,
            )
            Text(
                text = "Valor total",
                color = AuthColors.MutedText,
                style = MaterialTheme.typography.bodyMedium,
            )
            Text(
                text = summary?.currentValue.moneyOrEmpty(),
                color = AuthColors.PrimaryText,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = "Última actualización: ${summary?.lastPriceUpdatedAt ?: "sin registro"}",
                color = AuthColors.MutedText,
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}

@Composable
private fun LatestPricesCard(latestPriceSnapshots: LatestPriceSnapshots?) {
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
                    text = "Últimos precios",
                    color = AuthColors.PrimaryText,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = "Actualización: ${latestPriceSnapshots?.lastUpdatedAt ?: "sin registro"}",
                    color = AuthColors.MutedText,
                    style = MaterialTheme.typography.bodySmall,
                )
            }

            val prices = latestPriceSnapshots?.prices.orEmpty()
            if (prices.isEmpty()) {
                HomeMessage("Todavía no hay precios actualizados.")
            } else {
                prices.take(5).forEach { snapshot ->
                    PriceSnapshotRow(snapshot)
                }

                if (prices.size > 5) {
                    HomeMessage("Mostrando 5 de ${prices.size} precios.")
                }
            }
        }
    }
}

@Composable
private fun PriceSnapshotRow(snapshot: PriceSnapshot) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color(0xFF060A0F),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, AuthColors.Border),
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = snapshot.ticker,
                    color = AuthColors.PrimaryText,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = "${snapshot.source} · ${snapshot.fetchedAt ?: "sin fecha"}",
                    color = AuthColors.MutedText,
                    style = MaterialTheme.typography.bodySmall,
                )
            }

            Text(
                text = snapshot.price.money(),
                color = AuthColors.Accent,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Composable
private fun HomeMetrics(
    summary: PortfolioSummary,
    positionsCount: Int,
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            MetricCard(
                title = "P&L total",
                value = summary.totalProfitLoss.moneyOrEmpty(),
                positive = (summary.totalProfitLoss ?: 0.0) >= 0,
                modifier = Modifier.weight(1f),
            )
            MetricCard(
                title = "P&L %",
                value = summary.unrealizedProfitLossPercentage.percentOrEmpty(),
                positive = (summary.unrealizedProfitLossPercentage ?: 0.0) >= 0,
                modifier = Modifier.weight(1f),
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            MetricCard(
                title = "Costo",
                value = summary.totalCostBasis.money(),
                modifier = Modifier.weight(1f),
            )
            MetricCard(
                title = "Posiciones",
                value = positionsCount.toString(),
                subtitle = "activas",
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun MetricCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    positive: Boolean = false,
) {
    Surface(
        modifier = modifier,
        color = Color(0xFF0C1017),
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, AuthColors.Border),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = title,
                color = AuthColors.MutedText,
                style = MaterialTheme.typography.bodySmall,
            )
            Text(
                text = value,
                color = if (positive) AuthColors.Accent else AuthColors.PrimaryText,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    color = AuthColors.MutedText,
                    style = MaterialTheme.typography.bodySmall,
                )
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
private fun HomeLoading() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 28.dp),
        horizontalArrangement = Arrangement.Center,
    ) {
        CircularProgressIndicator(color = AuthColors.Accent)
    }
}

@Composable
private fun HomeMessage(
    text: String,
    error: Boolean = false,
) {
    Text(
        text = text,
        color = if (error) AuthColors.Error else AuthColors.MutedText,
        style = MaterialTheme.typography.bodyMedium,
    )
}

private fun Double.money(): String {
    return moneyFormatter.format(this)
}

private fun Double?.moneyOrEmpty(): String {
    return this?.money() ?: "Sin precio"
}

private fun Double?.percentOrEmpty(): String {
    val value = this ?: return "Sin dato"
    val prefix = if (value >= 0) "+" else ""
    return "$prefix${"%.2f".format(Locale.US, value)}%"
}

private val moneyFormatter = NumberFormat.getCurrencyInstance(Locale.US).apply {
    currency = Currency.getInstance("USD")
    maximumFractionDigits = 2
    minimumFractionDigits = 2
}
