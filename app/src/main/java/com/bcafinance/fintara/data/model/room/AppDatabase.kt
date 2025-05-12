package com.bcafinance.fintara.data.model.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.bcafinance.fintara.data.model.dao.CustomerProfileDao  // Pastikan ini adalah model yang kamu gunakan
import androidx.room.TypeConverters

@Database(entities = [CustomerProfileEntity::class], version = 1, exportSchema = false)
@TypeConverters(BigDecimalTypeConverter::class) // Daftarkan TypeConverter di sini
abstract class AppDatabase : RoomDatabase() {
    abstract fun customerProfileDao(): CustomerProfileDao
}