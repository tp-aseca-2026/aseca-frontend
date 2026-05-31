package com.aseca.mobile.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
import com.aseca.mobile.ui.transactions.TransactionCard
import com.aseca.mobile.ui.transactions.TransactionsLoading
import com.aseca.mobile.ui.transactions.TransactionsMessage
import com.aseca.mobile.viewmodel.TransactionsViewModel

@Composable
fun TransactionsScreen(
    viewModel: TransactionsViewModel,
    accessToken: String,
    modifier: Modifier = Modifier,
) {
    val state = viewModel.uiState

    LaunchedEffect(accessToken) {
        viewModel.loadTransactions(accessToken)
    }

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
            item {
                Text(
                    text = "Historial de transacciones",
                    color = AuthColors.PrimaryText,
                    style = MaterialTheme.typography.headlineLarge,
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Normal,
                )
            }

            when {
                state.loading -> item { TransactionsLoading() }
                state.error.isNotBlank() -> item {
                    TransactionsMessage(state.error, error = true)
                }
                state.transactions.isEmpty() -> item {
                    TransactionsMessage("Todavía no hay transacciones registradas.")
                }
                else -> itemsIndexed(
                    items = state.transactions,
                    key = { index, transaction -> transaction.id.takeIf { it > 0 } ?: index },
                ) { _, transaction ->
                    TransactionCard(
                        transaction = transaction,
                        stock = state.stocksById[transaction.stockId],
                    )
                }
            }
        }
    }
}
