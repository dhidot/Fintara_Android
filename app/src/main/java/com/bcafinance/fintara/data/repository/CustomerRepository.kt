package com.bcafinance.fintara.data.repository

import com.bcafinance.fintara.config.network.api.CustomerApiService
import com.bcafinance.fintara.data.model.ApiResponse
import com.bcafinance.fintara.data.model.dto.auth.FirstTimeUpdateRequest
import com.bcafinance.fintara.data.model.dto.auth.UserWithCustomerResponse
import com.bcafinance.fintara.data.model.dao.CustomerProfileDao
import com.bcafinance.fintara.data.model.dto.auth.toEntity
import com.bcafinance.fintara.data.model.room.toDto
import android.util.Log
import okhttp3.MultipartBody

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

    suspend fun uploadKtp(filePart: MultipartBody.Part): Result<String> {
        return try {
            val response = customerApiService.uploadKtp(filePart)
            Log.d("UploadKtp", "Response isSuccessful: ${response.status}")
            Log.d("UploadKtp", "Raw Response Body: ${response.data}")
            if (response.status == 200) {
                val body = response.data
                if (body != null) {
                    Result.success(body)
                } else {
                    Result.failure(Exception("Upload gagal: ${response.message}"))
                }
            } else {
                Result.failure(Exception("Upload gagal: ${response.message}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun uploadSelfieKtp(filePart: MultipartBody.Part): Result<String> {
        return try {
            val response = customerApiService.uploadSelfieKtp(filePart)
            Log.d("UploadSelfieKtp", "Response isSuccessful: ${response.status}")
            Log.d("UploadSelfieKtp", "Raw Response Body: ${response.data}")
            if (response.status == 200) {
                val body = response.data
                if (body != null) {
                    Result.success(body)
                } else {
                    Result.failure(Exception("Upload gagal: ${response.message}"))
                }
            } else {
                Result.failure(Exception("Upload gagal: ${response.message}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

