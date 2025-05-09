package com.bcafinance.fintara.config.network

import com.bcafinance.fintara.data.model.ApiResponse
import com.bcafinance.fintara.data.model.dto.FirstTimeUpdateRequest
import com.bcafinance.fintara.data.model.dto.UserWithCustomerResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
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
}

