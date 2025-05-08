package com.bcafinance.fintara.utils

import java.text.NumberFormat
import java.util.Locale

fun Int.toLocaleFormat(): String {
    return NumberFormat.getNumberInstance(Locale("in", "ID")).format(this)
}