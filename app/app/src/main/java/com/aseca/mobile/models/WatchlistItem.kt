package com.aseca.mobile.models

data class WatchlistItem(
    val id: Int,
    val userId: Int,
    val stockId: Int,
    val createdAt: String?,
    val stock: Stock,
)
