package com.aseca.mobile.ui.transactions

import java.text.NumberFormat
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.Currency
import java.util.Locale

internal fun Double.money(): String {
    return moneyFormatter.format(this)
}

internal fun String?.formatDate(): String {
    if (isNullOrBlank()) return "Sin fecha"

    return try {
        val parsed = OffsetDateTime.parse(this)
        parsed.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
    } catch (_: Exception) {
        this
    }
}

private val moneyFormatter = NumberFormat.getCurrencyInstance(Locale.US).apply {
    currency = Currency.getInstance("USD")
    maximumFractionDigits = 2
    minimumFractionDigits = 2
}
