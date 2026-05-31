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
import com.aseca.mobile.viewmodel.RegisterViewModel
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType

@Composable
fun RegisterScreen(
    viewModel: RegisterViewModel,
    onGoToLogin: () -> Unit,
    onAuthenticated: (String) -> Unit,
) {
    val state = viewModel.uiState

    AuthScaffold(subtitle = "Registro seguro") {
        Spacer(modifier = Modifier.height(12.dp))

        AuthTextField(
            value = state.email,
            onValueChange = viewModel::onEmailChange,
            label = "Email",
            accessibilityId = "register_email",
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next,
            ),
        )

        AuthTextField(
            value = state.password,
            onValueChange = viewModel::onPasswordChange,
            label = "Password",
            accessibilityId = "register_password",
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done,
            ),
        )

        AuthStatusMessage(
            error = state.error,
            success = state.success,
            successText = "Registro exitoso. Token guardado.",
            accessToken = state.accessToken,
        )

        Spacer(modifier = Modifier.height(4.dp))

        AuthSubmitButton(
            text = "Register",
            loading = state.loading,
            onClick = { viewModel.register(onAuthenticated) },
            accessibilityId = "register_submit",
        )

        AuthNavigationButton(
            text = "Back to Login",
            onClick = onGoToLogin,
            accessibilityId = "back_to_login",
        )
    }
}
