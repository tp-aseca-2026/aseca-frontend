package com.aseca.mobile.models

data class Stock(
    val id: Int,
    val ticker: String,
    val companyName: String?,
    val cik: String?,
)
