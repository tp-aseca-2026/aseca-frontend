package com.aseca.mobile.ui.edgar

import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

internal fun Double?.moneyOrUnavailable(): String {
    return this?.let { moneyFormatter.format(it) } ?: "No disponible"
}

internal fun Double?.numberOrUnavailable(): String {
    return this?.let { numberFormatter.format(it) } ?: "No disponible"
}

internal fun Double.formatMetric(money: Boolean): String {
    return if (money) moneyFormatter.format(this) else numberFormatter.format(this)
}

private val moneyFormatter = NumberFormat.getCurrencyInstance(Locale.US).apply {
    currency = Currency.getInstance("USD")
    maximumFractionDigits = 2
}

private val numberFormatter = NumberFormat.getNumberInstance(Locale.US).apply {
    maximumFractionDigits = 2
}
