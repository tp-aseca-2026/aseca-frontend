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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aseca.mobile.ui.AuthColors
import com.aseca.mobile.ui.edgar.CompanyCard
import com.aseca.mobile.ui.edgar.EdgarLoading
import com.aseca.mobile.ui.edgar.EdgarMessage
import com.aseca.mobile.ui.edgar.FilingCard
import com.aseca.mobile.ui.edgar.HistoricalSection
import com.aseca.mobile.ui.edgar.MetricsSection
import com.aseca.mobile.ui.edgar.SearchCard
import com.aseca.mobile.ui.edgar.SectionTitle
import com.aseca.mobile.ui.edgar.SelectedCompanyHeader
import com.aseca.mobile.viewmodel.EdgarViewModel

@Composable
fun EdgarScreen(
    viewModel: EdgarViewModel,
    accessToken: String,
    modifier: Modifier = Modifier,
) {
    val state = viewModel.uiState

    Surface(
        modifier = modifier.fillMaxSize(),
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
            item { EdgarHeader() }

            item {
                SearchCard(
                    query = state.query,
                    loading = state.loadingSearch,
                    onQueryChange = viewModel::onQueryChange,
                    onSearch = { viewModel.search(accessToken) },
                )
            }

            if (state.error.isNotBlank()) {
                item { EdgarMessage(state.error, error = true) }
            }

            if (state.loadingSearch) {
                item { EdgarLoading("Buscando empresas...") }
            }

            state.selectedCompany?.let { company ->
                item { SelectedCompanyHeader(company) }
            }

            if (state.loadingDetails) {
                item { EdgarLoading("Cargando métricas, histórico y filings...") }
            }

            state.metrics?.let { metrics ->
                item { MetricsSection(metrics) }
            }

            state.historical?.let { historical ->
                item {
                    HistoricalSection(
                        historical = historical,
                        activeMetric = state.activeHistoricalMetric,
                        onMetricSelected = viewModel::selectHistoricalMetric,
                    )
                }
            }

            if (state.filings.isNotEmpty()) {
                item { SectionTitle("Filings recientes") }
                items(
                    items = state.filings.take(6),
                    key = { filing -> filing.accessionNumber },
                ) { filing ->
                    FilingCard(filing)
                }
            }

            if (!state.loadingSearch && state.companies.isEmpty() && state.error.isBlank()) {
                item {
                    EdgarMessage(
                        if (state.hasSearched) {
                            "No hay resultados para mostrar."
                        } else {
                            "Buscá por ticker o nombre para ver resultados."
                        },
                    )
                }
            }

            if (state.companies.isNotEmpty()) {
                item { SectionTitle("Resultados") }
                items(
                    items = state.companies,
                    key = { company -> company.cik },
                ) { company ->
                    CompanyCard(
                        company = company,
                        selected = state.selectedCompany?.cik == company.cik,
                        onClick = { viewModel.selectCompany(accessToken, company) },
                    )
                }
            }
        }
    }
}

@Composable
private fun EdgarHeader() {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            text = "SEC EDGAR",
            color = AuthColors.Accent,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = "Buscar empresa",
            color = AuthColors.PrimaryText,
            style = MaterialTheme.typography.headlineLarge,
            fontSize = 36.sp,
            fontWeight = FontWeight.Normal,
        )
        Text(
            text = "Consultá métricas, evolución histórica y filings reportados ante la SEC.",
            color = AuthColors.MutedText,
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}
