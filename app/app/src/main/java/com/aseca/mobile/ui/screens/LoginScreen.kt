package com.aseca.mobile.ui.screens

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.aseca.mobile.ui.AuthNavigationButton
import com.aseca.mobile.ui.AuthScaffold
import com.aseca.mobile.ui.AuthStatusMessage
import com.aseca.mobile.ui.AuthSubmitButton
import com.aseca.mobile.ui.AuthTextField
import com.aseca.mobile.viewmodel.LoginViewModel

@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    onGoToRegister: () -> Unit,
    onAuthenticated: (String) -> Unit,
) {
    val state = viewModel.uiState

    AuthScaffold(subtitle = "Acceso seguro") {
        Spacer(modifier = Modifier.height(12.dp))

        AuthTextField(
            value = state.email,
            onValueChange = viewModel::onEmailChange,
            label = "Email",
        )

        AuthTextField(
            value = state.password,
            onValueChange = viewModel::onPasswordChange,
            label = "Password",
            visualTransformation = PasswordVisualTransformation(),
        )

        AuthStatusMessage(
            error = state.error,
            success = state.success,
            successText = "Login exitoso. Token guardado.",
            accessToken = state.accessToken,
        )

        Spacer(modifier = Modifier.height(4.dp))

        AuthSubmitButton(
            text = "Login",
            loading = state.loading,
            onClick = { viewModel.login(onAuthenticated) },
        )

        AuthNavigationButton(
            text = "Register",
            onClick = onGoToRegister,
        )
    }
}
