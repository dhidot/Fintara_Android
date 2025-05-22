package com.bcafinance.fintara.data.repository

import android.os.Build
import com.bcafinance.fintara.config.network.api.CustomerApiService
import com.bcafinance.fintara.data.model.ApiResponse
import com.bcafinance.fintara.data.model.dto.customer.FirstTimeUpdateRequest
import com.bcafinance.fintara.data.model.dto.customer.UserWithCustomerResponse
import com.bcafinance.fintara.data.model.dao.CustomerProfileDao
import com.bcafinance.fintara.data.model.dto.customer.toEntity
import com.bcafinance.fintara.data.model.room.toDto
import android.util.Log
import androidx.annotation.RequiresApi
import com.bcafinance.fintara.data.model.dto.customer.CustomerUpdateProfileRequestDTO
import com.bcafinance.fintara.utils.parseBackendDateTimeToEpochMillis
import okhttp3.MultipartBody

class CustomerRepository(
    private val customerApiService: CustomerApiService,
    private val customerProfileDao: CustomerProfileDao // Tambahkan DAO sebagai dependency
) {
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getProfile(userId: String): ApiResponse<UserWithCustomerResponse> {
        val localProfile = customerProfileDao.getProfile(userId)
        val localUpdatedAt = localProfile?.updatedAt ?: 0L

        return try {
            val apiResponse = customerApiService.getMyProfile()

            if (apiResponse.status == 200 && apiResponse.data != null) {
                val remoteProfile = apiResponse.data
                val remoteUpdatedAt = remoteProfile.customerDetails?.updatedAt
                    ?.let { parseBackendDateTimeToEpochMillis(it) } ?: 0L

                if (remoteUpdatedAt > localUpdatedAt) {
                    val entity = remoteProfile.toEntity()
                    customerProfileDao.insertProfile(entity)
                    Log.d("CustomerRepository", "Data backend lebih baru. Update lokal dan return data backend.")
                    ApiResponse.success(remoteProfile)
                } else {
                    Log.d("CustomerRepository", "Data lokal terbaru. Return data lokal.")
                    ApiResponse.success(localProfile!!.toDto())
                }
            } else {
                // API error tapi tidak exception (misal status 400)
                if (localProfile != null) {
                    Log.d("CustomerRepository", "API error (bukan exception). Return data lokal.")
                    ApiResponse.success(localProfile.toDto())
                } else {
                    apiResponse
                }
            }
        } catch (e: Exception) {
            Log.e("CustomerRepository", "Exception saat call API: ${e.message}")
            if (localProfile != null) {
                Log.d("CustomerRepository", "API gagal (exception). Return data lokal.")
                ApiResponse.success(localProfile.toDto())
            } else {
                ApiResponse.error("Gagal mengambil data profil dan data lokal tidak tersedia.")
            }
        }
    }

    suspend fun updateFirstTimeProfile(request: FirstTimeUpdateRequest): ApiResponse<String> {
        return customerApiService.updateFirstTimeProfile(request)
    }

    suspend fun updateMyProfile(request: CustomerUpdateProfileRequestDTO): ApiResponse<String> {
        return customerApiService.updateMyProfile(request)
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

