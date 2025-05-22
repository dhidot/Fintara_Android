package com.bcafinance.fintara.utils

import java.text.NumberFormat
import java.util.Locale

fun formatRupiah(amount: Number?): String =
    NumberFormat.getCurrencyInstance(Locale("in", "ID")).format(amount).replace(",00", "")

fun formatRupiahInput(amount: String): String {
    if (amount.isBlank()) return ""
    val cleanString = amount.replace("[Rp,.\\s]".toRegex(), "")
    val parsed = cleanString.toDoubleOrNull() ?: return ""
    val formatter = NumberFormat.getCurrencyInstance(Locale("in", "ID"))
    return formatter.format(parsed).replace("Rp", "Rp").replace(",00", "")
}
