package com.bcafinance.fintara.data.model.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.bcafinance.fintara.data.model.room.CustomerProfileEntity

@Dao
interface CustomerProfileDao {
    @Query("SELECT * FROM customer_profile WHERE id = :id LIMIT 1")
    suspend fun getProfile(id: String): CustomerProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfile(profile: CustomerProfileEntity)
}