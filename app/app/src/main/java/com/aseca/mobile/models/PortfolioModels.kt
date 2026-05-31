package com.aseca.mobile.models

data class PortfolioPosition(
    val stockId: Int,
    val ticker: String,
    val companyName: String?,
    val quantity: Double,
    val averageBuyPrice: Double,
    val costBasis: Double,
    val latestPrice: Double?,
    val currentValue: Double?,
    val unrealizedProfitLoss: Double?,
    val unrealizedProfitLossPercentage: Double?,
    val realizedProfitLoss: Double,
    val totalProfitLoss: Double?,
    val lastPriceUpdatedAt: String?,
)

data class PortfolioSummary(
    val totalCostBasis: Double,
    val currentValue: Double?,
    val unrealizedProfitLoss: Double?,
    val unrealizedProfitLossPercentage: Double?,
    val realizedProfitLoss: Double,
    val totalProfitLoss: Double?,
    val lastPriceUpdatedAt: String?,
)

data class PortfolioResponse(
    val positions: List<PortfolioPosition>,
    val summary: PortfolioSummary,
)

data class LatestPriceSnapshots(
    val lastUpdatedAt: String?,
    val prices: List<PriceSnapshot>,
)

data class PriceSnapshot(
    val ticker: String,
    val stockId: Int,
    val price: Double,
    val source: String,
    val fetchedAt: String?,
)
