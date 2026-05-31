package com.aseca.mobile.models

data class EdgarCompany(
    val companyName: String,
    val ticker: String,
    val cik: String,
)

data class EdgarMetricPoint(
    val fy: Int?,
    val fp: String?,
    val end: String?,
    val filed: String?,
    val form: String?,
    val value: Double?,
)

data class EdgarMetrics(
    val revenue: EdgarMetricPoint?,
    val netIncome: EdgarMetricPoint?,
    val eps: EdgarMetricPoint?,
    val totalAssets: EdgarMetricPoint?,
    val totalLiabilities: EdgarMetricPoint?,
)

data class EdgarHistoricalMetrics(
    val revenue: List<EdgarMetricPoint>,
    val netIncome: List<EdgarMetricPoint>,
    val eps: List<EdgarMetricPoint>,
    val totalAssets: List<EdgarMetricPoint>,
    val totalLiabilities: List<EdgarMetricPoint>,
)

data class EdgarFiling(
    val form: String,
    val filingDate: String,
    val accessionNumber: String,
    val reportDate: String?,
    val primaryDocument: String?,
    val link: String,
)
