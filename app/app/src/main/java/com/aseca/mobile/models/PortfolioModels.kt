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
