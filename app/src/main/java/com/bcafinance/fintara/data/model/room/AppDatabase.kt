package com.bcafinance.fintara.data.model.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.bcafinance.fintara.data.model.dao.CustomerProfileDao  // Pastikan ini adalah model yang kamu gunakan
import androidx.room.TypeConverters

@Database(entities = [CustomerProfileEntity::class], version = 1, exportSchema = false)
@TypeConverters(BigDecimalTypeConverter::class) // Daftarkan TypeConverter di sini
abstract class AppDatabase : RoomDatabase() {
    abstract fun customerProfileDao(): CustomerProfileDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "fintara_db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}