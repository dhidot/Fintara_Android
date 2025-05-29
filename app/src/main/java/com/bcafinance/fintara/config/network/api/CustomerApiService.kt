package com.bcafinance.fintara.config.network.api

import com.bcafinance.fintara.data.model.ApiResponse
import com.bcafinance.fintara.data.model.dto.customer.CustomerUpdateProfileRequestDTO
import com.bcafinance.fintara.data.model.dto.customer.DebtInfoResponse
import com.bcafinance.fintara.data.model.dto.customer.FirstTimeUpdateRequest
import com.bcafinance.fintara.data.model.dto.customer.UserWithCustomerResponse
import okhttp3.MultipartBody
import retrofit2.Response
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

    @PUT("api/v1/profilecustomer/update-my-profile")
    suspend fun updateMyProfile(
        @Body request: CustomerUpdateProfileRequestDTO
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

    @Multipart
    @POST("api/v1/profilecustomer/upload-photo")
    suspend fun uploadProfilePhoto(
        @Part file: MultipartBody.Part
    ): ApiResponse<String>
}

