package com.aseca.mobile.repository

import com.aseca.mobile.models.LatestPriceSnapshots
import com.aseca.mobile.models.PortfolioPosition
import com.aseca.mobile.models.PortfolioResponse
import com.aseca.mobile.models.PortfolioSummary
import com.aseca.mobile.models.PriceSnapshot
import com.aseca.mobile.models.Stock
import com.aseca.mobile.models.Transaction
import com.aseca.mobile.models.WatchlistComparisonItem
import com.aseca.mobile.models.WatchlistItem
import com.aseca.mobile.models.WatchlistMetric
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

    suspend fun getTransactions(accessToken: String): List<Transaction> =
        withContext(Dispatchers.IO) {
            val response = apiClient.getArray(path = "/transactions", accessToken = accessToken)
            response.toTransactions()
        }

    suspend fun getWatchlist(accessToken: String): List<WatchlistItem> =
        withContext(Dispatchers.IO) {
            val response = apiClient.getArray(path = "/watchlist", accessToken = accessToken)
            response.toWatchlist()
        }

    suspend fun getWatchlistComparison(accessToken: String): List<WatchlistComparisonItem> =
        withContext(Dispatchers.IO) {
            val response = apiClient.getArray(
                path = "/watchlist/comparison",
                accessToken = accessToken,
            )
            response.toWatchlistComparison()
        }

    suspend fun addToWatchlist(accessToken: String, ticker: String): WatchlistItem =
        withContext(Dispatchers.IO) {
            val response = apiClient.post(
                path = "/watchlist",
                accessToken = accessToken,
                payload = JSONObject().put("ticker", ticker),
            )

            response.toWatchlistItem()
        }

    suspend fun removeFromWatchlist(accessToken: String, ticker: String): Unit =
        withContext(Dispatchers.IO) {
            apiClient.delete(
                path = "/watchlist/${ticker.trim().uppercase()}",
                accessToken = accessToken,
            )
        }

    suspend fun updatePriceSnapshots(accessToken: String, tickers: List<String>): Unit =
        withContext(Dispatchers.IO) {
            apiClient.post(
                path = "/price-snapshots/update",
                accessToken = accessToken,
                payload = JSONObject().put("tickers", JSONArray(tickers)),
            )
        }

    suspend fun getLatestPriceSnapshots(accessToken: String): LatestPriceSnapshots =
        withContext(Dispatchers.IO) {
            val response = apiClient.get(
                path = "/price-snapshots/latest",
                accessToken = accessToken,
            )
            response.toLatestPriceSnapshots()
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

private fun JSONObject.toLatestPriceSnapshots(): LatestPriceSnapshots {
    val pricesJson = getJSONArray("prices")

    return LatestPriceSnapshots(
        lastUpdatedAt = nullableString("lastUpdatedAt"),
        prices = (0 until pricesJson.length()).map { index ->
            pricesJson.getJSONObject(index).toPriceSnapshot()
        },
    )
}

private fun JSONObject.toPriceSnapshot(): PriceSnapshot {
    return PriceSnapshot(
        ticker = getString("ticker"),
        stockId = getInt("stockId"),
        price = getDouble("price"),
        source = getString("source"),
        fetchedAt = nullableString("fetchedAt"),
    )
}

private fun JSONArray.toStocks(): List<Stock> {
    return (0 until length()).map { index ->
        getJSONObject(index).toStock()
    }
}

private fun JSONArray.toTransactions(): List<Transaction> {
    return (0 until length()).map { index ->
        getJSONObject(index).toTransaction()
    }
}

private fun JSONArray.toWatchlist(): List<WatchlistItem> {
    return (0 until length()).map { index ->
        getJSONObject(index).toWatchlistItem()
    }
}

private fun JSONArray.toWatchlistComparison(): List<WatchlistComparisonItem> {
    return (0 until length()).map { index ->
        getJSONObject(index).toWatchlistComparisonItem()
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

private fun JSONObject.toWatchlistItem(): WatchlistItem {
    val stockJson = getJSONObject("stock")

    return WatchlistItem(
        id = getInt("id"),
        userId = getInt("userId"),
        stockId = getInt("stockId"),
        createdAt = nullableString("createdAt"),
        stock = stockJson.toStock(),
    )
}

private fun JSONObject.toWatchlistComparisonItem(): WatchlistComparisonItem {
    return WatchlistComparisonItem(
        ticker = getString("ticker"),
        companyName = nullableString("companyName"),
        revenue = nullableMetric("revenue"),
        netIncome = nullableMetric("netIncome"),
        eps = nullableMetric("eps"),
        totalAssets = nullableMetric("totalAssets"),
        totalLiabilities = nullableMetric("totalLiabilities"),
    )
}

private fun JSONObject.nullableMetric(key: String): WatchlistMetric? {
    return if (isNull(key)) null else WatchlistMetric(value = getJSONObject(key).getDouble("val"))
}

private fun JSONObject.nullableDouble(key: String): Double? {
    return if (isNull(key)) null else getDouble(key)
}

private fun JSONObject.nullableString(key: String): String? {
    return if (isNull(key)) null else optString(key)
}
