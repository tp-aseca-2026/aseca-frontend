package com.aseca.mobile.ui.watchlist

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.aseca.mobile.models.WatchlistItem
import com.aseca.mobile.ui.AuthColors

@Composable
fun WatchlistCard(
    item: WatchlistItem,
    actionLoading: Boolean,
    onRemove: () -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color(0xFF0C1017),
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, AuthColors.Border),
    ) {
        Row(
            modifier = Modifier.padding(18.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.stock.ticker,
                    color = AuthColors.PrimaryText,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = item.stock.companyName ?: "Sin nombre registrado",
                    color = AuthColors.MutedText,
                    style = MaterialTheme.typography.bodySmall,
                )
            }

            Button(
                onClick = onRemove,
                enabled = !actionLoading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF23101A),
                    contentColor = AuthColors.Error,
                    disabledContainerColor = Color(0xFF151820),
                    disabledContentColor = AuthColors.MutedText,
                ),
                shape = RoundedCornerShape(12.dp),
            ) {
                Text("Eliminar")
            }
        }
    }
}
