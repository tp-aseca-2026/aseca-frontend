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
    val loadingSearch: Boolean = false,
    val loadingDetails: Boolean = false,
    val error: String = "",
    val companies: List<EdgarCompany> = emptyList(),
    val selectedCompany: EdgarCompany? = null,
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

    fun search() {
        val query = uiState.query.trim()
        if (query.isBlank()) {
            uiState = uiState.copy(error = "Ingresá un ticker o nombre de empresa.")
            return
        }

        viewModelScope.launch {
            uiState = uiState.copy(
                loadingSearch = true,
                error = "",
                selectedCompany = null,
                metrics = null,
                filings = emptyList(),
                historical = null,
            )

            try {
                val companies = edgarRepository.searchCompanies(query)
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

    fun selectCompany(company: EdgarCompany) {
        viewModelScope.launch {
            uiState = uiState.copy(
                loadingDetails = true,
                error = "",
                selectedCompany = company,
                metrics = null,
                filings = emptyList(),
                historical = null,
            )

            try {
                val ticker = company.ticker
                val metrics = edgarRepository.getCompanyMetrics(ticker)
                val filings = edgarRepository.getCompanyFilings(ticker)
                val historical = edgarRepository.getHistoricalMetrics(ticker)

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
