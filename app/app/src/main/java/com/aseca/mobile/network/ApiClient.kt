package com.aseca.mobile.network

import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class ApiException(message: String) : Exception(message)

class ApiClient(
    private val baseUrl: String = "http://10.0.2.2:3000",
) {
    fun post(path: String, payload: JSONObject): JSONObject {
        val url = URL("$baseUrl$path")
        val connection = (url.openConnection() as HttpURLConnection).apply {
            requestMethod = "POST"
            setRequestProperty("Content-Type", "application/json")
            connectTimeout = 10_000
            readTimeout = 10_000
            doOutput = true
        }

        return try {
            connection.outputStream.use { output ->
                output.write(payload.toString().toByteArray())
            }

            val statusCode = connection.responseCode
            val inputStream = if (statusCode in 200..299) {
                connection.inputStream
            } else {
                connection.errorStream
            }
            val responseText = inputStream?.bufferedReader()?.use(BufferedReader::readText).orEmpty()

            if (statusCode in 200..299) {
                if (responseText.isBlank()) JSONObject() else JSONObject(responseText)
            } else {
                throw ApiException(extractMessage(responseText))
            }
        } finally {
            connection.disconnect()
        }
    }

    private fun extractMessage(rawResponse: String): String {
        if (rawResponse.isBlank()) return "Error de red."

        return try {
            val json = JSONObject(rawResponse)
            when (val message = json.opt("message")) {
                is String -> message
                is JSONArray -> (0 until message.length()).joinToString(" - ") {
                    message.optString(it)
                }
                else -> "Error en la solicitud."
            }
        } catch (_: Exception) {
            "Error en la solicitud."
        }
    }
}
