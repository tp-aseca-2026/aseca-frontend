package com.aseca.mobile.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aseca.mobile.models.LatestPriceSnapshots
import com.aseca.mobile.models.PortfolioResponse
import com.aseca.mobile.models.Stock
import com.aseca.mobile.repository.PortfolioRepository
import kotlinx.coroutines.launch

enum class TransactionMode {
    Buy,
    Sell,
}

data class PortfolioUiState(
    val loading: Boolean = false,
    val error: String = "",
    val portfolio: PortfolioResponse? = null,
    val latestPriceSnapshots: LatestPriceSnapshots? = null,
    val stocks: List<Stock> = emptyList(),
    val transactionMode: TransactionMode = TransactionMode.Buy,
    val transactionModalOpen: Boolean = false,
    val selectedTicker: String = "",
    val quantity: String = "",
    val transactionLoading: Boolean = false,
    val transactionError: String = "",
    val priceUpdateLoading: Boolean = false,
    val priceUpdateMessage: String = "",
    val priceUpdateError: String = "",
)

class PortfolioViewModel(
    private val portfolioRepository: PortfolioRepository,
) : ViewModel() {
    var uiState by mutableStateOf(PortfolioUiState())
        private set

    fun loadPortfolio(accessToken: String) {
        if (accessToken.isBlank()) {
            uiState = uiState.copy(error = "No hay sesión activa.", loading = false)
            return
        }

        viewModelScope.launch {
            uiState = uiState.copy(loading = true, error = "")
            try {
                val portfolio = portfolioRepository.getPortfolio(accessToken)
                val stocks = portfolioRepository.getStocks(accessToken)
                val latestPriceSnapshots = runCatching {
                    portfolioRepository.getLatestPriceSnapshots(accessToken)
                }.getOrNull()

                uiState = uiState.copy(
                    loading = false,
                    portfolio = portfolio,
                    latestPriceSnapshots = latestPriceSnapshots,
                    stocks = stocks,
                )
            } catch (exception: Exception) {
                uiState = uiState.copy(
                    loading = false,
                    error = exception.message ?: "No se pudo cargar el portfolio.",
                )
            }
        }
    }

    fun openBuy(defaultTicker: String = "") {
        openTransaction(TransactionMode.Buy, defaultTicker)
    }

    fun openSell(defaultTicker: String = "") {
        openTransaction(TransactionMode.Sell, defaultTicker)
    }

    fun closeTransaction() {
        uiState = uiState.copy(
            transactionModalOpen = false,
            selectedTicker = "",
            quantity = "",
            transactionError = "",
        )
    }

    fun onTickerChange(ticker: String) {
        uiState = uiState.copy(selectedTicker = ticker.uppercase())
    }

    fun onQuantityChange(quantity: String) {
        uiState = uiState.copy(quantity = quantity.filter(Char::isDigit))
    }

    fun submitTransaction(accessToken: String) {
        val ticker = uiState.selectedTicker.trim().uppercase()
        val quantity = uiState.quantity.toIntOrNull()

        if (ticker.isBlank()) {
            uiState = uiState.copy(transactionError = "Ingresá un ticker válido.")
            return
        }

        if (quantity == null || quantity <= 0) {
            uiState = uiState.copy(transactionError = "La cantidad debe ser un entero positivo.")
            return
        }

        viewModelScope.launch {
            uiState = uiState.copy(transactionLoading = true, transactionError = "")
            try {
                if (uiState.transactionMode == TransactionMode.Buy) {
                    portfolioRepository.buy(accessToken, ticker, quantity)
                } else {
                    portfolioRepository.sell(accessToken, ticker, quantity)
                }

                val portfolio = portfolioRepository.getPortfolio(accessToken)
                uiState = uiState.copy(
                    loading = false,
                    portfolio = portfolio,
                    transactionLoading = false,
                    transactionModalOpen = false,
                    selectedTicker = "",
                    quantity = "",
                    transactionError = "",
                )
            } catch (exception: Exception) {
                val fallback = if (uiState.transactionMode == TransactionMode.Buy) {
                    "No se pudo registrar la compra. Verificá ticker y precio."
                } else {
                    "No se pudo registrar la venta. Verificá acciones suficientes."
                }

                uiState = uiState.copy(
                    transactionLoading = false,
                    transactionError = exception.message ?: fallback,
                )
            }
        }
    }

    fun updatePrices(accessToken: String) {
        if (accessToken.isBlank()) {
            uiState = uiState.copy(priceUpdateError = "No hay sesión activa.")
            return
        }

        viewModelScope.launch {
            uiState = uiState.copy(
                priceUpdateLoading = true,
                priceUpdateMessage = "",
                priceUpdateError = "",
            )

            try {
                val stocks = uiState.stocks.ifEmpty {
                    portfolioRepository.getStocks(accessToken)
                }
                val tickers = stocks.map { stock -> stock.ticker }

                if (tickers.isEmpty()) {
                    uiState = uiState.copy(
                        priceUpdateLoading = false,
                        priceUpdateError = "No hay acciones cargadas para actualizar.",
                    )
                    return@launch
                }

                portfolioRepository.updatePriceSnapshots(accessToken, tickers)

                val portfolio = portfolioRepository.getPortfolio(accessToken)
                val refreshedStocks = portfolioRepository.getStocks(accessToken)
                val latestPriceSnapshots = portfolioRepository.getLatestPriceSnapshots(accessToken)

                uiState = uiState.copy(
                    portfolio = portfolio,
                    stocks = refreshedStocks,
                    latestPriceSnapshots = latestPriceSnapshots,
                    priceUpdateLoading = false,
                    priceUpdateMessage = "Precios actualizados.",
                    priceUpdateError = "",
                )
            } catch (exception: Exception) {
                uiState = uiState.copy(
                    priceUpdateLoading = false,
                    priceUpdateError = exception.message ?: "No se pudieron actualizar los precios.",
                )
            }
        }
    }

    private fun openTransaction(mode: TransactionMode, defaultTicker: String) {
        uiState = uiState.copy(
            transactionMode = mode,
            transactionModalOpen = true,
            selectedTicker = defaultTicker,
            quantity = "",
            transactionError = "",
        )
    }
}
