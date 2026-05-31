package com.aseca.mobile.ui.edgar

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.aseca.mobile.models.EdgarCompany
import com.aseca.mobile.ui.AuthColors

@Composable
fun SearchCard(
    query: String,
    loading: Boolean,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
) {
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
            OutlinedTextField(
                value = query,
                onValueChange = onQueryChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics { contentDescription = "edgar_search_input" },
                label = { Text("Ej: AAPL, Apple, Microsoft") },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AuthColors.Accent,
                    unfocusedBorderColor = AuthColors.Border,
                    focusedLabelColor = AuthColors.Accent,
                    unfocusedLabelColor = AuthColors.MutedText,
                    focusedTextColor = AuthColors.PrimaryText,
                    unfocusedTextColor = AuthColors.PrimaryText,
                    cursorColor = AuthColors.Accent,
                ),
            )

            Button(
                onClick = onSearch,
                enabled = !loading,
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics { contentDescription = "edgar_search_button" },
                colors = ButtonDefaults.buttonColors(
                    containerColor = AuthColors.Accent,
                    contentColor = Color(0xFF06100B),
                    disabledContainerColor = Color(0xFF183425),
                    disabledContentColor = AuthColors.MutedText,
                ),
                shape = RoundedCornerShape(14.dp),
            ) {
                Text(if (loading) "Buscando..." else "Buscar")
            }
        }
    }
}

@Composable
fun CompanyCard(
    company: EdgarCompany,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .semantics { contentDescription = "edgar_company_${company.ticker}" },
        color = if (selected) Color(0xFF10291C) else Color(0xFF0C1017),
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, if (selected) AuthColors.Accent else AuthColors.Border),
        onClick = onClick,
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = company.companyName,
                color = AuthColors.PrimaryText,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = "${company.ticker} · CIK ${company.cik}",
                color = if (selected) AuthColors.Accent else AuthColors.MutedText,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Composable
fun SelectedCompanyHeader(company: EdgarCompany) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color(0xFF0C1017),
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, AuthColors.Border),
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = company.companyName,
                color = AuthColors.PrimaryText,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = "Ticker ${company.ticker} · CIK ${company.cik}",
                color = AuthColors.MutedText,
                style = MaterialTheme.typography.bodyMedium,
            )
            Text(
                text = "Datos reales SEC",
                color = AuthColors.Accent,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}
