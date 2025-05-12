package com.bcafinance.fintara.data.model.room

import androidx.room.TypeConverter
import java.math.BigDecimal

class BigDecimalTypeConverter {

    @TypeConverter
    fun fromBigDecimal(value: BigDecimal?): String? {
        return value?.toString()
    }

    @TypeConverter
    fun toBigDecimal(value: String?): BigDecimal? {
        return value?.let { BigDecimal(it) }
    }
}
