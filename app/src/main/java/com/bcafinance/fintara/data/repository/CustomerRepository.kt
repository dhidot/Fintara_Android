package com.bcafinance.fintara.data.repository

import android.content.Context
import com.bcafinance.fintara.config.network.CustomerApiService
import com.bcafinance.fintara.data.model.ApiResponse
import com.bcafinance.fintara.data.model.dto.FirstTimeUpdateRequest
import com.bcafinance.fintara.data.model.dto.UserWithCustomerResponse
import com.bcafinance.fintara.data.model.dao.CustomerProfileDao
import com.bcafinance.fintara.data.model.dto.toEntity
import com.bcafinance.fintara.data.model.room.toDto
import android.util.Log


class CustomerRepository(
    private val customerApiService: CustomerApiService,
    private val customerProfileDao: CustomerProfileDao // Tambahkan DAO sebagai dependency
) {
    suspend fun getProfile(userId: String): ApiResponse<UserWithCustomerResponse> {
        val localProfile = customerProfileDao.getProfile(userId) // Sesuaikan ID sesuai kebutuhan
        return if (localProfile != null) {
            Log.d("CustomerRepository", "Mengambil profil dari lokal Room DB")
            ApiResponse.success(localProfile.toDto()) // Convert dari Entity ke DTO
        } else {
            val response = customerApiService.getMyProfile()
            if (response.status == 200) {
                // Jika response dari API berhasil, simpan ke lokal
                Log.d("CustomerRepository", "Profil berhasil diambil dari backend. Menyimpan ke lokal Room DB")
                val entity = response.data!!.toEntity()
                Log.d("CustomerRepository", "Menyimpan profile ke Room: id=${entity.id}")
                customerProfileDao.insertProfile(entity)
            }
            response
        }
    }

    suspend fun updateFirstTimeProfile(request: FirstTimeUpdateRequest): ApiResponse<String> {
        return customerApiService.updateFirstTimeProfile(request)
    }
}

