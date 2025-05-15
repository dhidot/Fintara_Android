package com.bcafinance.fintara.config.network.api

import com.bcafinance.fintara.data.model.ApiResponse
import com.bcafinance.fintara.data.model.dto.auth.LoginRequest
import com.bcafinance.fintara.data.model.dto.auth.LoginResponse
import com.bcafinance.fintara.data.model.dto.auth.RegisterCustomerResponse
import com.bcafinance.fintara.data.model.dto.auth.RegisterRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface AuthApiService {
    @POST("/api/v1/auth/register/customer")
    suspend fun registerUser(@Body request: RegisterRequest): Response<RegisterCustomerResponse>

    @POST("/api/v1/auth/login-customer")
    suspend fun loginUser(@Body loginRequest: LoginRequest): Response<LoginResponse>

    @POST("/api/v1/auth/login-google")
    suspend fun loginWithGoogle(
        @Header("Authorization") idToken: String
    ): Response<LoginResponse>

    @POST("/api/v1/auth/logout")
    suspend fun logout(): Response<ApiResponse<String>>
}
