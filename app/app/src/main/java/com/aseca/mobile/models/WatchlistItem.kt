package com.aseca.mobile.models

data class WatchlistItem(
    val id: Int,
    val userId: Int,
    val stockId: Int,
    val createdAt: String?,
    val stock: Stock,
)

data class WatchlistComparisonItem(
    val ticker: String,
    val companyName: String?,
    val revenue: WatchlistMetric?,
    val netIncome: WatchlistMetric?,
    val eps: WatchlistMetric?,
    val totalAssets: WatchlistMetric?,
    val totalLiabilities: WatchlistMetric?,
)

data class WatchlistMetric(
    val value: Double,
)
