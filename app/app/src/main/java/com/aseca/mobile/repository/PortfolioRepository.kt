package com.aseca.mobile.repository

import com.aseca.mobile.models.PortfolioPosition
import com.aseca.mobile.models.PortfolioResponse
import com.aseca.mobile.models.PortfolioSummary
import com.aseca.mobile.models.Stock
import com.aseca.mobile.models.Transaction
import com.aseca.mobile.network.ApiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

class PortfolioRepository(
    private val apiClient: ApiClient = ApiClient(),
) {
    suspend fun getPortfolio(accessToken: String): PortfolioResponse = withContext(Dispatchers.IO) {
        val response = apiClient.get(path = "/portfolio", accessToken = accessToken)
        response.toPortfolioResponse()
    }

    suspend fun getStocks(accessToken: String): List<Stock> = withContext(Dispatchers.IO) {
        val response = apiClient.getArray(path = "/stocks", accessToken = accessToken)
        response.toStocks()
    }

    suspend fun buy(accessToken: String, ticker: String, quantity: Int): Transaction =
        withContext(Dispatchers.IO) {
            val response = apiClient.post(
                path = "/transactions/buy",
                accessToken = accessToken,
                payload = JSONObject()
                    .put("ticker", ticker)
                    .put("quantity", quantity),
            )

            response.toTransaction()
        }

    suspend fun sell(accessToken: String, ticker: String, quantity: Int): Transaction =
        withContext(Dispatchers.IO) {
            val response = apiClient.post(
                path = "/transactions/sell",
                accessToken = accessToken,
                payload = JSONObject()
                    .put("ticker", ticker)
                    .put("quantity", quantity),
            )

            response.toTransaction()
        }
}

private fun JSONObject.toPortfolioResponse(): PortfolioResponse {
    val positionsJson = getJSONArray("positions")
    val positions = (0 until positionsJson.length()).map { index ->
        positionsJson.getJSONObject(index).toPortfolioPosition()
    }

    return PortfolioResponse(
        positions = positions,
        summary = getJSONObject("summary").toPortfolioSummary(),
    )
}

private fun JSONObject.toPortfolioPosition(): PortfolioPosition {
    return PortfolioPosition(
        stockId = getInt("stockId"),
        ticker = getString("ticker"),
        companyName = nullableString("companyName"),
        quantity = getDouble("quantity"),
        averageBuyPrice = getDouble("averageBuyPrice"),
        costBasis = getDouble("costBasis"),
        latestPrice = nullableDouble("latestPrice"),
        currentValue = nullableDouble("currentValue"),
        unrealizedProfitLoss = nullableDouble("unrealizedProfitLoss"),
        unrealizedProfitLossPercentage = nullableDouble("unrealizedProfitLossPercentage"),
        realizedProfitLoss = getDouble("realizedProfitLoss"),
        totalProfitLoss = nullableDouble("totalProfitLoss"),
        lastPriceUpdatedAt = nullableString("lastPriceUpdatedAt"),
    )
}

private fun JSONObject.toPortfolioSummary(): PortfolioSummary {
    return PortfolioSummary(
        totalCostBasis = getDouble("totalCostBasis"),
        currentValue = nullableDouble("currentValue"),
        unrealizedProfitLoss = nullableDouble("unrealizedProfitLoss"),
        unrealizedProfitLossPercentage = nullableDouble("unrealizedProfitLossPercentage"),
        realizedProfitLoss = getDouble("realizedProfitLoss"),
        totalProfitLoss = nullableDouble("totalProfitLoss"),
        lastPriceUpdatedAt = nullableString("lastPriceUpdatedAt"),
    )
}

private fun JSONArray.toStocks(): List<Stock> {
    return (0 until length()).map { index ->
        getJSONObject(index).toStock()
    }
}

private fun JSONObject.toStock(): Stock {
    return Stock(
        id = getInt("id"),
        ticker = getString("ticker"),
        companyName = nullableString("companyName"),
        cik = nullableString("cik"),
    )
}

private fun JSONObject.toTransaction(): Transaction {
    return Transaction(
        id = getInt("id"),
        userId = getInt("userId"),
        stockId = getInt("stockId"),
        type = getString("type"),
        quantity = getInt("quantity"),
        price = getDouble("price"),
        executedAt = nullableString("executedAt"),
    )
}

private fun JSONObject.nullableDouble(key: String): Double? {
    return if (isNull(key)) null else getDouble(key)
}

private fun JSONObject.nullableString(key: String): String? {
    return if (isNull(key)) null else optString(key)
}
