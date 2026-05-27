package com.aseca.mobile.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aseca.mobile.ui.asTokenPreview

@Composable
fun HomeScreen(
    accessToken: String,
    onGoToPortfolio: () -> Unit,
    onLogout: () -> Unit,
) {
    val accent = Color(0xFF00E676)
    val background = Color(0xFF080D14)
    val primaryText = Color(0xFFF3F6FB)
    val mutedText = Color(0xFFA1A9B7)

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = background,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 40.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            Text(
                text = "ASECA",
                color = primaryText,
                style = MaterialTheme.typography.headlineLarge,
                fontSize = 44.sp,
            )

            Text(
                text = "Sesión iniciada",
                color = accent,
                style = MaterialTheme.typography.titleMedium,
            )

            Text(
                text = "Token guardado: ${accessToken.asTokenPreview()}",
                color = mutedText,
                style = MaterialTheme.typography.bodyMedium,
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onGoToPortfolio,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = accent,
                    contentColor = Color(0xFF06100B),
                ),
                shape = RoundedCornerShape(18.dp),
            ) {
                Text("Portfolio")
            }

            Button(
                onClick = onLogout,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF0C1017),
                    contentColor = primaryText,
                ),
                shape = RoundedCornerShape(18.dp),
            ) {
                Text("Logout")
            }
        }
    }
}
