package com.aseca.mobile.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aseca.mobile.models.Stock
import com.aseca.mobile.models.WatchlistComparisonItem
import com.aseca.mobile.models.WatchlistItem
import com.aseca.mobile.repository.PortfolioRepository
import kotlinx.coroutines.launch

data class WatchlistUiState(
    val loading: Boolean = false,
    val actionLoading: Boolean = false,
    val error: String = "",
    val success: String = "",
    val selectedTicker: String = "",
    val items: List<WatchlistItem> = emptyList(),
    val comparison: List<WatchlistComparisonItem> = emptyList(),
    val availableStocks: List<Stock> = emptyList(),
)

class WatchlistViewModel(
    private val portfolioRepository: PortfolioRepository,
) : ViewModel() {
    var uiState by mutableStateOf(WatchlistUiState())
        private set

    fun loadWatchlist(accessToken: String) {
        if (accessToken.isBlank()) {
            uiState = uiState.copy(error = "No hay sesión activa.", loading = false)
            return
        }

        viewModelScope.launch {
            uiState = uiState.copy(loading = true, error = "", success = "")
            try {
                refreshWatchlist(accessToken, loading = false)
            } catch (exception: Exception) {
                uiState = uiState.copy(
                    loading = false,
                    error = exception.message ?: "No pudimos cargar la watchlist.",
                )
            }
        }
    }

    fun onTickerChange(ticker: String) {
        uiState = uiState.copy(
            selectedTicker = ticker.trim().uppercase(),
            error = "",
            success = "",
        )
    }

    fun add(accessToken: String) {
        val ticker = uiState.selectedTicker.trim().uppercase()

        if (ticker.isBlank()) {
            uiState = uiState.copy(error = "Seleccioná una acción.", success = "")
            return
        }

        viewModelScope.launch {
            uiState = uiState.copy(actionLoading = true, error = "", success = "")
            try {
                portfolioRepository.addToWatchlist(accessToken, ticker)
                refreshWatchlist(accessToken, loading = false)
                uiState = uiState.copy(
                    actionLoading = false,
                    selectedTicker = "",
                    success = "$ticker agregado a tu watchlist.",
                )
            } catch (exception: Exception) {
                uiState = uiState.copy(
                    actionLoading = false,
                    error = exception.message ?: "No se pudo agregar a la watchlist.",
                )
            }
        }
    }

    fun remove(accessToken: String, ticker: String) {
        viewModelScope.launch {
            uiState = uiState.copy(actionLoading = true, error = "", success = "")
            try {
                portfolioRepository.removeFromWatchlist(accessToken, ticker)
                refreshWatchlist(accessToken, loading = false)
                uiState = uiState.copy(
                    actionLoading = false,
                    success = "$ticker eliminado de tu watchlist.",
                )
            } catch (exception: Exception) {
                uiState = uiState.copy(
                    actionLoading = false,
                    error = exception.message ?: "No se pudo eliminar de la watchlist.",
                )
            }
        }
    }

    private suspend fun refreshWatchlist(
        accessToken: String,
        loading: Boolean,
    ) {
        val items = portfolioRepository.getWatchlist(accessToken)
        val comparison = runCatching {
            portfolioRepository.getWatchlistComparison(accessToken)
        }.getOrDefault(emptyList())
        val stocks = portfolioRepository.getStocks(accessToken)
        val selectedTickers = items.map { item -> item.stock.ticker }.toSet()

        uiState = uiState.copy(
            loading = loading,
            items = items,
            comparison = comparison,
            availableStocks = stocks.filterNot { stock -> stock.ticker in selectedTickers },
        )
    }
}
