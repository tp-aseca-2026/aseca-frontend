package com.aseca.mobile.ui.transactions

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.aseca.mobile.ui.AuthColors

@Composable
fun TransactionsLoading() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp),
        horizontalArrangement = Arrangement.Center,
    ) {
        CircularProgressIndicator(
            color = AuthColors.Accent,
            strokeWidth = 2.dp,
        )
    }
}

@Composable
fun TransactionsMessage(
    text: String,
    error: Boolean = false,
) {
    Text(
        text = text,
        color = if (error) AuthColors.Error else AuthColors.MutedText,
        style = MaterialTheme.typography.bodyMedium,
        modifier = Modifier.padding(vertical = 12.dp),
    )
}
