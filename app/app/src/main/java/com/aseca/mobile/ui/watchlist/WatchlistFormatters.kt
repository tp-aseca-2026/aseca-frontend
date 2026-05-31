package com.aseca.mobile.ui.watchlist

import com.aseca.mobile.models.WatchlistMetric
import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

internal fun WatchlistMetric?.money(): String {
    return this?.value?.let { value ->
        "${moneyFormatter.format(value.toCompactAmount())}${value.compactSuffix()}"
    } ?: "No disponible"
}

internal fun WatchlistMetric?.number(): String {
    return this?.value?.let { value ->
        numberFormatter.format(value)
    } ?: "No disponible"
}

private fun Double.toCompactAmount(): Double {
    val absolute = kotlin.math.abs(this)
    return when {
        absolute >= 1_000_000_000 -> this / 1_000_000_000
        absolute >= 1_000_000 -> this / 1_000_000
        absolute >= 1_000 -> this / 1_000
        else -> this
    }
}

private fun Double.compactSuffix(): String {
    val absolute = kotlin.math.abs(this)
    return when {
        absolute >= 1_000_000_000 -> "B"
        absolute >= 1_000_000 -> "M"
        absolute >= 1_000 -> "K"
        else -> ""
    }
}

private val moneyFormatter = NumberFormat.getCurrencyInstance(Locale.US).apply {
    currency = Currency.getInstance("USD")
    maximumFractionDigits = 2
}

private val numberFormatter = NumberFormat.getNumberInstance(Locale.US).apply {
    maximumFractionDigits = 2
}
