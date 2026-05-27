package com.aseca.mobile.models

data class Transaction(
    val id: Int,
    val userId: Int,
    val stockId: Int,
    val type: String,
    val quantity: Int,
    val price: Double,
    val executedAt: String?,
)
