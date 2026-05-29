package com.aseca.mobile.ui.home

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
import androidx.compose.ui.unit.sp
import com.aseca.mobile.models.PortfolioSummary
import com.aseca.mobile.ui.AuthColors

@Composable
fun HomeHeader(onLogout: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column {
            Text(
                text = "StockFolio",
                color = AuthColors.PrimaryText,
                style = MaterialTheme.typography.headlineLarge,
                fontSize = 34.sp,
                fontWeight = FontWeight.Normal,
            )
            Text(
                text = "Dashboard",
                color = AuthColors.Accent,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )
        }

        Button(
            onClick = onLogout,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF0C1017),
                contentColor = AuthColors.PrimaryText,
            ),
            shape = RoundedCornerShape(14.dp),
        ) {
            Text("Cerrar sesión")
        }
    }
}

@Composable
fun DashboardHero(summary: PortfolioSummary?) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color(0xFF0C1017),
        shape = RoundedCornerShape(22.dp),
        border = BorderStroke(1.dp, AuthColors.Border),
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = "Estado actual de tu portfolio",
                color = AuthColors.PrimaryText,
                style = MaterialTheme.typography.headlineSmall,
                fontSize = 28.sp,
                fontWeight = FontWeight.Normal,
            )
            Text(
                text = "Valor total",
                color = AuthColors.MutedText,
                style = MaterialTheme.typography.bodyMedium,
            )
            Text(
                text = summary?.currentValue.moneyOrEmpty(),
                color = AuthColors.PrimaryText,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = "Última actualización: ${summary?.lastPriceUpdatedAt ?: "sin registro"}",
                color = AuthColors.MutedText,
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}
