package com.aseca.mobile.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

object AuthColors {
    val Accent = Color(0xFF00E676)
    val Background = Color(0xFF080D14)
    val Border = Color(0xFF243044)
    val Error = Color(0xFFFF6B86)
    val InputBackground = Color(0xFF060A0F)
    val PrimaryText = Color(0xFFF3F6FB)
    val ButtonText = Color(0xFF06100B)
    val MutedText = Color(0xFFA1A9B7)
}

@Composable
fun AuthScaffold(
    subtitle: String,
    content: @Composable () -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = AuthColors.Background,
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
                color = AuthColors.PrimaryText,
                style = MaterialTheme.typography.headlineLarge,
                fontSize = 44.sp,
            )

            Text(
                text = subtitle,
                color = AuthColors.MutedText,
                style = MaterialTheme.typography.titleMedium,
            )

            content()
        }
    }
}

@Composable
fun AuthTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    visualTransformation: VisualTransformation = VisualTransformation.None,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = modifier.fillMaxWidth(),
        singleLine = true,
        visualTransformation = visualTransformation,
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = AuthColors.PrimaryText,
            unfocusedTextColor = AuthColors.PrimaryText,
            focusedContainerColor = AuthColors.InputBackground,
            unfocusedContainerColor = AuthColors.InputBackground,
            focusedBorderColor = AuthColors.Accent,
            unfocusedBorderColor = AuthColors.Border,
            focusedLabelColor = AuthColors.Accent,
            unfocusedLabelColor = AuthColors.MutedText,
            cursorColor = AuthColors.Accent,
        ),
        shape = RoundedCornerShape(18.dp),
    )
}

@Composable
fun AuthStatusMessage(
    error: String,
    success: Boolean,
    successText: String,
    accessToken: String,
) {
    if (error.isNotBlank()) {
        Text(
            text = error,
            color = AuthColors.Error,
            style = MaterialTheme.typography.bodyMedium,
        )
    }

    if (success) {
        Text(
            text = successText,
            color = AuthColors.Accent,
            style = MaterialTheme.typography.bodyMedium,
        )
        Text(
            text = "Token: ${accessToken.asTokenPreview()}",
            color = AuthColors.MutedText,
            style = MaterialTheme.typography.bodySmall,
        )
    }
}

@Composable
fun AuthSubmitButton(
    text: String,
    loading: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Button(
        onClick = onClick,
        enabled = !loading,
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = AuthColors.Accent,
            contentColor = AuthColors.ButtonText,
            disabledContainerColor = AuthColors.Accent.copy(alpha = 0.55f),
            disabledContentColor = AuthColors.ButtonText.copy(alpha = 0.75f),
        ),
        shape = RoundedCornerShape(18.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
    ) {
        Box {
            if (loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(22.dp),
                    color = AuthColors.ButtonText,
                    strokeWidth = 2.dp,
                )
            } else {
                Text(text)
            }
        }
    }
}

@Composable
fun AuthNavigationButton(
    text: String,
    onClick: () -> Unit,
) {
    TextButton(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 6.dp),
        colors = ButtonDefaults.textButtonColors(contentColor = AuthColors.Accent),
    ) {
        Text(text)
    }
}

fun String.asTokenPreview(): String {
    if (isBlank()) return ""
    if (length <= 16) return "recibido"
    return "${take(8)}...${takeLast(6)}"
}
