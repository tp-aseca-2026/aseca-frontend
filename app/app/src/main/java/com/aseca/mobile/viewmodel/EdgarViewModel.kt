package com.aseca.mobile.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aseca.mobile.models.EdgarCompany
import com.aseca.mobile.models.EdgarFiling
import com.aseca.mobile.models.EdgarHistoricalMetrics
import com.aseca.mobile.models.EdgarMetrics
import com.aseca.mobile.repository.EdgarRepository
import kotlinx.coroutines.launch

data class EdgarUiState(
    val query: String = "",
    val hasSearched: Boolean = false,
    val loadingSearch: Boolean = false,
    val loadingDetails: Boolean = false,
    val error: String = "",
    val companies: List<EdgarCompany> = emptyList(),
    val selectedCompany: EdgarCompany? = null,
    val activeHistoricalMetric: String = "revenue",
    val metrics: EdgarMetrics? = null,
    val filings: List<EdgarFiling> = emptyList(),
    val historical: EdgarHistoricalMetrics? = null,
)

class EdgarViewModel(
    private val edgarRepository: EdgarRepository,
) : ViewModel() {
    var uiState by mutableStateOf(EdgarUiState())
        private set

    fun onQueryChange(query: String) {
        uiState = uiState.copy(query = query, error = "")
    }

    fun selectHistoricalMetric(metric: String) {
        uiState = uiState.copy(activeHistoricalMetric = metric)
    }

    fun search(accessToken: String) {
        val query = uiState.query.trim()
        if (query.isBlank()) {
            uiState = uiState.copy(error = "Ingresá un ticker o nombre de empresa.")
            return
        }

        viewModelScope.launch {
            uiState = uiState.copy(
                hasSearched = true,
                loadingSearch = true,
                error = "",
                selectedCompany = null,
                activeHistoricalMetric = "revenue",
                metrics = null,
                filings = emptyList(),
                historical = null,
            )

            try {
                val companies = edgarRepository.searchCompanies(accessToken, query)
                uiState = uiState.copy(
                    loadingSearch = false,
                    companies = companies,
                    error = if (companies.isEmpty()) "No encontramos empresas para esa búsqueda." else "",
                )
            } catch (exception: Exception) {
                uiState = uiState.copy(
                    loadingSearch = false,
                    error = exception.message ?: "No pudimos buscar empresas en EDGAR.",
                )
            }
        }
    }

    fun selectCompany(accessToken: String, company: EdgarCompany) {
        viewModelScope.launch {
            uiState = uiState.copy(
                loadingDetails = true,
                error = "",
                selectedCompany = company,
                activeHistoricalMetric = "revenue",
                metrics = null,
                filings = emptyList(),
                historical = null,
            )

            try {
                val ticker = company.ticker
                val metrics = edgarRepository.getCompanyMetrics(accessToken, ticker)
                val filings = edgarRepository.getCompanyFilings(accessToken, ticker)
                val historical = edgarRepository.getHistoricalMetrics(accessToken, ticker)

                uiState = uiState.copy(
                    loadingDetails = false,
                    metrics = metrics,
                    filings = filings,
                    historical = historical,
                )
            } catch (exception: Exception) {
                uiState = uiState.copy(
                    loadingDetails = false,
                    error = exception.message ?: "No pudimos cargar los datos financieros.",
                )
            }
        }
    }
}
