package com.aseca.mobile.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aseca.mobile.models.Stock
import com.aseca.mobile.models.Transaction
import com.aseca.mobile.repository.PortfolioRepository
import kotlinx.coroutines.launch

data class TransactionsUiState(
    val loading: Boolean = false,
    val error: String = "",
    val transactions: List<Transaction> = emptyList(),
    val stocksById: Map<Int, Stock> = emptyMap(),
)

class TransactionsViewModel(
    private val portfolioRepository: PortfolioRepository,
) : ViewModel() {
    var uiState by mutableStateOf(TransactionsUiState())
        private set

    fun loadTransactions(accessToken: String) {
        if (accessToken.isBlank()) {
            uiState = uiState.copy(error = "No hay sesión activa.", loading = false)
            return
        }

        viewModelScope.launch {
            uiState = uiState.copy(loading = true, error = "")
            try {
                val transactions = portfolioRepository.getTransactions(accessToken)
                val stocks = portfolioRepository.getStocks(accessToken)

                uiState = uiState.copy(
                    loading = false,
                    transactions = transactions,
                    stocksById = stocks.associateBy { stock -> stock.id },
                )
            } catch (exception: Exception) {
                uiState = uiState.copy(
                    loading = false,
                    error = exception.message ?: "No se pudieron cargar las transacciones.",
                )
            }
        }
    }
}
