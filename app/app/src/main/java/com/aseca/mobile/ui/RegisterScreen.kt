package com.aseca.mobile.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aseca.mobile.viewmodel.RegisterViewModel

@Composable
fun RegisterScreen(
    viewModel: RegisterViewModel,
    onGoToLogin: () -> Unit,
    onAuthenticated: (String) -> Unit,
) {
    val state = viewModel.uiState
    val accent = Color(0xFF00E676)
    val background = Color(0xFF080D14)
    val inputBackground = Color(0xFF060A0F)
    val border = Color(0xFF243044)
    val primaryText = Color(0xFFF3F6FB)
    val mutedText = Color(0xFFA1A9B7)

    Surface(
        modifier = Modifier
            .fillMaxSize(),
        color = background,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
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
                text = "Registro seguro",
                color = mutedText,
                style = MaterialTheme.typography.titleMedium,
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = state.email,
                onValueChange = viewModel::onEmailChange,
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = primaryText,
                    unfocusedTextColor = primaryText,
                    focusedContainerColor = inputBackground,
                    unfocusedContainerColor = inputBackground,
                    focusedBorderColor = accent,
                    unfocusedBorderColor = border,
                    focusedLabelColor = accent,
                    unfocusedLabelColor = mutedText,
                    cursorColor = accent,
                ),
                shape = RoundedCornerShape(18.dp),
            )

            OutlinedTextField(
                value = state.password,
                onValueChange = viewModel::onPasswordChange,
                label = { Text("Password") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = primaryText,
                    unfocusedTextColor = primaryText,
                    focusedContainerColor = inputBackground,
                    unfocusedContainerColor = inputBackground,
                    focusedBorderColor = accent,
                    unfocusedBorderColor = border,
                    focusedLabelColor = accent,
                    unfocusedLabelColor = mutedText,
                    cursorColor = accent,
                ),
                shape = RoundedCornerShape(18.dp),
            )

            if (state.error.isNotBlank()) {
                Text(
                    text = state.error,
                    color = Color(0xFFFF6B86),
                    style = MaterialTheme.typography.bodyMedium,
                )
            }

            if (state.success) {
                Text(
                    text = "Registro exitoso. Token guardado.",
                    color = accent,
                    style = MaterialTheme.typography.bodyMedium,
                )
                Text(
                    text = "Token: ${state.accessToken.asTokenPreview()}",
                    color = mutedText,
                    style = MaterialTheme.typography.bodySmall,
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Button(
                onClick = { viewModel.register(onAuthenticated) },
                enabled = !state.loading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = accent,
                    contentColor = Color(0xFF06100B),
                    disabledContainerColor = accent.copy(alpha = 0.55f),
                    disabledContentColor = Color(0xFF06100B).copy(alpha = 0.75f),
                ),
                shape = RoundedCornerShape(18.dp),
            ) {
                Box {
                    if (state.loading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(22.dp),
                            color = Color(0xFF06100B),
                            strokeWidth = 2.dp,
                        )
                    } else {
                        Text("Register")
                    }
                }
            }

            TextButton(
                onClick = onGoToLogin,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp),
                colors = ButtonDefaults.textButtonColors(contentColor = accent),
            ) {
                Text("Back to Login")
            }
        }
    }
}

private fun String.asTokenPreview(): String {
    if (isBlank()) return ""
    if (length <= 16) return "recibido"
    return "${take(8)}...${takeLast(6)}"
}
