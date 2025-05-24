package com.bcafinance.fintara.utils

import java.text.NumberFormat
import java.util.Locale

fun formatRupiah(amount: Number?): String {
    if (amount == null) return "Rp0"
    return NumberFormat.getCurrencyInstance(Locale("in", "ID"))
        .format(amount)
        .replace(",00", "")
}

fun formatRupiahInput(amount: String): String {
    if (amount.isBlank()) return ""
    val cleanString = amount.replace("[^\\d]".toRegex(), "")
    val parsed = cleanString.toDoubleOrNull() ?: return ""
    return NumberFormat.getCurrencyInstance(Locale("in", "ID"))
        .format(parsed)
        .replace(",00", "")
}
