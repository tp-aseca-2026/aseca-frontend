package com.aseca.mobile.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.aseca.mobile.ui.AuthColors

@Composable
fun SectionTitle(text: String) {
    Text(
        text = text,
        color = AuthColors.PrimaryText,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
    )
}

@Composable
fun HomeLoading() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 28.dp),
        horizontalArrangement = Arrangement.Center,
    ) {
        CircularProgressIndicator(color = AuthColors.Accent)
    }
}

@Composable
fun HomeMessage(
    text: String,
    error: Boolean = false,
) {
    Text(
        text = text,
        color = if (error) AuthColors.Error else AuthColors.MutedText,
        style = MaterialTheme.typography.bodyMedium,
    )
}

internal fun profitColor(value: Double?): Color {
    return if ((value ?: 0.0) >= 0) AuthColors.Accent else AuthColors.Error
}
