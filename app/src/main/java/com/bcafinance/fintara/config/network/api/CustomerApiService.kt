package com.bcafinance.fintara.config.network.api

import com.bcafinance.fintara.data.model.ApiResponse
import com.bcafinance.fintara.data.model.dto.auth.FirstTimeUpdateRequest
import com.bcafinance.fintara.data.model.dto.auth.UserWithCustomerResponse
import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part

interface CustomerApiService {
    @GET("api/v1/customer/me")
    suspend fun getMyProfile(): ApiResponse<UserWithCustomerResponse>

    @PUT("api/v1/profilecustomer/first-time_update")
    suspend fun updateFirstTimeProfile(
        @Body request: FirstTimeUpdateRequest
    ): ApiResponse<String>

    @Multipart
    @POST("api/v1/profilecustomer/upload-ktp")
    suspend fun uploadKtp(
        @Part file: MultipartBody.Part
    ): ApiResponse<String>

    @Multipart
    @POST("api/v1/profilecustomer/upload-selfie-ktp")
    suspend fun uploadSelfieKtp(
        @Part file: MultipartBody.Part
    ): ApiResponse<String>
}

