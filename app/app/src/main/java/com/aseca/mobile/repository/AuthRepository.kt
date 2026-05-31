package com.aseca.mobile.repository

import com.aseca.mobile.models.LoginRequest
import com.aseca.mobile.models.LoginResponse
import com.aseca.mobile.models.RegisterRequest
import com.aseca.mobile.network.ApiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

class AuthRepository(
    private val apiClient: ApiClient = ApiClient(),
) {
    suspend fun login(email: String, password: String): LoginResponse = withContext(Dispatchers.IO) {
        val request = LoginRequest(email = email, password = password)
        val response = apiClient.post(
            path = "/auth/login",
            payload = JSONObject()
                .put("email", request.email)
                .put("password", request.password),
        )
        LoginResponse(accessToken = response.getString("accessToken"))
    }

    suspend fun register(email: String, password: String) = withContext(Dispatchers.IO) {
        val request = RegisterRequest(email = email, password = password)
        apiClient.post(
            path = "/auth/register",
            payload = JSONObject()
                .put("email", request.email)
                .put("password", request.password),
        )
    }
}
