package com.aseca.mobile.repository

import com.aseca.mobile.models.EdgarCompany
import com.aseca.mobile.models.EdgarFiling
import com.aseca.mobile.models.EdgarHistoricalMetrics
import com.aseca.mobile.models.EdgarMetricPoint
import com.aseca.mobile.models.EdgarMetrics
import com.aseca.mobile.network.ApiClient
import java.net.URLEncoder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

class EdgarRepository(
    private val apiClient: ApiClient = ApiClient(),
) {
    suspend fun searchCompanies(query: String): List<EdgarCompany> =
        withContext(Dispatchers.IO) {
            val encodedQuery = URLEncoder.encode(query.trim(), "UTF-8")
            val response = apiClient.getArray("/edgar/companies/search?q=$encodedQuery")
            response.toCompanies()
        }

    suspend fun getCompanyMetrics(ticker: String): EdgarMetrics =
        withContext(Dispatchers.IO) {
            val response = apiClient.get("/edgar/companies/${ticker.trim().uppercase()}/metrics")
            response.toMetrics()
        }

    suspend fun getCompanyFilings(ticker: String): List<EdgarFiling> =
        withContext(Dispatchers.IO) {
            val response = apiClient.getArray("/edgar/companies/${ticker.trim().uppercase()}/filings")
            response.toFilings()
        }

    suspend fun getHistoricalMetrics(ticker: String): EdgarHistoricalMetrics =
        withContext(Dispatchers.IO) {
            val response = apiClient.get("/edgar/companies/${ticker.trim().uppercase()}/historical-metrics")
            response.toHistoricalMetrics()
        }
}

private fun JSONArray.toCompanies(): List<EdgarCompany> {
    return (0 until length()).map { index ->
        val item = getJSONObject(index)

        EdgarCompany(
            companyName = item.optString("companyName"),
            ticker = item.optString("ticker"),
            cik = item.optString("cik"),
        )
    }
}

private fun JSONObject.toMetrics(): EdgarMetrics {
    return EdgarMetrics(
        revenue = nullableMetric("revenue"),
        netIncome = nullableMetric("netIncome"),
        eps = nullableMetric("eps"),
        totalAssets = nullableMetric("totalAssets"),
        totalLiabilities = nullableMetric("totalLiabilities"),
    )
}

private fun JSONObject.toHistoricalMetrics(): EdgarHistoricalMetrics {
    return EdgarHistoricalMetrics(
        revenue = optJSONArray("revenue").toMetricPoints(),
        netIncome = optJSONArray("netIncome").toMetricPoints(),
        eps = optJSONArray("eps").toMetricPoints(),
        totalAssets = optJSONArray("totalAssets").toMetricPoints(),
        totalLiabilities = optJSONArray("totalLiabilities").toMetricPoints(),
    )
}

private fun JSONArray?.toMetricPoints(): List<EdgarMetricPoint> {
    if (this == null) return emptyList()

    return (0 until length()).map { index ->
        getJSONObject(index).toMetricPoint()
    }
}

private fun JSONArray.toFilings(): List<EdgarFiling> {
    return (0 until length()).map { index ->
        val item = getJSONObject(index)

        EdgarFiling(
            form = item.optString("form"),
            filingDate = item.optString("filingDate"),
            accessionNumber = item.optString("accessionNumber"),
            reportDate = item.nullableString("reportDate"),
            primaryDocument = item.nullableString("primaryDocument"),
            link = item.optString("link"),
        )
    }
}

private fun JSONObject.nullableMetric(key: String): EdgarMetricPoint? {
    return if (isNull(key)) null else getJSONObject(key).toMetricPoint()
}

private fun JSONObject.toMetricPoint(): EdgarMetricPoint {
    return EdgarMetricPoint(
        fy = nullableInt("fy"),
        fp = nullableString("fp"),
        end = nullableString("end"),
        filed = nullableString("filed"),
        form = nullableString("form"),
        value = nullableDouble("val"),
    )
}

private fun JSONObject.nullableString(key: String): String? {
    return if (isNull(key)) null else optString(key)
}

private fun JSONObject.nullableInt(key: String): Int? {
    return if (isNull(key)) null else optInt(key)
}

private fun JSONObject.nullableDouble(key: String): Double? {
    return if (isNull(key)) null else optDouble(key)
}
