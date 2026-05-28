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
    fun get(path: String, accessToken: String? = null): JSONObject {
        val url = URL("$baseUrl$path")
        val connection = (url.openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            setRequestProperty("Content-Type", "application/json")
            accessToken?.takeIf { it.isNotBlank() }?.let {
                setRequestProperty("Authorization", "Bearer $it")
            }
            connectTimeout = 10_000
            readTimeout = 10_000
        }

        return readResponse(connection)
    }

    fun getArray(path: String, accessToken: String? = null): JSONArray {
        val url = URL("$baseUrl$path")
        val connection = (url.openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            setRequestProperty("Content-Type", "application/json")
            accessToken?.takeIf { it.isNotBlank() }?.let {
                setRequestProperty("Authorization", "Bearer $it")
            }
            connectTimeout = 10_000
            readTimeout = 10_000
        }

        return readArrayResponse(connection)
    }

    fun post(path: String, payload: JSONObject, accessToken: String? = null): JSONObject {
        val url = URL("$baseUrl$path")
        val connection = (url.openConnection() as HttpURLConnection).apply {
            requestMethod = "POST"
            setRequestProperty("Content-Type", "application/json")
            accessToken?.takeIf { it.isNotBlank() }?.let {
                setRequestProperty("Authorization", "Bearer $it")
            }
            connectTimeout = 10_000
            readTimeout = 10_000
            doOutput = true
        }

        connection.outputStream.use { output ->
            output.write(payload.toString().toByteArray())
        }

        return readResponse(connection)
    }

    fun delete(path: String, accessToken: String? = null): JSONObject {
        val url = URL("$baseUrl$path")
        val connection = (url.openConnection() as HttpURLConnection).apply {
            requestMethod = "DELETE"
            setRequestProperty("Content-Type", "application/json")
            accessToken?.takeIf { it.isNotBlank() }?.let {
                setRequestProperty("Authorization", "Bearer $it")
            }
            connectTimeout = 10_000
            readTimeout = 10_000
        }

        return readResponse(connection)
    }

    private fun readResponse(connection: HttpURLConnection): JSONObject {
        val responseText = readResponseText(connection)
        return if (responseText.isBlank()) JSONObject() else JSONObject(responseText)
    }

    private fun readArrayResponse(connection: HttpURLConnection): JSONArray {
        val responseText = readResponseText(connection)
        return if (responseText.isBlank()) JSONArray() else JSONArray(responseText)
    }

    private fun readResponseText(connection: HttpURLConnection): String {
        return try {
            val statusCode = connection.responseCode
            val inputStream = if (statusCode in 200..299) {
                connection.inputStream
            } else {
                connection.errorStream
            }
            val responseText = inputStream?.bufferedReader()?.use(BufferedReader::readText).orEmpty()

            if (statusCode in 200..299) {
                responseText
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
