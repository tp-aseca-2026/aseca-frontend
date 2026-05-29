package com.aseca.mobile.ui.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.aseca.mobile.models.LatestPriceSnapshots
import com.aseca.mobile.models.PriceSnapshot
import com.aseca.mobile.ui.AuthColors

@Composable
fun UpdatePricesButton(
    loading: Boolean,
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick,
        enabled = !loading,
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .semantics { contentDescription = "prices_update_button" },
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF0C1017),
            contentColor = AuthColors.PrimaryText,
            disabledContainerColor = Color(0xFF151820),
            disabledContentColor = AuthColors.MutedText,
        ),
        shape = RoundedCornerShape(18.dp),
    ) {
        Text(if (loading) "Actualizando precios..." else "Actualizar precios")
    }
}

@Composable
fun LatestPricesCard(latestPriceSnapshots: LatestPriceSnapshots?) {
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
