package com.aseca.mobile.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aseca.mobile.repository.AuthRepository
import kotlinx.coroutines.launch

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val loading: Boolean = false,
    val error: String = "",
    val accessToken: String = "",
    val success: Boolean = false,
)

class LoginViewModel(
    private val authRepository: AuthRepository,
) : ViewModel() {
    var uiState by mutableStateOf(LoginUiState())
        private set

    fun onEmailChange(email: String) {
        uiState = uiState.copy(email = email)
    }

    fun onPasswordChange(password: String) {
        uiState = uiState.copy(password = password)
    }

    fun login(onAuthenticated: (String) -> Unit) {
        val email = uiState.email.trim()
        val password = uiState.password

        if (email.isBlank() || password.isBlank()) {
            uiState = uiState.copy(error = "Email y contraseña son obligatorios.", success = false)
            return
        }

        viewModelScope.launch {
            uiState = uiState.copy(loading = true, error = "", success = false, accessToken = "")
            try {
                val response = authRepository.login(email = email, password = password)
                uiState = uiState.copy(
                    loading = false,
                    success = true,
                    accessToken = response.accessToken,
                )
                onAuthenticated(response.accessToken)
            } catch (exception: Exception) {
                uiState = uiState.copy(
                    loading = false,
                    success = false,
                    error = exception.message ?: "Email o contraseña incorrectos.",
                )
            }
        }
    }
}
