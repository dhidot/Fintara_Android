package com.bcafinance.fintara.data.repository

import android.content.Context
import com.bcafinance.fintara.config.network.CustomerApiService
import com.bcafinance.fintara.data.model.ApiResponse
import com.bcafinance.fintara.data.model.dto.FirstTimeUpdateRequest
import com.bcafinance.fintara.data.model.dto.UserWithCustomerResponse
import com.bcafinance.fintara.utils.SessionManager
import android.util.Log
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class CustomerRepository(private val customerApiService: CustomerApiService) {

    private val gson = Gson()

    suspend fun getProfile(token: String): ApiResponse<UserWithCustomerResponse> {
        return customerApiService.getMyProfile("Bearer $token")
    }

    suspend fun submitFirstLoginData(
        token: String,
        textData: FirstTimeUpdateRequest,
        ktpFile: File,
        selfieFile: File
    ): Result<String> {
        return try {
            // Step 1: Upload photos ke backend
            val uploadResult = uploadPhotosToBackend(ktpFile, selfieFile)

            if (uploadResult.isSuccess) {
                val uploadResponse = uploadResult.getOrNull()
                val ktpUrl = uploadResponse?.get("ktpPhotoUrl")
                val selfieUrl = uploadResponse?.get("selfiePhotoUrl")

                // Step 2: Gabungkan dengan data lain (termasuk foto URLs)
                val updatedRequest = textData.copy(
                    ktpPhotoUrl = ktpUrl.orEmpty(),  // URL KTP
                    selfiePhotoUrl = selfieUrl.orEmpty()  // URL Selfie
                )

                // Step 3: Kirim ke backend semua data termasuk teks dan foto URL
                val response = customerApiService.submitFirstLoginData(
                    token = "Bearer $token",
                    data = updatedRequest // Ini sudah termasuk semua data
                )

                val message = response.getFormattedMessages()
                if (response.status == 200) {
                    Result.success(message)
                } else {
                    Result.failure(Exception(message))
                }
            } else {
                Result.failure(Exception("Upload failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun uploadPhotosToBackend(ktpFile: File, selfieFile: File): Result<Map<String, String>> {
        return try {
            // Prepare the request body for both KTP and selfie photos
            val ktpRequestBody = ktpFile.asRequestBody("image/*".toMediaType())
            val selfieRequestBody = selfieFile.asRequestBody("image/*".toMediaType())

            // Create MultipartBody.Part for both photos
            val ktpPart = MultipartBody.Part.createFormData("ktpPhoto", ktpFile.name, ktpRequestBody)
            val selfiePart = MultipartBody.Part.createFormData("selfiePhoto", selfieFile.name, selfieRequestBody)

            // Make the upload request
            val response = customerApiService.uploadPhotos(ktpPart, selfiePart)

            // Log the response to check if it is successful
            if (response.isSuccessful && response.body() != null) {
                Log.d("UploadSuccess", "Upload successful: ${response.body()}")
                Result.success(response.body()!!)
            } else {
                // Log error if response is not successful
                Log.e("UploadError", "Upload failed with status code: ${response.code()} - ${response.message()}")
                Log.e("UploadError", "Response body: ${response.errorBody()?.string()}")
                Result.failure(Exception("Upload failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            // Log the exception if any error occurs
            Log.e("UploadException", "Error during upload: ${e.localizedMessage}", e)
            Result.failure(e)
        }
    }

}

