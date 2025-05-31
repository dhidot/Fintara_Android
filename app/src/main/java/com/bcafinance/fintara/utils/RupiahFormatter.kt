package com.bcafinance.fintara.utils

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
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
    val cleanString = amount.replace("[^\\d,\\.]".toRegex(), "") // Jaga titik/koma
        .replace(",", ".") // Ganti koma ke titik supaya bisa di-parse

    val parsed = cleanString.toBigDecimalOrNull() ?: return ""

    val symbols = DecimalFormatSymbols().apply {
        groupingSeparator = '.'
        decimalSeparator = ','
        currencySymbol = "Rp"
    }

    val formatter = DecimalFormat("Rp#,##0", symbols)
    return formatter.format(parsed)
}


