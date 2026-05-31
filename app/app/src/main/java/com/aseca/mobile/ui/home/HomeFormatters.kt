package com.aseca.mobile.ui.home

import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

internal fun Double.money(): String {
    return moneyFormatter.format(this)
}

internal fun Double?.moneyOrEmpty(): String {
    return this?.money() ?: "Sin precio"
}

internal fun Double?.percentOrEmpty(): String {
    val value = this ?: return "Sin dato"
    val prefix = if (value >= 0) "+" else ""
    return "$prefix${"%.2f".format(Locale.US, value)}%"
}

private val moneyFormatter = NumberFormat.getCurrencyInstance(Locale.US).apply {
    currency = Currency.getInstance("USD")
    maximumFractionDigits = 2
    minimumFractionDigits = 2
}
