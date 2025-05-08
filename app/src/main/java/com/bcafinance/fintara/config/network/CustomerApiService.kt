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
    suspend fun getMyProfile(@Header("Authorization") token: String): ApiResponse<UserWithCustomerResponse>

    @Multipart
    @PUT("api/v1/profilecustomer/update")
    suspend fun submitFirstLoginDataWithFile(
        @Header("Authorization") token: String, // <-- tambahkan ini!
        @Part("textData") textData: RequestBody,
        @Part ktp: MultipartBody.Part,   // Pastikan parameter ini ada
        @Part selfie: MultipartBody.Part
    ): ApiResponse<String>

    @PUT("api/v1/profilecustomer/update")
    suspend fun submitFirstLoginData(
        @Header("Authorization") token: String,
        @Body data: FirstTimeUpdateRequest
    ): ApiResponse<String>

    @Multipart
    @POST("/api/v1/cloudinary/upload")
    suspend fun uploadPhotos(
        @Part ktpPhoto: MultipartBody.Part,
        @Part selfiePhoto: MultipartBody.Part
    ): Response<Map<String, String>>
}
