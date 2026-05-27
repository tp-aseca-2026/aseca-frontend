package com.aseca.mobile.auth

import android.content.Context

class TokenStore(context: Context) {
    private val preferences = context.getSharedPreferences("aseca_auth", Context.MODE_PRIVATE)

    fun getAccessToken(): String {
        return preferences.getString(ACCESS_TOKEN_KEY, "").orEmpty()
    }

    fun saveAccessToken(token: String) {
        preferences.edit().putString(ACCESS_TOKEN_KEY, token).apply()
    }

    fun clear() {
        preferences.edit().remove(ACCESS_TOKEN_KEY).apply()
    }

    private companion object {
        const val ACCESS_TOKEN_KEY = "access_token"
    }
}
