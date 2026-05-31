package com.aseca.mobile.ui.portfolio

import androidx.compose.ui.graphics.Color
import com.aseca.mobile.ui.AuthColors
import java.text.NumberFormat
import java.util.Locale

internal fun Double.money(): String {
    return "USD ${moneyFormatter.format(this)}"
}

internal fun Double?.moneyOrEmpty(fallback: String = "Sin datos"): String {
    return this?.money() ?: fallback
}

internal fun Double?.percentOrEmpty(): String {
    val value = this ?: return "Sin datos"
    val sign = if (value >= 0) "+" else ""
    return "$sign${"%.2f".format(Locale.US, value)}%"
}

internal fun Double?.profitColor(): Color {
    val value = this ?: return AuthColors.MutedText
    return if (value >= 0) AuthColors.Accent else AuthColors.Error
}

internal fun Double.trimmed(): String {
    return if (this % 1.0 == 0.0) {
        toInt().toString()
    } else {
        "%.4f".format(Locale.US, this).trimEnd('0').trimEnd('.')
    }
}

private val moneyFormatter = NumberFormat.getNumberInstance(Locale.US).apply {
    minimumFractionDigits = 2
    maximumFractionDigits = 2
}
