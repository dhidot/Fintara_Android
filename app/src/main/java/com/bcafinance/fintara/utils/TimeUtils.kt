package com.bcafinance.fintara.utils

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
fun parseBackendDateTimeToEpochMillis(dateTimeStr: String): Long {
    val localDateTime = LocalDateTime.parse(dateTimeStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
    return localDateTime.toInstant(ZoneOffset.UTC).toEpochMilli()
}
